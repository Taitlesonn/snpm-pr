package main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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
        }else{
            System.out.println("Can't run hear");
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
                Runtime.getRuntime().exec("psql -U mib_user -d mib_db -f " + path + "sql\\mib_init.sql");
            }else {
                Runtime.getRuntime().exec("psql -U mib_user -d mib_db -f " + path + "sql/mib_init.sql");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection(){
        return System_l.connection;
    }

    public static boolean get_t(){
        return System_l.type;
    }
}
