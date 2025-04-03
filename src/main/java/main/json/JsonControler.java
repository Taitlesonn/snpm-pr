package main.json;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.PDUContorler.System_l;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class JsonControler {
    public static List<String> get_ip_list(Gson gson, String path){
        if (System_l.get_t()){
            try (Reader reader = new FileReader(path + "\\src\\main\\java\\main\\json\\file\\IpIndex.json")){
                Type listType = new TypeToken<List<String>>(){}.getType();
                return gson.fromJson(reader, listType);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            try (Reader reader = new FileReader(path + "/src/main/java/main/json/file/IpIndex.json")){
                Type listType = new TypeToken<List<String>>(){}.getType();
                return gson.fromJson(reader, listType);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static int[] get_oids(Gson gson, String path){
        if (System_l.get_t()){
            try (Reader reader = new FileReader(path + "\\src\\main\\java\\main\\json\\file\\OidsIndex.json")){
                Type listType = new TypeToken<List<Integer>>(){}.getType();
                 List<Integer> l = gson.fromJson(reader, listType);
                int[] intArray = Arrays.stream(l.toArray(new Integer[0]))
                        .mapToInt(Integer::intValue)
                        .toArray();
                return intArray;
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            try (Reader reader = new FileReader(path + "/src/main/java/main/json/file/OidsIndex.json")){
                Type listType = new TypeToken<List<Integer>>(){}.getType();
                List<Integer> l = gson.fromJson(reader, listType);
                int[] intArray = Arrays.stream(l.toArray(new Integer[0]))
                        .mapToInt(Integer::intValue)
                        .toArray();
                return intArray;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String[] get_oids_list(Gson gson, String path){
        if (System_l.get_t()){
            try (Reader reader = new FileReader(path + "\\src\\main\\java\\main\\json\\file\\ListOfoids_number.json")){
                Type listType = new TypeToken<List<String>>(){}.getType();
                List<String> list = gson.fromJson(reader, listType);
                return list.toArray(new String[0]);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            try (Reader reader = new FileReader(path + "/src/main/java/main/json/file/ListOfoids_number.json")){
                Type listType = new TypeToken<List<String>>(){}.getType();
                List<String> list = gson.fromJson(reader, listType);
                return list.toArray(new String[0]);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

}
