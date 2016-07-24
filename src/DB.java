import java.sql.*;

/**
 * Created by Pinanny on 21.07.2016.
 */
public class DB {

    public String res;
    private Connection conn;

    //Connecting to a Database using JDBC
    public void connection() {
        try {
            Class.forName("org.h2.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:h2:~/users",
                    "sa", "");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Get user by id
    public String getUserByID(int id) {
        try {
            connection();

            Statement st = null;
            st = conn.createStatement();

            ResultSet result;
            result = st.executeQuery("SELECT * FROM TEST WHERE id = " + id);
            while (result.next()) {
                String name = result.getString("NAME");
                String surname = result.getString("SURNAME");

                res = result.getString("ID") + " " + name + " " + surname;
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    //Insert new user to the database
    public String saveNewUser(String name, String surname) {
        try {
            connection();

            String q = "insert into TEST(name, surname) values(?, ?)";

            PreparedStatement st1 = null;
            st1 = conn.prepareStatement(q);
            st1.setString(1, name);
            st1.setString(2, surname);
            st1.execute();

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}