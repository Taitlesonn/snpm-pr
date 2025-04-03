package main;

public class System_l {
    private static boolean type;


    public static void init(){
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            System_l.type = true;
        }else if(os.contains("nix") || os.contains("nux") || os.contains("mac")){
            System_l.type = false;
        }else{
            System.out.println("Can't run hear");
            System.exit(1);
        }
    }

    public static boolean get_t(){
        return System_l.type;
    }
}
