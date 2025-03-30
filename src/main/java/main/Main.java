package main;


import main.PDUContorler.PDUget;
import main.json.JsonControler;
import org.snmp4j.smi.GenericAddress;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        UDPControler udpControler = new UDPControler();
        new Thread(() -> {
            try {
                udpControler.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();


        Gson gson = new Gson();

        new Thread(() -> {
            while (true) {
                for (String address : JsonControler.get_ip_list(gson)) {
                    try {
                        PDUget.get(GenericAddress.parse(address), udpControler.getSnmp(), JsonControler.get_oids(gson), "public", gson);
                    } catch (IOException e) {
                        System.err.println("Error sending GET to:" + address);
                        e.printStackTrace();
                    }
                }

            }
        }).start();


    }
}