package Services;

import java.sql.*;

public class TableService {
    // Setup all tables in the MyBnB database.
    public static void createTables(Connection conn) {
        createAddressTable(conn);
        createUserTable(conn);
        createListingTable(conn);
        createAmenityTable(conn);
        createBookingOptionTable(conn);
        createAccessibilityOptionTable(conn);
        createListDateTable(conn);
        createBookingTable(conn);
        createPaymentInfoTable(conn);
        createReviewTable(conn);
        System.out.println("Completed");
    }

    private static void createAddressTable(Connection conn) {
        System.out.println("Creating address table...");
        try {
            Statement stmt = conn.createStatement();
            String query = String.format("CREATE TABLE IF NOT EXISTS address (" +
                    "aid INT NOT NULL AUTO_INCREMENT, " +
                    "address VARCHAR(255) DEFAULT '123 Sample St.', " +
                    "city VARCHAR(255) DEFAULT 'Toronto', " +
                    "country VARCHAR(255) DEFAULT 'Canada', " +
                    "postal_code CHAR(7), " +
                    "PRIMARY KEY (aid));");
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Connection error occurred");
        }
    }

    private static void createUserTable(Connection conn) {
        System.out.println("Creating user table...");
        try {
            Statement stmt = conn.createStatement();
            String query = String.format("CREATE TABLE IF NOT EXISTS user (" +
                    "username VARCHAR(255) NOT NULL, " +
                    "first_name VARCHAR(255), " +
                    "last_name VARCHAR(255), " +
                    "dob DATE, " +
                    "sin CHAR(9), " +
                    "occupation VARCHAR(255), " +
                    "aid INT, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "PRIMARY KEY (username), " +
                    "INDEX address_idx (aid), " +
                    "FOREIGN KEY (aid) REFERENCES address(aid));");
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Connection error occurred");
        }
    }

    private static void createListingTable(Connection conn) {
        System.out.println("Creating listing table...");
        try {
            Statement stmt = conn.createStatement();
            String query = String.format("CREATE TABLE IF NOT EXISTS listing (" +
                    "lid INT NOT NULL AUTO_INCREMENT, " +
                    "listing_type INT DEFAULT 0, " +
                    "latitude REAL, " +
                    "longitude REAL, " +
                    "aid INT, " +
                    "username VARCHAR(255), " +
                    "PRIMARY KEY (lid), " +
                    "INDEX address_idx (aid), " +
                    "INDEX user_idx (username), " +
                    "FOREIGN KEY (aid) REFERENCES address(aid), " +
                    "FOREIGN KEY (username) REFERENCES user(username), " +
                    "CHECK (listing_type >= 0 AND listing_type <= 3));");
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Connection error occurred");
        }
    }

    private static void createAmenityTable(Connection conn) {
        System.out.println("Creating amenity table...");
        try {
            Statement stmt = conn.createStatement();
            String query = String.format("CREATE TABLE IF NOT EXISTS amenity (" +
                    "amenity_name VARCHAR(255) NOT NULL, " +
                    "category VARCHAR(255) NOT NULL, " +
                    "lid INT NOT NULL, " +
                    "PRIMARY KEY (amenity_name, lid), " +
                    "INDEX list_idx (lid), " +
                    "FOREIGN KEY (lid) REFERENCES listing(lid));");
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Connection error occurred");
        }
    }

    private static void createBookingOptionTable(Connection conn) {
        System.out.println("Creating booking option table...");
        try {
            Statement stmt = conn.createStatement();
            String query = String.format("CREATE TABLE IF NOT EXISTS booking_option (" +
                    "booking_option_name VARCHAR(255) NOT NULL, " +
                    "lid INT NOT NULL, " +
                    "PRIMARY KEY (booking_option_name, lid), " +
                    "INDEX list_idx (lid), " +
                    "FOREIGN KEY (lid) REFERENCES listing(lid));");
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Connection error occurred");
        }
    }

    private static void createAccessibilityOptionTable(Connection conn) {
        System.out.println("Creating accessibility option table...");
        try {
            Statement stmt = conn.createStatement();
            String query = String.format("CREATE TABLE IF NOT EXISTS accessibility_option (" +
                    "accessibility_option_name VARCHAR(255) NOT NULL, " +
                    "category VARCHAR(255) NOT NULL, " +
                    "lid INT, " +
                    "PRIMARY KEY (accessibility_option_name, lid), " +
                    "INDEX list_idx (lid), " +
                    "FOREIGN KEY (lid) REFERENCES listing(lid));");
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Connection error occurred");
        }
    }

    private static void createListDateTable(Connection conn) {
        System.out.println("Creating list date table...");
        try {
            Statement stmt = conn.createStatement();
            String query = String.format("CREATE TABLE IF NOT EXISTS list_date (" +
                    "lid INT NOT NULL, " +
                    "list_date DATE NOT NULL, " +
                    "price REAL DEFAULT 0.00, " +
                    "PRIMARY KEY (lid, list_date), " +
                    "INDEX list_idx (lid), " +
                    "FOREIGN KEY (lid) REFERENCES listing(lid));");
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Connection error occurred");
        }
    }

    private static void createBookingTable(Connection conn) {
        System.out.println("Creating booking table...");
        try {
            Statement stmt = conn.createStatement();
            String query = String.format("CREATE TABLE IF NOT EXISTS booking (" +
                    "lid INT NOT NULL, " +
                    "book_date DATE NOT NULL, " +
                    "username VARCHAR(255) NOT NULL, " +
                    "cancelled INT DEFAULT 0," +
                    "PRIMARY KEY (lid, book_date, username), " +
                    "INDEX list_date_idx (lid, book_date), " +
                    "INDEX user_idx (username), " +
                    "FOREIGN KEY (lid, book_date) REFERENCES list_date(lid, list_date), " +
                    "FOREIGN KEY (username) REFERENCES user(username), " +
                    "CHECK (cancelled = 0 OR cancelled = 1));");
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Connection error occurred");
        }
    }

    private static void createPaymentInfoTable(Connection conn) {
        System.out.println("Creating payment info table...");
        try {
            Statement stmt = conn.createStatement();
            String query = String.format("CREATE TABLE IF NOT EXISTS payment_info (" +
                    "cc_number CHAR(16) NOT NULL, " +
                    "security_code VARCHAR(4) NOT NULL, " +
                    "exp_date DATE NOT NULL, " +
                    "cardholder VARCHAR(255) NOT NULL, " +
                    "username VARCHAR(255) NOT NULL," +
                    "PRIMARY KEY (username), " +
                    "INDEX user_idx (username), " +
                    "FOREIGN KEY (username) REFERENCES user(username));");
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Connection error occurred");
        }
    }

    private static void createReviewTable(Connection conn) {
        System.out.println("Creating review table...");
        try {
            Statement stmt = conn.createStatement();
            String query = String.format("CREATE TABLE IF NOT EXISTS review (" +
                    "lid INT NOT NULL, " +
                    "book_date DATE NOT NULL, " +
                    "username VARCHAR(255) NOT NULL, " +
                    "contents TEXT, " +
                    "rating INT, " +
                    "review_type INT NOT NULL, " +
                    "PRIMARY KEY (lid, book_date, username, review_type), " +
                    "INDEX booking_idx (lid, book_date, username), " +
                    "FOREIGN KEY (lid, book_date, username) REFERENCES booking(lid, book_date, username), " +
                    "CHECK (review_type = 1 OR review_type = 2));");
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Connection error occurred");
        }
    }
}
