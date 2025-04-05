Jasne! Oto poprawiona wersja Twojego oryginalnego `README`, do którego dodałem wzmiankę o konfiguracji bazy danych PostgreSQL **w ramach tekstu ciągłego**, tak jak prosiłeś:

---

# SNMP Server for Windows

This is a simple SNMP server project developed in Java. It is designed to run on Windows and periodically sends SNMP `GET` requests to a list of devices to retrieve various system information such as RAM usage, CPU temperature, and disk usage.

## Project Structure

- **Main.java**: The entry point of the application where the SNMP server is started and periodic `GET` requests are sent.
- **UDPControler.java**: Handles the SNMP communication setup and manages the listening process.
- **PDUget.java**: Contains methods for sending SNMP `GET` requests and processing the responses.
- **JsonControler.java**: A new class that reads configuration from JSON files. In these files, you now define:
    - The IP addresses to be scanned.
    - The indexes for OIDs.
    - The list of OIDs themselves.

## Features

- **SNMPv2c** support.
- Periodically fetch system data from remote devices using SNMP `GET` requests.
- Handles multiple SNMP agents with customizable OIDs for data retrieval.
- Configurable SNMP community string (default: `"public"`).
- **JSON-based configuration:** Configure target IPs, OID indexes, and OID lists via external JSON files using the new `JsonControler` class.
- **PostgreSQL integration:** SNMP data can be stored in a PostgreSQL database. The connection details (host, port, database name, username, and password) are configured in the Java code (`System_l.java`), and the project uses the PostgreSQL JDBC driver added via Maven.

## Requirements

- Java 21.0.6 or higher
- Maven for building the project
- PostgreSQL server (tested with version 15) running on `localhost:5432`, with a database named `mib_db`, user `mib_user`, and the appropriate password set during user creation

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

The list of SNMP agent addresses was previously hardcoded (e.g., `udp:10.10.10.2/161` and `udp:10.10.10.3/161`). Now, you can define the target IP addresses along with the OID indexes and the list of OIDs in external JSON configuration files. The `JsonControler` class is responsible for reading these files.

The SNMP community string is set to `"public"` by default, but it can be changed in the code by updating the parameter in the call to `PDUget.get(...)`.

Additionally, database credentials are defined in the code to connect to PostgreSQL (host `localhost`, port `5432`, database `mib_db`, user `mib_user`, and a password set during user creation). Ensure the PostgreSQL JDBC driver is added as a dependency in the Maven `pom.xml`.

---

Let me know if chcesz, żebym wrzucił jeszcze fragmenty kodu lub przykład konfiguracji JSON/SQL 😊