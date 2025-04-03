package main;

import org.snmp4j.*;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class UDPControler {
    private Address listenAddress;
    private TransportMapping<UdpAddress> transport;
    private Snmp snmp;
    private CountDownLatch initLatch = new CountDownLatch(1);



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
                            // Implementation of reading data from MIB for GET.
                        }
                        case PDU.GETNEXT -> {
                            // Implementation of MIB tree traversal.
                        }
                        case PDU.SET -> {
                            // Implementation of setting values in MIB.
                        }
                        default -> {
                            // Unsupported PDU type.
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
