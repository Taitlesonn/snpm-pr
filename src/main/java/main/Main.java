package main;


import main.PDUContorler.PDUget;
import org.snmp4j.smi.GenericAddress;

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

        //Lista adresów puki co bez bazy danych adresów
        List<String> snmpAddress = Arrays.asList(
                "udp:10.10.10.1/161",
                "udp:10.10.10.2/161"
        );

        int[] oidsToFetch = {0, 1, 2, 3, 4, 5, 6};


        new Thread(() -> {
            while (true) {
                for (String address : snmpAddress) {
                    try {
                        PDUget.get(GenericAddress.parse(address), udpControler.getSnmp(), oidsToFetch);
                    } catch (IOException e) {
                        System.err.println("Error sending GET to:" + address);
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
}