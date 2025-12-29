package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectJDBC {
    private static String url = "jdbc:sqlserver://LAPTOP-RG4L7AKF:1433;databaseName=QLKhachSan;encrypt=true;trustServerCertificate=true";
    private static String user = "administrator";
    private static String pass = "1";
    public static Connection getConnection(){
        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void closeConnection(Connection conn){
        if(conn!=null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
