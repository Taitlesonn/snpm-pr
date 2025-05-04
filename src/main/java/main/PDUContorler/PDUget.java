package main.PDUContorler;

import com.google.gson.Gson;
import main.System_l;
import main.json.JsonControler;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PDUget {

    /**
     * Wysyła zapytania SNMP GET w partiach po заданej liczbie OID-ów
     * @param address adres SNMP agent
     * @param snmp instancja Snmp
     * @param indices tablica indeksów OID-ów do pobrania
     * @param communityString wspólnota SNMP
     * @param gson instancja Gson
     * @param path ścieżka do pliku JSON z listą OID-ów
     * @param batchSize liczba OID-ów w jednym zapytaniu
     */
    public static void get(Address address,
                           Snmp snmp,
                           int[] indices,
                           String communityString,
                           Gson gson,
                           String path,
                           int batchSize
    ) throws IOException {
        // Przygotowanie targeta SNMP
        CommunityTarget<Address> target = createTarget(address, communityString);

        // Wczytanie listy OID-ów z JSON-a
        String[] oids = JsonControler.get_oids_list(gson, path);
        if (oids == null || indices.length == 0) {
            System.err.println("Brak OID-ów do przetworzenia");
            return;
        }

        // Tworzymy listę PDU podzielonych na partie
        List<PDU> pduList = chunkOidsIntoPdus(oids, indices, batchSize);

        // Wysyłamy każde zapytanie i obsługujemy odpowiedź
        for (PDU pdu : pduList) {
            pdu.setType(PDU.GET);
            try {
                ResponseEvent event = snmp.send(pdu, target);
                if (event != null && event.getResponse() != null) {
                    for (VariableBinding vb : event.getResponse().getVariableBindings()) {
                        if (new String(String.valueOf(vb.getVariable())).equals("noSuchObject")){
                            LocalDateTime now = LocalDateTime.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            String data = now.format(formatter);

                            if (System_l.get_t()){
                                File log = new File(path+ "\\src\\main\\java\\main\\json\\logs\\log.log");

                                try (FileWriter writer = new FileWriter(log, true)){
                                    writer.write(data+ ": Not a object" + address + "\n");
                                }catch (IOException e2){
                                    e2.printStackTrace();
                                }
                            }else{
                                File log = new File(path + "/src/main/java/main/json/logs/log.log");

                                try (FileWriter writer = new FileWriter(log, true)){
                                    writer.write(data+ ": Not a object" + address + "\n");
                                }catch (IOException e2){
                                    e2.printStackTrace();
                                }
                            }
                        }

                        System.out.println(vb.getOid() + " = " + vb.getVariable());
                    }
                } else {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String data = now.format(formatter);
                    if (System_l.get_t()){
                        File log = new File(path+ "\\src\\main\\java\\main\\json\\logs\\log.log");

                        try (FileWriter writer = new FileWriter(log, true)){
                            writer.write(data+ ": Brak odpowiedzi od agenta SNMP: " + address + "\n");
                        }catch (IOException e2){
                            e2.printStackTrace();
                        }
                    }else{
                        File log = new File(path + "/src/main/java/main/json/logs/log.log");

                        try (FileWriter writer = new FileWriter(log, true)){
                            writer.write(data+ ": Brak odpowiedzi od agenta SNMP: " + address + "\n");
                        }catch (IOException e2){
                            e2.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String data = now.format(formatter);

                if (System_l.get_t()){
                    File log = new File(path+ "\\src\\main\\java\\main\\json\\logs\\log.log");

                    try (FileWriter writer = new FileWriter(log, true)){
                        writer.write(data+ ": Błąd podczas wysyłania PDU: " + e.getMessage() + "\n");
                    }catch (IOException e2){
                        e2.printStackTrace();
                    }
                }else{
                    File log = new File(path + "/src/main/java/main/json/logs/log.log");

                    try (FileWriter writer = new FileWriter(log, true)){
                        writer.write(data+ ": Błąd podczas wysyłania PDU: " + e.getMessage() + "\n");
                    }catch (IOException e2){
                        e2.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Dzieli listę OID-ów na PDU o określonym rozmiarze
     */
    private static List<PDU> chunkOidsIntoPdus(String[] oids, int[] indices, int batchSize) {
        List<PDU> pduList = new ArrayList<>();
        PDU currentPdu = new PDU();
        int count = 0;
        for (int idx : indices) {
            if (idx < 0 || idx >= oids.length) {
                System.err.println("Indeks OID poza zakresem: " + idx);
                continue;
            }
            currentPdu.add(new VariableBinding(new OID(oids[idx])));
            count++;
            if (count >= batchSize) {
                pduList.add(currentPdu);
                currentPdu = new PDU();
                count = 0;
            }
        }
        // Dodajemy ostatni PDU, jeśli zawiera zmienne
        if (currentPdu.getVariableBindings().size() > 0) {
            pduList.add(currentPdu);
        }
        return pduList;
    }

    /**
     * Tworzy CommunityTarget na podstawie adresu i wspólnoty
     */
    private static CommunityTarget<Address> createTarget(Address address, String community) {
        CommunityTarget<Address> target = new CommunityTarget<>();
        target.setAddress(address);
        target.setCommunity(new OctetString(community));
        target.setRetries(2);
        target.setTimeout(2000);
        target.setVersion(SnmpConstants.version2c);
        return target;
    }
}
