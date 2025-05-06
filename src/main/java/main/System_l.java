package main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class System_l {
    private static boolean type;
    private static final String url = "jdbc:postgresql://localhost:5432/mib_db";
    private static final String user = "mib_user";
    private static final String password = "ZAQ!2wsx";
    private static Connection connection;

    public static void init(String path){
        //os
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            System_l.type = true;
        }else if(os.contains("nix") || os.contains("nux") || os.contains("mac")){
            System_l.type = false;
        }else{System.out.println("Can't run hear");
            System.exit(1);
        }
        //DB
        try {
            System_l.connection = DriverManager.getConnection(System_l.url, System_l.user, System_l.password);
            System.out.println("DataBes connected suces");
        }catch (SQLException e){
            e.printStackTrace();
            System.exit(1);
        }

        try {
            if (System_l.get_t()){
                Runtime.getRuntime().exec("psql -U mib_user -d mib_db -f " + path + "\\src\\main\\java\\main\\sql\\mib_init.sql");
            }else {
                Runtime.getRuntime().exec("psql -U mib_user -d mib_db -f " + path + "/src/main/java/main/sql/mib_init.sql");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Wstawia nowy wpis do tabeli mib_objects.
     *
     * @param ipAddress IPv4 urządzenia (np. "192.168.1.1")
     * @param oid        OID SNMP (np. "1.3.6.1.2.1.1.1.0")
     * @param val        Wartość jako tekst (String)
     * @param comment    Komentarz/opis
     * @throws SQLException gdy wystąpi błąd SQL
     */
    public static void insertMibLog(String ipAddress, String oid, String val, String comment) throws SQLException {
        String sql = """
            INSERT INTO mib_objects (ip_address, oid, val, comment)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (ip_address, oid) DO NOTHING
            """;

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, ipAddress);
            pst.setString(2, oid);
            pst.setString(3, val);
            pst.setString(4, comment);

            int affected = pst.executeUpdate();
//            if (affected > 0) {
//                System.out.printf("Wpis dodany: %s / %s%n", ipAddress, oid);
//            } else {
//                System.out.printf("Wpis już istnieje: %s / %s%n", ipAddress, oid);
//            }
        }
    }

    public static Connection getConnection(){
        return System_l.connection;
    }

    public static boolean get_t(){
        return System_l.type;
    }
}
