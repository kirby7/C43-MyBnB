import Objects.User;
import Services.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.sql.*;

public class Main {

    private static final String dbClassName = "com.mysql.cj.jdbc.Driver";
    private static final String CONNECTION = "jdbc:mysql://127.0.0.1/mydb";
    private static final String USER = "root";
    private static final String PASS = "";

    public static void main(String[] args) throws ClassNotFoundException {
        // Register JDBC driver
        Class.forName(dbClassName);
        // Database credentials
        System.out.println("Connecting to database...");

        try {
            User u = null; // Stores the currently logged-in user

            // Establish connection
            Connection conn = DriverManager.getConnection(CONNECTION,USER,PASS);
            System.out.println("Successfully connected to database");

            // Read user input
            BufferedReader r = new BufferedReader((new InputStreamReader((System.in))));
            System.out.println("Enter a command or input help for a list of commands:");
            for (String s = r.readLine(); s != null; s = r.readLine()) {
                CommandService.Command c = CommandService.commandLookup(s);

                switch (c) {
                    case EXIT:
                        exit(conn);
                        r.close();
                        return;
                    case HELP:
                        System.out.printf("List of supported commands:\n" +
                                "   help        Displays the list of commands\n" +
                                "\n" +
                                "   register    Creates a new user account\n" +
                                "   login       Logs in to an existing user account\n" +
                                "   logout      Logs out of the currently logged-in account\n" +
                                "   account     Edit account details or delete the current account\n" +
                                "\n" +
                                "   hosting     Manage properties listed by the current account\n" +
                                "\n" +
                                "   book        Book a listing for a certain date\n" +
                                "\n" +
                                "   setup       Creates new tables for an empty database\n" +
                                "   exit        (Alias: quit) Exits the program\n");
                        break;
                    case SETUP: // setup tables from an empty database
                        TableService.createTables(conn);
                        break;
                    case REGISTER: // create a new user account
                        if (u != null) {
                            System.out.println("You are currently logged in to an account");
                        }
                        else {
                            String newUser = "", newPw = "";

                            System.out.println("Input a username for your new account:");
                            newUser = r.readLine();
                            System.out.println("Input a password for your new account:");
                            newPw = r.readLine();

                            u = UserService.createUser(r, conn, newUser, newPw);
                            if (u != null)
                                System.out.printf("Now logged in as %s\n", u.getUsername());
                            else
                                System.out.printf("Failed to create account with name %s\n", newUser);
                        }
                        break;
                    case LOGIN: // log in to a user account
                        if (u != null) {
                            System.out.println("You are currently logged in to an account");
                        }
                        else {
                            String user = "", pw = "";

                            System.out.println("Input account username:");
                            user = r.readLine();
                            System.out.printf("Input password for %s\n", user);
                            pw = r.readLine();

                            u = UserService.login(conn, user, pw);
                            if (u != null)
                                System.out.printf("Now logged in as %s\n", u.getUsername());
                            else
                                System.out.printf("Failed to log in to account with name %s\n", user);
                        }
                        break;
                    case LOGOUT: // log out of the current account
                        System.out.printf("Logging out of account %s\n", u.getUsername());
                        u = null;
                    case ACCOUNT: // account management
                        if (u == null)
                            System.out.println("You are not logged in to an account");
                        else
                            u = UserService.accountDashboard(r, conn, u);
                        break;
                    case HOSTING: // host management
                        if (u == null)
                            System.out.println("You are not logged in to an account");
                        else
                            ListingService.hostDashboard(r, conn, u);
                        break;
                    case BOOK: // book a listing
                        if (u == null)
                            System.out.println("You are not logged in to an account");
                        else
                            BookingService.createBooking(r, conn, u);
                        break;
                    default:
                        System.out.println("Invalid command");
                }
                System.out.println("Enter a command or input help for a list of commands:");
            }
            r.close();
        }
        catch (IOException e) {
            System.err.println("IO error occurred");
        }
        catch (SQLException e) {
            System.err.println("Connection error occurred");
        }
    }

    private static void exit(Connection conn) throws SQLException {
        // Close connection
        System.out.println("Closing connection...");
        conn.close();
    }
}