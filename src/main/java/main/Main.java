package main;


import main.PDUContorler.PDUget;
import main.json.JsonControler;
import org.snmp4j.smi.GenericAddress;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;


public class Main {

    public static void main(String[] args) throws InterruptedException {
        Gson gson = new Gson();
        String path = Paths.get("").toAbsolutePath().toString();

        System_l.init(path); // Checking the system

        UDPControler udpControler = new UDPControler();
        new Thread(() -> {
            try {
                udpControler.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        // Wait until UDPControler initialization is complete
        udpControler.awaitInitialization();



        new Thread(() -> {
            while (true) {
                for (String address : Objects.requireNonNull(JsonControler.get_ip_list(gson, path))) {
                    try {
                        PDUget.get(GenericAddress.parse(address), udpControler.getSnmp(), Objects.requireNonNull(JsonControler.get_oids(gson, path)), "public", gson, path);
                        Thread.sleep(1000);
                    } catch (IOException e) {
                        System.err.println("Error sending GET to:" + address);
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }).start();


    }
}