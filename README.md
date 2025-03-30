# SNMP Server for Windows

This is a simple SNMP server project developed in Java. It is designed to run on Windows and periodically sends SNMP `GET` requests to a list of devices to retrieve various system information such as RAM usage, CPU temperature, and disk usage.

## Project Structure

The main class of the project is located in the `Main` class, where the SNMP server starts and other necessary components (like `UDPControler` and `PDUget`) are invoked to handle the SNMP communication. The `UDPControler` class is responsible for initializing the SNMP server and managing incoming requests, while `PDUget` contains the logic for sending `GET` requests to specified SNMP agents.

## Features

- **SNMPv2c** support.
- Periodically fetch system data from remote devices using SNMP `GET` requests.
- Handles multiple SNMP agents with customizable OIDs for data retrieval.
- Configurable SNMP community string (default: "public").

## Requirements

- Java 21.0.6 or higher
- Maven for building the project

## Building and Running the Project

1. Clone the repository:

   ```bash
   git clone https://github.com/Taitlesonn/snmp-pr.git
   cd snmp-server
   ```

2. Build the project using Maven:

   ```bash
   mvn clean install
   ```

3. Run the project:

   ```bash
   mvn exec:java -Dexec.mainClass="Main"
   ```

## Configuration

- You can modify the list of SNMP agent addresses in the `Main` class (currently hardcoded as `udp:192.168.1.1/161` and `udp:192.168.1.2/161`).
- The SNMP community string is set to `"public"`, but this can be changed by updating the `OctetString("public")` in the code.

## Project Files

- **Main.java**: The entry point of the application where the SNMP server is started, and periodic `GET` requests are sent.
- **UDPControler.java**: Handles the SNMP communication setup and manages the listening process.
- **PDUget.java**: Contains methods for sending SNMP `GET` requests and processing the responses.
