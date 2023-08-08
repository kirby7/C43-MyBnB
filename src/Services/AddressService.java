package Services;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;

import Objects.*;

public class AddressService {
    public static Address createAddress(Connection conn, String address, String city, String country,
                                        String postalCode) throws SQLException {
        Address a = null;

        PreparedStatement stmt = conn.prepareStatement("SELECT EXISTS (SELECT * FROM address " +
                "WHERE address=? AND city=? AND country=? AND postal_code=?);");

        // Use identical address if it already exists
        stmt.setString(1, address);
        stmt.setString(2, city);
        stmt.setString(3, country);
        stmt.setString(4, postalCode);
        ResultSet rs = stmt.executeQuery();

        if (rs.next() && rs.getInt(1) == 0) {
            // Add new row to user table
            stmt = conn.prepareStatement("INSERT INTO address (address, city, country, postal_code)" +
                    "VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, address);
            stmt.setString(2, city);
            stmt.setString(3, country);
            stmt.setString(4, postalCode);
            stmt.executeUpdate();
        }
        // Get AID from new entry and set it for object a (can't use generated keys since row may not be new)
        stmt = conn.prepareStatement("SELECT aid FROM address " +
                "WHERE address=? AND city=? AND country=? AND postal_code=?;");
        stmt.setString(1, address);
        stmt.setString(2, city);
        stmt.setString(3, country);
        stmt.setString(4, postalCode);
        rs = stmt.executeQuery();
        if (rs.next()) {
            a = new Address(rs.getInt("aid"), address, city, country, postalCode);
        }

        stmt.close();

        return a;
    }

    public static Address getAddressFromAid(Connection conn, int aid) throws SQLException {
        Address a = null;
        // Get aid from new entry and set it for object a
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM address WHERE aid = ?;");
        stmt.setInt(1, aid);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            a = new Address(aid, rs.getString("address"), rs.getString("city"),
                    rs.getString("country"), rs.getString("postal_code"));
        }
        else
            System.out.printf("Could not find address with aid %d\n", aid);

        stmt.close();

        return a;
    }

    public static Address editAddress(BufferedReader r, Connection conn, Address a) throws IOException, SQLException {
        String t = r.readLine();
        if (!t.equals("")) {
            String addressName = t;

            System.out.printf("Input the name of the city where your address is located:\n");
            String city = r.readLine();

            System.out.printf("Input the name of the country where your address is located:\n");
            String country = r.readLine();

            System.out.printf("Input the postal code of your address, with format X1X 1X1:\n");
            while (true) {
                String postalCode = r.readLine();
                if (!postalCode.matches("^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$"))
                    System.out.println("Input does not match valid postal code format");
                else {
                    return AddressService.createAddress(conn, addressName, city, country, postalCode);
                }
            }
        }
        return a;
    }
}
