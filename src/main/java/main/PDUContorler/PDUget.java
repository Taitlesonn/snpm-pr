package main.PDUContorler;

import com.google.gson.Gson;
import main.System_l;
import main.json.JsonControler;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PDUget {

    /**
     * Wysyła zapytania SNMP GET w partiach po заданej liczbie OID-ów
     */
    public static void get(Address address,
                           Snmp snmp,
                           int[] indices,
                           String communityString,
                           Gson gson,
                           String path,
                           int batchSize
    ) throws IOException {
        CommunityTarget<Address> target = createTarget(address, communityString);
        String[] oids = JsonControler.get_oids_list(gson, path);
        if (oids == null || indices.length == 0) {
            System.err.println("Brak OID-ów do przetworzenia");
            return;
        }

        List<PDU> pduList = chunkOidsIntoPdus(oids, indices, batchSize);

        for (PDU pdu : pduList) {
            pdu.setType(PDU.GET);
            try {
                ResponseEvent event = snmp.send(pdu, target);
                if (event != null && event.getResponse() != null) {
                    // iterujemy po odpowiedziach
                    for (VariableBinding vb : event.getResponse().getVariableBindings()) {
                        Variable var = vb.getVariable();

                        // sprawdzamy null lub wyjątek SNMP
                        boolean isException = (var instanceof Null) &&
                                (((Null) var).getSyntax() == SMIConstants.EXCEPTION_NO_SUCH_OBJECT ||
                                        ((Null) var).getSyntax() == SMIConstants.EXCEPTION_NO_SUCH_INSTANCE ||
                                        ((Null) var).getSyntax() == SMIConstants.EXCEPTION_END_OF_MIB_VIEW);

                        if (var == null || isException) {
                            // logujemy brak wartości
                            logMessage(path, address, "Brak wartości dla OID " + vb.getOid());
                        } else {
                            // konwersja wszystkich parametrów do Stringów
                            String ipStr    = address.toString();
                            String oidStr   = vb.getOid().toString();
                            String valStr   = var.toString();
                            String comment  = ""; // lub dowolny opis
                            System_l.insertMibLog(ipStr, oidStr, valStr, comment);
                        }
                    }
                } else {
                    // brak odpowiedzi od agenta SNMP
                    logMessage(path, address, "Brak odpowiedzi od agenta SNMP");
                }
            } catch (IOException e) {
                // błąd wysyłki PDU
                logMessage(path, address, "Błąd podczas wysyłania PDU: " + e.getMessage());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void logMessage(String path, Address address, String message) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(fmt);
        String fullMsg = timestamp + ": " + message + " (" + address + ")\n";

        // wybór separatora ścieżki zależnie od System_l.get_t()
        String logPath = System_l.get_t()
                ? path + "\\src\\main\\java\\main\\json\\logs\\log.log"
                : path + "/src/main/java/main/json/logs/log.log";

        File logFile = new File(logPath);
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(fullMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        if (!currentPdu.getVariableBindings().isEmpty()) {
            pduList.add(currentPdu);
        }
        return pduList;
    }

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
