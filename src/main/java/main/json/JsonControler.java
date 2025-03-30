package main.json;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

public class JsonControler {
    public static List<String> get_ip_list(Gson gson){
        try (Reader reader = new FileReader("json/file/IpIndex.json")){
            Type listType = new TypeToken<List<String>>(){}.getType();
            return gson.fromJson(reader, listType);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static int[] get_oids(Gson gson){
        try (Reader reader = new FileReader("json/file/OidsIndex.json")){
            Type listType = new TypeToken<List<Integer>>(){}.getType();
            return gson.fromJson(reader, listType);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String[] get_oids_list(Gson gson){
        try (Reader reader = new FileReader("json/file/ListOfoids_number")){
            Type listType = new TypeToken<List<String>>(){}.getType();
            return gson.fromJson(reader, listType);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
