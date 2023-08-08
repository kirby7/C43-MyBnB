package Services;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;

import Objects.*;

import static Services.UserService.getUserFromUsername;

public class ListingService {
    public static void hostDashboard(BufferedReader r, Connection conn, User u) {
        try {
            Integer listCount = 0;
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM listing WHERE username = ?;");
            stmt.setString(1, u.getUsername());
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                listCount = rs.getInt(1);

            System.out.printf("Hosting management for %s:\n" +
                    "# of listings: %d\n" +
                    "   create      Create a new listing\n" +
                    "   remove      Remove an existing listing\n" +
                    "   list        View all and manage individual owned listings\n" +
                    "   cancel      Exits from hosting management\n",
                    u.getUsername(), listCount);

            for (String s = r.readLine(); s != null; s = r.readLine()) {
                if (s.toLowerCase().equals("create")) {
                    createListing(r, conn, u);
                }
                else if (s.toLowerCase().equals("remove")) {
                    stmt = conn.prepareStatement("DELETE FROM listing WHERE lid = ?;");

                    System.out.println("Input the number in square brackets by the listing to remove it, " +
                            "or input cancel to return to hosting management:");
                    Listing[] listings = getListingsByUser(conn, u, listCount);
                    int i = 1;
                    for (Listing l : listings) {
                        if (l != null)
                            System.out.println("   [" + i + "] " + l.toString());
                        i++;
                    }
                    for (String t = r.readLine(); t != null; t = r.readLine()) {
                        if (t.equals("cancel"))
                            break;
                        try {
                            int index = Integer.parseInt(t);
                            if (index < 1 || index > listCount)
                                System.out.println("Input is not within range of displayed listings");
                            else {
                                Listing l = listings[index-1];
                                stmt.setInt(1, l.getLid());
                                stmt.executeUpdate();

                                // Refresh displayed list
                                System.out.println("Input the number in square brackets by the listing to remove it, " +
                                        "or input cancel to return to hosting management:");
                                listings = getListingsByUser(conn, u, listCount-1);
                                i = 1;
                                for (Listing k : listings) {
                                    if (k != null)
                                        System.out.println("   [" + i + "] " + k.toString());
                                    i++;
                                }
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("Input is not a numerical value");
                        }
                    }
                }
                else if (s.toLowerCase().equals("list")) {
                    listDashboard(r, conn, u, listCount);
                }
                else if (s.toLowerCase().equals("cancel")) {
                    stmt.close();
                    return;
                }
                else {
                    System.out.println("Invalid command");
                }

                stmt = conn.prepareStatement("SELECT COUNT(*) FROM listing WHERE username = ?;");
                stmt.setString(1, u.getUsername());
                rs = stmt.executeQuery();
                if (rs.next())
                    listCount = rs.getInt(1);

                System.out.printf("Hosting management for %s:\n" +
                                "# of listings: %d\n" +
                                "   create      Create a new listing\n" +
                                "   remove      Remove an existing listing\n" +
                                "   list        View all and manage individual owned listings\n" +
                                "   cancel      Exits from hosting management\n",
                        u.getUsername(), listCount);
            }
        } catch (SQLException e) {
            System.err.println("Connection error occurred");
        } catch (IOException e) {
            System.err.println("IO error occurred");
        }
    }

    public static void listDashboard(BufferedReader r, Connection conn, User u, Integer listCount) {
        try {
            System.out.println("Input the number in square brackets by the listing to edit it, " +
                    "or input cancel to return to hosting management:");
            Listing[] listings = getListingsByUser(conn, u, listCount);

            int i = 1;
            for (Listing l : listings) {
                System.out.println("   [" + i + "] " + l.toString());
                i++;
            }

            for (String s = r.readLine(); s != null; s = r.readLine()) {
                if (s.toLowerCase().equals("cancel")) {
                    return;
                }
                try {
                    int index = Integer.parseInt(s);
                    if (index < 1 || index > listCount)
                        System.out.println("Input is not within range of displayed listings");
                    else {
                        Listing l = listings[index-1];

                        System.out.printf("Selected listing: %s\n" +
                                "   edit        Change listing details\n" +
                                "   open        Open the selected listing for booking\n" +
                                "   cancel      Return to listing management\n",
                                l.toString());
                        Boolean validCommand = false;
                        while (!validCommand) {
                            s = r.readLine();
                            if (s.toLowerCase().equals("edit")) {
                                l = editListing(r, conn, l);
                            }
                            else if (s.toLowerCase().equals("open")) {
                                createListDate(r, conn, l);
                            }
                            else if (s.toLowerCase().equals("cancel")) {
                                break;
                            }
                            else {
                                System.out.println("Invalid command");
                            }

                            System.out.printf("Selected listing: %s\n" +
                                            "   edit        Change listing details\n" +
                                            "   open        Open the selected listing for booking\n" +
                                            "   cancel      Return to listing management\n",
                                    l.toString());
                        }
                    }
                }
                catch (NumberFormatException e) {
                    System.out.println("Input is not a numerical value");
                }

                System.out.println("Input the number in square brackets by the listing to edit it, " +
                        "or input cancel to return to hosting management:");
                listings = getListingsByUser(conn, u, listCount);

                i = 1;
                for (Listing l : listings) {
                    System.out.println("   [" + i + "] " + l.toString());
                    i++;
                }
            }

        } catch (SQLException e) {
            System.err.println("Connection error occurred");
        } catch (IOException e) {
            System.err.println("IO error occurred");
        }
    }

    public static Listing createListing(BufferedReader r, Connection conn, User u) throws SQLException, IOException {
        Listing l = null;

        System.out.printf("Creating new listing for user named %s...\n", u.getUsername());
        // Add new row to listing table
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO listing (username) VALUES (?);",
                Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, u.getUsername());
        stmt.executeUpdate();

        // Get LID of new listing
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            l = new Listing(rs.getInt(1), Listing.ListingType.HOUSE, null, null, null, u);
        }

        stmt.close();
        editListing(r, conn, l);

        return l;
    }

    public static Listing editListing(BufferedReader r, Connection conn, Listing l) throws SQLException, IOException {

        // Edit listing address
        PreparedStatement stmt = conn.prepareStatement("UPDATE listing SET aid = ? WHERE lid = ?;");
        Boolean validAddress = false;
        while (!validAddress) {
            if (l.getAddress() != null)
                System.out.printf("The current listing address is %s, in %s, %s. Input a new address, " +
                                "or leave blank if you wish to keep the current listing address:\n",
                        l.getAddress().getAddress(), l.getAddress().getCity(), l.getAddress().getCountry());
            else
                System.out.printf("The listing currently has no address set. Input an address:\n");
            l.setAddress(AddressService.editAddress(r, conn, l.getAddress()));
            if (l.getAddress() != null)
                validAddress = true;
        }
        // Update corresponding row in listing table
        stmt.setInt(1, l.getAddress().getAid());
        stmt.setInt(2, l.getLid());
        stmt.executeUpdate();

        stmt = conn.prepareStatement("UPDATE listing SET listing_type = ? WHERE lid = ?;");
        if (l.getListingType() != null)
            System.out.printf("The current listing type is %s. Input one of House, Apartment, Guesthouse, or Hotel, " +
                    "or leave blank if you wish to keep the current listing type:\n", l.getListingType().name());
        else
            System.out.println("The listing currently has no type. " +
                    "Input one of House, Apartment, Guesthouse, or Hotel.");
        Boolean validType = false;
        while (!validType) {
            String s = r.readLine();
            if (s.equalsIgnoreCase("house") || s.equalsIgnoreCase("apartment") ||
                    s.equalsIgnoreCase("guesthouse") || s.equalsIgnoreCase("hotel")) {
                validType = true;
                l.setListingType(Listing.ListingType.valueOf(s.toUpperCase()));

                // Update corresponding row in listing table
                stmt.setInt(1, l.getListingType().ordinal());
                stmt.setInt(2, l.getLid());
                stmt.executeUpdate();
            }
            else if (s.equals("") && l.getListingType() != null)
                validType = true;
            else
                System.out.println("Valid type must be one of House, Apartment, Guesthouse, or Hotel");
        }

        stmt = conn.prepareStatement("UPDATE listing SET latitude = ? WHERE lid = ?;");
        if (l.getLatitude() != null)
            System.out.printf("The current listing latitude is %s. Input a new latitude, " +
                    "or leave blank if you wish to keep the current latitude:\n", l.getLatitude());
        else
            System.out.println("The listing currently has no latitude. Input a new latitude.");
        Boolean validLatitude = false;
        while (!validLatitude) {
            String s = r.readLine();
            if (!s.matches("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?)$"))
                System.out.println("Input does not match valid latitude format (-90.0 to 90.0)");
            else if (s.equals("") && l.getLatitude() != null)
                validLatitude = true;
            else {
                validLatitude = true;
                l.setLatitude(Double.parseDouble(s));

                // Update corresponding row in listing table
                stmt.setDouble(1, l.getLatitude());
                stmt.setInt(2, l.getLid());
                stmt.executeUpdate();
            }
        }

        stmt = conn.prepareStatement("UPDATE listing SET longitude = ? WHERE lid = ?;");
        if (l.getLongitude() != null)
            System.out.printf("The current listing longitude is %s. Input a new longitude, " +
                    "or leave blank if you wish to keep the current longitude:\n", l.getLongitude());
        else
            System.out.println("The listing currently has no longitude. Input a new longitude.");
        Boolean validLongitude = false;
        while (!validLongitude) {
            String s = r.readLine();
            if (!s.matches("^[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$"))
                System.out.println("Input does not match valid longitude format (-180.0 to 180.0)");
            else if (s.equals("") && l.getLongitude() != null)
                validLongitude = true;
            else {
                validLongitude = true;
                l.setLongitude(Double.parseDouble(s));
                // Update corresponding row in listing table
                stmt.setDouble(1, l.getLongitude());
                stmt.setInt(2, l.getLid());
                stmt.executeUpdate();
            }
        }

        System.out.printf("Updated listing details:\n" +
                    "Type: %s\n" +
                    "Address: %s\n" +
                    "Coordinates: %f, %f\n",
                l.getListingType(), l.getAddress(), l.getLatitude(), l.getLongitude());

        stmt.close();
        return l;
    }

    public static Listing[] getListingsByUser(Connection conn, User u, Integer listCount) throws SQLException {
        Double[][] listingData = new Double[listCount][4];
        Listing[] out = new Listing[listCount];

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM listing WHERE username = ?;");
        stmt.setString(1, u.getUsername());
        ResultSet rs = stmt.executeQuery();

        // Record result set into a 2-D array
        int i = 0;
        while (rs.next()) {
            out[i] = new Listing(rs.getInt("lid"),
                    Listing.ListingType.values()[rs.getInt("listing_type")],
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude"),
                    AddressService.getAddressFromAid(conn, rs.getInt("aid")),
                    getUserFromUsername(conn, u.getUsername()));
            i++;
        }

        return out;
    }

    public static ListDate createListDate(BufferedReader r, Connection conn, Listing l) throws SQLException, IOException {
        ListDate ld = null;
        Date date = null;
        Double price = 0.00;
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO list_date (lid, list_date, price) " +
                "VALUES (?, ?, ?);");

        System.out.printf("Input a date to open the listing in format yyyy-mm-dd:\n");
        Boolean validDate = false;
        while (!validDate) {
            String s = r.readLine();
            if (!s.matches("^((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$"))
                System.out.println("Input does not match valid date format");
            else {
                validDate = true;
                date = Date.valueOf(s);
            }
        }

        System.out.printf("Input a price to book the listing on that date (in $CAD):\n");
        Boolean validPrice = false;
        while (!validPrice) {
            String s = r.readLine();
            if (!s.matches("^\\d+(.\\d{1,2})?$"))
                System.out.println("Input does not match valid pricing format");
            else {
                validPrice = true;
                price = Double.parseDouble(s);
            }
        }

        System.out.printf("Updating the listing for %s for booking...\n", l.toString());
        ld = new ListDate(l, date, price);

        // Update corresponding row in list_date table
        stmt.setInt(1, l.getLid());
        stmt.setDate(2, date);
        stmt.setDouble(3, price);
        stmt.executeUpdate();

        return ld;
    }
}
