package main;

import org.snmp4j.*;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class UDPControler {
    private Address listenAddress;
    private TransportMapping<UdpAddress> transport;
    private Snmp snmp;
    private CountDownLatch initLatch = new CountDownLatch(1);
    private final Map<OID, Variable> mibStore = new ConcurrentHashMap<>();

    



    private void init() throws IOException {
        try {
            // Listen on all interfaces on UDP port 161
            this.listenAddress = GenericAddress.parse("udp:0.0.0.0/161");

            // Create a transport mapping instance:
            this.transport = new DefaultUdpTransportMapping((UdpAddress) this.listenAddress);

            // Initialize the SNMP object with the given transport.
            this.snmp = new Snmp(this.transport);

            // Register a CommandResponder to handle incoming requests.
            this.snmp.addCommandResponder(new CommandResponder() {
                @Override
                public void processPdu(CommandResponderEvent commandResponderEvent) {
                    System.out.println("Processing PDU...");
                    PDU pdu = commandResponderEvent.getPDU();

                    // If PDU is null, do not proceed.
                    if (pdu == null) {
                        return;
                    }

                    // Retrieve the sender's address.
                    Address peerAddress = commandResponderEvent.getPeerAddress();

                    switch (pdu.getType()) {
                        case PDU.GET -> {
                            PDU response = new PDU(PDU.RESPONSE, (List<? extends VariableBinding>) pdu.getRequestID());
                            for (VariableBinding vb : pdu.getVariableBindings()) {
                                OID oid = vb.getOid();
                                Variable val = mibStore.get(oid);
                                if (val != null) {
                                    response.add(new VariableBinding(oid, val));
                                } else {
                                    // noSuchObject
                                    response.add(new VariableBinding(
                                            oid,
                                            new org.snmp4j.smi.Null()
                                    ));
                                }
                            }
                            CommunityTarget target = new CommunityTarget();
                            target.setAddress(peerAddress);
                            target.setCommunity(new OctetString("public"));
                            target.setVersion(SnmpConstants.version2c);
                            try {
                                snmp.send(response, target);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        case PDU.GETNEXT -> {
                            PDU response = new PDU(PDU.RESPONSE, (List<? extends VariableBinding>) pdu.getRequestID());
                            // posortowane klucze MIB
                            List<OID> sorted = new ArrayList<>(mibStore.keySet());
                            Collections.sort(sorted);
                            for (VariableBinding vb : pdu.getVariableBindings()) {
                                OID oid = vb.getOid();
                                // znajdź najmniejszy OID > żądanego
                                OID next = null;
                                for (OID cand : sorted) {
                                    if (cand.compareTo(oid) > 0) {
                                        next = cand;
                                        break;
                                    }
                                }
                                if (next != null) {
                                    response.add(new VariableBinding(next, mibStore.get(next)));
                                } else {
                                    // koniec drzewa
                                    response.add(new VariableBinding(
                                            oid,
                                            new org.snmp4j.smi.Null()
                                    ));
                                }
                            }
                            CommunityTarget target = new CommunityTarget();
                            target.setAddress(peerAddress);
                            target.setCommunity(new OctetString("public"));
                            target.setVersion(SnmpConstants.version2c);
                            try {
                                snmp.send(response, target);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        case PDU.SET -> {
                            PDU response = new PDU(PDU.RESPONSE, (List<? extends VariableBinding>) pdu.getRequestID());
                            int idx = 1;
                            for (VariableBinding vb : pdu.getVariableBindings()) {
                                OID oid = vb.getOid();
                                Variable newVal = vb.getVariable();
                                if (!mibStore.containsKey(oid)) {
                                    // nieznana nazwa → noSuchName
                                    response.setErrorStatus(PDU.noSuchName);
                                    response.setErrorIndex(idx);
                                    break;
                                }
                                // zapis do MIB
                                mibStore.put(oid, newVal);
                                response.add(new VariableBinding(oid, newVal));
                                idx++;
                            }
                            CommunityTarget target = new CommunityTarget();
                            target.setAddress(peerAddress);
                            target.setCommunity(new OctetString("public"));
                            target.setVersion(SnmpConstants.version2c);
                            try {
                                snmp.send(response, target);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        default -> {
                            // wszystkie inne typy → genErr
                            PDU response = new PDU(PDU.RESPONSE, (List<? extends VariableBinding>) pdu.getRequestID());
                            response.setErrorStatus(PDU.genErr);
                            CommunityTarget target = new CommunityTarget();
                            target.setAddress(peerAddress);
                            target.setCommunity(new OctetString("public"));
                            target.setVersion(SnmpConstants.version2c);
                            try {
                                snmp.send(response, target);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    // NOTE: We do not call transport.listen() here because the listening is started once during initialization.
                    System.out.println("Processing completed.");
                }
            });

            // Start listening - should be called once during initialization.
            this.transport.listen();
            System.out.println("SNMP Server is listening on address: " + this.listenAddress);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void start() throws IOException {
        try {
            this.init();
            initLatch.countDown(); // signal that initialization is complete
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Snmp getSnmp() {
        return this.snmp;
    }

    public void awaitInitialization() throws InterruptedException {
        initLatch.await();
    }
}
