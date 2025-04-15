package main.PDUContorler;

import com.google.gson.Gson;
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

import java.io.IOException;

public class PDUget {



    public static void get(Address address, Snmp snmp, int[] id, String Comunity_string, Gson gson, String path) throws IOException {
        // Create a new CommunityTarget object to define the SNMP target
        CommunityTarget<Address> target = getTarget(address, Comunity_string);


        // Creating PDU get
        PDU pdu = new PDU();

        String[] oids = JsonControler.get_oids_list(gson, path);
        int lengh = oids.length;

        for (int i : id) {
            // Checking if it is in the index
            if (i > lengh || i < 0) {
                System.out.println("Out of index oid");
                break;
            }
            // Ading OIDS to pdu
            pdu.add(new VariableBinding(new OID(oids[i])));
        }

        // Sending GET
        try {
            ResponseEvent event = snmp.send(pdu, target);
            if (event != null && event.getResponse() != null) {
                PDU responserPDU = event.getResponse();

                for (VariableBinding vb : responserPDU.getVariableBindings()) {
                    //Implementacja zapisu odpowiediz
                    System.out.println(vb.getOid() + " = " + vb.getVariable());
                }
            } else {
                System.out.println("No response from the SNMP agent or an error occurred form: " + address);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private static CommunityTarget<Address> getTarget(Address address, String Comunity_string) {
        CommunityTarget<Address> target = new CommunityTarget<>();

        // Set the address of the SNMP agent (device)
        target.setAddress(address);

        // Set the community string for authentication (commonly "public" for read-only access)
        target.setCommunity(new OctetString(Comunity_string));

        // Set the number of retries in case the request fails (2 retries)
        target.setRetries(2);

        // Set the timeout in seconds for receiving a response (2 seconds)
        target.setTimeout(2);

        // Set the SNMP version (SNMPv2c in this case)
        target.setVersion(SnmpConstants.version2c);
        return target;
    }
}
