package Services;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

import Objects.*;

public class UserService {
    public static User accountDashboard(BufferedReader r, Connection conn, User u) {
        String name = "", dob = "", occupation = "", address = "";
        if (u.getFirstName() != null)
            name = name.concat(u.getFirstName() + " ");
        if (u.getLastName() != null)
            name = name.concat(u.getLastName());
        if (u.getLocalDoB() != null)
            dob = u.getLocalDoB().toString();
        if (u.getOccupation() != null)
            occupation = u.getOccupation();
        if (u.getAddress() != null)
            address = u.getAddress().toString();

        System.out.printf("Account management for %s:\n" +
                        "Name: %s\n" +
                        "Date of Birth: %s\n" +
                        "Occupation: %s\n" +
                        "Address: %s\n" +
                        "   edit        Edit details such as name, address, DoB, occupation, and SIN\n" +
                        "   payment     Add or remove a credit card used for payment\n" +
                        "   delete      Deletes the current account\n" +
                        "   cancel      Exits from account management\n",
                u.getUsername(), name, dob, occupation, address);

        try {
            for (String s = r.readLine(); s != null; s = r.readLine()) {
                if (s.toLowerCase().equals("edit")) {
                    return editDetails(r, conn, u);
                }
                else if (s.toLowerCase().equals("payment")) {
                    editPayment(r, conn, u);
                }
                else if (s.toLowerCase().equals("delete")) {
                    System.out.printf("Are you sure you want to delete account with name %s? Input Y to confirm:\n",
                            u.getUsername());
                    s = r.readLine();
                    if (s.toLowerCase().equals("y")) {
                        System.out.printf("Deleting account %s...\n", u.getUsername());

                        PreparedStatement stmt = conn.prepareStatement("DELETE FROM user WHERE username = ?;");
                        stmt.setString(1, u.getUsername());
                        stmt.executeUpdate();

                        stmt.close();

                        return null;
                    }
                }
                else if (s.toLowerCase().equals("cancel")) {
                    return u;
                }
                else {
                    System.out.println("Invalid command");
                }
                System.out.printf("Account management for %s:\n" +
                                "Name: %s\n" +
                                "Date of Birth: %s\n" +
                                "Occupation: %s\n" +
                                "Address: %s\n" +
                                "   edit        Edit details such as name, address, DoB, occupation, and SIN\n" +
                                "   delete      Deletes the current account\n" +
                                "   cancel      Exits from account management\n",
                        u.getUsername(), name, dob, occupation, address);
            }
        } catch (SQLException e) {
            System.err.println("Connection error occurred");
        } catch (IOException e) {
            System.err.println("IO error occurred");
        }
        return u;
    }

    public static User createUser(BufferedReader r, Connection conn, String username, String password) {
        User u = new User(username, password);

        System.out.printf("Creating new user named %s...\n", username);
        try {
            // Check if user with that username exists
            PreparedStatement stmt = conn.prepareStatement("SELECT EXISTS (SELECT * FROM user WHERE username=?);");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 1) {
                System.out.printf("A user with the username '%s' already exists\n", username);
                return null;
            }

            // Add new row to user table
            stmt = conn.prepareStatement("INSERT INTO user (username, password) VALUES (?,?);");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            stmt.close();

            editDetails(r, conn, u);
        } catch (SQLException e) {
            System.err.println("Connection error occurred");
        } catch (IOException e) {
            System.err.println("IO error occurred");
        }

        return u;
    }

    public static User login(Connection conn, String username, String password) {
        User u = null;
        try {
            // Check if user with that username exists
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE username=? AND password = ?;");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                u = new User(rs.getString("username"), rs.getString("password"));
                u.setFirstName(rs.getString("first_name"));
                u.setLastName(rs.getString("last_name"));
                u.setDateOfBirth(rs.getDate("dob"));
                u.setSin(rs.getString("sin"));
                u.setOccupation(rs.getString("occupation"));
                u.setAddress(AddressService.getAddressFromAid(conn, rs.getInt("aid")));
                return u;
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Connection error occurred");
        }

        return null;
    }

    public static User getUserFromUsername(Connection conn, String username) {
        User u = null;
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT password FROM user WHERE username = ?;");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                u = login(conn, username, rs.getString("password"));
            }
            else
                System.out.printf("Could not find user with username %s\n", username);

            stmt.close();
        } catch (SQLException e) {
            System.err.println("Connection error occurred");
        }

        return u;
    }

    public static User editDetails(BufferedReader r, Connection conn, User u) throws SQLException, IOException {
        String name = "", dob = "", occupation = "", address = "";
        if (u.getFirstName() != null)
            name = name.concat(u.getFirstName() + " ");
        if (u.getLastName() != null)
            name = name.concat(u.getLastName());
        if (u.getLocalDoB() != null)
            dob = u.getLocalDoB().toString();
        if (u.getOccupation() != null)
            occupation = u.getOccupation();
        if (u.getAddress() != null)
            address = u.getAddress().toString();


        // Edit first name
        PreparedStatement stmt = conn.prepareStatement("UPDATE user SET first_name = ? WHERE username = ?;");
        System.out.printf("Your current first name is %s. Input a new first name, " +
                "or leave blank if you wish to keep your current first name:\n", u.getFirstName());
        String t =  r.readLine();
        if (!t.equals("")) {
            u.setFirstName(t);
            // Update corresponding row in user table
            stmt.setString(1, u.getFirstName());
            stmt.setString(2, u.getUsername());
            stmt.executeUpdate();
        }

        // Edit last name
        stmt = conn.prepareStatement("UPDATE user SET last_name = ? WHERE username = ?;");
        System.out.printf("Your current last name is %s. Input a new last name, " +
                "or leave blank if you wish to keep your current last name:\n", u.getLastName());
        t =  r.readLine();
        if (!t.equals("")) {
            u.setLastName(t);
            // Update corresponding row in user table
            stmt.setString(1, u.getLastName());
            stmt.setString(2, u.getUsername());
            stmt.executeUpdate();
        }

        // Edit date of birth
        stmt = conn.prepareStatement("UPDATE user SET dob = ? WHERE username = ?;");
        System.out.printf("Your current date of birth is %s. Input a new date in format yyyy-mm-dd, " +
                "or leave blank if you wish to keep your current date of birth:\n", u.getLocalDoB());
        Boolean validDob = false;
        while (!validDob) {
            t = r.readLine();
            if (t.equals(""))
                validDob = true;
            else if (!t.matches("^((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$"))
                System.out.println("Input does not match valid date format");
            else {
                validDob = true;
                u.setDateOfBirth(Date.valueOf(t));
                // Update corresponding row in user table
                stmt.setDate(1, u.getDateOfBirth());
                stmt.setString(2, u.getUsername());
                stmt.executeUpdate();
            }
        }

        // Edit address
        stmt = conn.prepareStatement("UPDATE user SET aid = ? WHERE username = ?;");
        if (u.getAddress() != null)
            System.out.printf("Your current address is %s, in %s, %s. Input a new address, " +
                            "or leave blank if you wish to keep your current address:\n",
                    u.getAddress().getAddress(), u.getAddress().getCity(), u.getAddress().getCountry());
        else
            System.out.printf("Your currently have no address set. Input an address, " +
                    "or leave blank if you wish to keep your current address:\n");
        u.setAddress(AddressService.editAddress(r, conn, u.getAddress()));
        // Update corresponding row in user table
        if (u.getAddress() != null) {
            stmt.setInt(1, u.getAddress().getAid());
            stmt.setString(2, u.getUsername());
            stmt.executeUpdate();
        }

        // Edit occupation
        stmt = conn.prepareStatement("UPDATE user SET occupation = ? WHERE username = ?;");
        System.out.printf("Your current occupation is %s. Input a new occupation, " +
                "or leave blank if you wish to keep your current occupation:\n", u.getOccupation());
        t = r.readLine();
        if (!t.equals("")) {
            u.setOccupation(t);
            // Update corresponding row in user table
            stmt.setString(1, u.getOccupation());
            stmt.setString(2, u.getUsername());
            stmt.executeUpdate();
        }

        // Edit social insurance number
        stmt = conn.prepareStatement("UPDATE user SET sin = ? WHERE username = ?;");
        if (u.hasSin())
            System.out.println("You already have a SIN attached to your account. Input a new SIN, " +
                    "or leave blank if you wish to keep your current SIN:");
        else
            System.out.println("You currently have no SIN attached to your account. Input a SIN, " +
                    "or leave blank if you do not want to attach a SIN:");
        Boolean validSin = false;
        while (!validSin) {
            t = r.readLine();
            if (t.equals(""))
                validSin = true;
            else {
                if (!validateSin(t))
                    System.out.println("Input does not match valid SIN format");
                else {
                    validSin = true;
                    u.setSin(t);
                    // Update corresponding row in user table
                    stmt.setString(1, t);
                    stmt.setString(2, u.getUsername());
                    stmt.executeUpdate();
                }
            }
        }

        name = "";
        if (u.getFirstName() != null)
            name = name.concat(u.getFirstName() + " ");
        if (u.getLastName() != null)
            name = name.concat(u.getLastName());
        if (u.getLocalDoB() != null)
            dob = u.getLocalDoB().toString();
        if (u.getOccupation() != null)
            occupation = u.getOccupation();
        if (u.getAddress() != null)
            address = u.getAddress().toString();

        System.out.printf("Updated account details for %s:\n" +
                        "Name: %s\n" +
                        "Date of Birth: %s\n" +
                        "Occupation: %s\n" +
                        "Address: %s\n",
                u.getUsername(), name, dob, occupation, address);

        stmt.close();
        return u;
    }

    public static void editPayment(BufferedReader r, Connection conn, User u) throws SQLException, IOException {
        PaymentInfo p = u.getPaymentInfo();
        if (p != null)
            System.out.printf("Current payment method for %s:\n" +
                    "Credit card starting with %s\n" +
                    "   edit        Remove the existing payment method and add a new one\n" +
                    "   cancel      Exits from payment management\n",
                    u.getUsername(), p.getCcNumber().substring(0,4));
        else
            System.out.printf("There is currently no payment method set for %s.\n" +
                    "   edit        Add a new credit card to use as payment method\n" +
                    "   cancel      Exits from payment management\n",
                    u.getUsername());
        for (String s = r.readLine(); s != null; s = r.readLine()) {
            if (s.toLowerCase().equals("edit")) {
                String t = "";

                String ccNumber = "";
                String securityCode = "";
                Date expDate = null;
                String cardholder = "";

                System.out.println("Input the 16-digit credit card number with no spaces:");
                Boolean validCC = false;
                while (!validCC) {
                    t = r.readLine();
                    if (!t.matches("^\\d{16}$"))
                        System.out.println("Input does not match valid credit card number format");
                    else {
                        validCC = true;
                        ccNumber = t;
                    }
                }

                System.out.println("Input the 3 or 4-digit security code, usually found on the back of the card:");
                Boolean validCSC = false;
                while (!validCSC) {
                    t = r.readLine();
                    if (!t.matches("^\\d{4}|\\d{3}$"))
                        System.out.println("Input does not match valid security code format");
                    else {
                        validCSC = true;
                        securityCode = t;
                    }
                }

                System.out.println("Input the expiration date of the card in format mm/yy:");
                Boolean validExpDate = false;
                while (!validExpDate) {
                    t = r.readLine();
                    if (!t.matches("^(0?[1-9]|1[012])\\/\\d{2}$"))
                        System.out.println("Input does not match valid date format");
                    else {
                        validExpDate = true;
                        String[] dateValues = t.split("/");
                        expDate = Date.valueOf("20" + dateValues[1] + "-" + dateValues[0] + "-1");
                    }
                }

                System.out.println("Input the name of the cardholder as printed on the card:");
                Boolean validCardholder = false;
                while (!validCardholder) {
                    t = r.readLine();
                    if (t.equals(""))
                        System.out.println("Cardholder name cannot be blank");
                    else {
                        validCardholder = true;
                        cardholder = t;
                    }
                }

                // Create PaymentInfo object
                p = new PaymentInfo(ccNumber, securityCode, expDate, cardholder, u);
                u.setPaymentInfo(p);

                // Perform SQL operations
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM payment_info WHERE username=?;");
                // Delete the existing payment info for the current user
                stmt.setString(1, u.getUsername());
                stmt.executeUpdate();

                // Add new row to user table
                stmt = conn.prepareStatement("INSERT INTO payment_info " +
                        "(cc_number, security_code, exp_date, cardholder, username) VALUES (?, ?, ?, ?, ?);");
                stmt.setString(1, ccNumber);
                stmt.setString(2, securityCode);
                stmt.setDate(3, expDate);
                stmt.setString(4, cardholder);
                stmt.setString(5, u.getUsername());
                stmt.executeUpdate();
                stmt.close();
            }
            else if (s.toLowerCase().equals("cancel")) {
                return;
            }
            else {
                System.out.println("Invalid command");
            }
            p = u.getPaymentInfo();
            if (p != null)
                System.out.printf("Current payment method for %s:\n" +
                                "Credit card starting with %s\n" +
                                "   edit        Remove the existing payment method and add a new one\n" +
                                "   cancel      Exits from payment management\n",
                        u.getUsername(), p.getCcNumber().substring(0,4));
            else
                System.out.printf("There is currently no payment method set for %s.\n" +
                                "   edit        Add a new credit card to use as payment method\n" +
                                "   cancel      Exits from payment management\n",
                        u.getUsername());
        }
    }

    private static Boolean validateSin(String sin) {
        if (sin.length() != 9)
            return false;
        Integer digit, sum = 0;
        // Run through the Luhn algorithm
        for (int i = 0; i < sin.length(); i++) {
            digit = Integer.parseInt(sin.substring(i, i+1));
            if (i % 2 == 0)
                sum += digit;
            else {
                digit = digit * 2;
                if (digit > 10)
                    sum += (1 + digit % 10); // (First digit = 1) + (Second digit = remainder)
                else
                    sum += digit;
            }
        }
        if (sum % 10 != 0)
            return false;
        return true;
    }
}
