package main;

import org.snmp4j.*;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;

public class UDPControler {
    private Address listenAddress;
    private TransportMapping<UdpAddress> transport;
    private Snmp snmp;

    private void init() throws IOException {
        try {
            //Enabling listening on all interfaces on port 161-udp
            this.listenAddress = GenericAddress.parse("udp:0.0.0.0/161");

            // Create a transport mapping instance:
            // We use DefaultUdpTransportMapping, which implements UdpTransportMapping.
            this.transport = new DefaultUdpTransportMapping((UdpAddress) this.listenAddress);

            // Initialize the SNMP root object with the passed transport.
            this.snmp = new Snmp(this.transport);

            // Registering a Command Responder that will respond to incoming requests.
            this.snmp.addCommandResponder(new CommandResponder() {
                @Override
                public <A extends Address> void processPdu(CommandResponderEvent<A> commandResponderEvent) {
                    PDU pdu = commandResponderEvent.getPDU();

                    // If PDU is null, we do not continue processing.
                    if (pdu == null) {
                        return;
                    }

                    // Getting the sender's address.
                    A peerAddress = commandResponderEvent.getPeerAddress();

                    switch (pdu.getType()) {
                        case PDU.GET -> {
                            //Implementation of data reading logic from MIB for GET.

                            break;
                        }
                        case PDU.GETNEXT -> {
                            // Implementation of MIB tree traversal logic.

                            break;
                        }
                        case PDU.SET -> {
                            // Implementation of MIB tree traversal logic.

                            break;
                        }
                        default -> {
                            // Unsupported PDU type:
                            break;
                        }
                    }

                    try {
                        // Start listening – open network socket.
                        transport.listen();
                        System.out.println("SNMP Server is lisinig on Address");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                }


            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void start() throws IOException {
        try {
            this.init();

            while (true) {
                Thread.sleep(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Snmp getSnmp() {
        return this.snmp;
    }
}
