package Services;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import Objects.*;

import static Services.UserService.getUserFromUsername;

public class BookingService {

    public static Booking createBooking(BufferedReader r, Connection conn, User u ) throws SQLException, IOException {
        Booking b = null;
        Double latitude = 0.0, longitude = 0.0, distance = 10.0;

        System.out.println("Input the latitude around which you wish to search for listings:");
        Boolean validLatitude = false;
        while (!validLatitude) {
            String s = r.readLine();
            if (!s.matches("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?)$"))
                System.out.println("Input does not match valid latitude format (-90.0 to 90.0)");
            else {
                validLatitude = true;
                latitude = Double.parseDouble(s);
            }
        }

        System.out.println("Input the longitude around which you wish to search for listings:");
        Boolean validLongitude = false;
        while (!validLongitude) {
            String s = r.readLine();
            if (!s.matches("^[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$"))
                System.out.println("Input does not match valid longitude format (-180.0 to 180.0)");
            else {
                validLongitude = true;
                longitude = Double.parseDouble(s);
            }
        }

        System.out.printf("Input the distance (in km) from (%f, %f) where you want to search for listings, " +
                        "or leave blank if you want to use the default (10 km):\n",
                latitude, longitude);
        Boolean validDistance = false;
        while (!validDistance) {
            String s = r.readLine();
            if (s.equals(""))
                validDistance = true;
            else {
                try {
                    distance = Double.parseDouble(s);
                    validDistance = true;
                }
                catch (NumberFormatException e) {
                    System.out.println("Input is not a numerical value");
                }
            }
        }

        // Calculate lat/longitude range based on given distance
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM listing WHERE " +
                "latitude >= ? AND latitude <= ? AND longitude >= ? AND longitude <= ? AND username != ?;");
        stmt.setDouble(1, latitude - Math.abs(distance)/110.574);
        stmt.setDouble(2, latitude + Math.abs(distance)/110.574);
        Double cos = Math.toRadians(latitude);
        stmt.setDouble(3, longitude - Math.abs(distance)/(cos*111.32));
        stmt.setDouble(4, longitude + Math.abs(distance)/(cos*111.32));
        stmt.setString(5, u.getUsername());

        // We use a list here since the number of results is not known
        ResultSet rs = stmt.executeQuery();
        ArrayList<Listing> listings = new ArrayList<>();
        while (rs.next()) {
            listings.add(new Listing(rs.getInt("lid"),
                    Listing.ListingType.values()[rs.getInt("listing_type")],
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude"),
                    AddressService.getAddressFromAid(conn, rs.getInt("aid")),
                    getUserFromUsername(conn, u.getUsername())));
        }

        // Print out listings for the user to pick from
        stmt = conn.prepareStatement("SELECT * from list_date WHERE lid = ?;");
        System.out.println("Input the number in square brackets by the listing to view it, " +
                "or input cancel to stop searching for listings:");
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
                if (index < 1 || index > i)
                    System.out.println("Input is not within range of displayed listings");
                else {
                    // Print out available dates for the user to pick from
                    Listing l = listings.get(index-1);
                    stmt.setInt(1, l.getLid());
                    rs = stmt.executeQuery();
                    ArrayList<ListDate> listDates = new ArrayList<>();
                    while (rs.next()) {
                        listDates.add(new ListDate(l, rs.getDate("list_date"), rs.getDouble("price")));
                    }

                    stmt = conn.prepareStatement("INSERT INTO booking (lid, book_date, username, cancelled) " +
                            "VALUES (?, ?, ?, 0);");
                    System.out.println("Input the number in square brackets by the date to book that date, " +
                            "or input cancel to select a different listing:");
                    int j = 1;
                    for (ListDate ld : listDates) {
                        if (ld != null)
                            System.out.println("   [" + j + "] " + ld.toString());
                        j++;
                    }
                    for (String k = r.readLine(); k != null; k = r.readLine()) {
                        if (k.equals("cancel"))
                            break;
                        try {
                            int ldIndex = Integer.parseInt(k);
                            if (ldIndex < 1 || ldIndex > i)
                                System.out.println("Input is not within range of displayed dates");
                            else {
                                b = new Booking(l, listDates.get(ldIndex-1).getDate(), u);
                                // Add new booking to booking table
                                stmt.setInt(1, b.getListing().getLid());
                                stmt.setDate(2, b.getDate());
                                stmt.setString(3, b.getUser().getUsername());
                                stmt.executeUpdate();

                                System.out.printf("Successfully booked %s on %s\n", b.getListing(), b.getDate());
                                stmt.close();
                                return b;
                            }
                        }
                        catch (NumberFormatException e) {
                            System.out.println("Input is not a numerical value");
                        }
                    }
                }
            }
            catch (NumberFormatException e) {
                System.out.println("Input is not a numerical value");
            }
        }

        stmt.close();
        return b;
    }
}
