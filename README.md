Thought for a second


Poniżej całościowa, zaktualizowana wersja pliku **README.md** w języku polskim, z uwzględnieniem, że aplikacja działa na Windows i Linux:


# SNMP Server dla Windows i Linux

Prosty serwer SNMP napisany w Javie, przeznaczony do uruchamiania na systemach **Windows** oraz **Linux**. Aplikacja cyklicznie wysyła zapytania SNMP `GET` do zdefiniowanej listy urządzeń, zbierając takie dane jak wykorzystanie pamięci RAM, temperatura CPU czy obciążenie dysku.

## Struktura projektu

- **Main.java** – punkt wejścia aplikacji, inicjalizuje i uruchamia cykliczne zapytania SNMP `GET`.  
- **UDPControler.java** – odpowiada za konfigurację i obsługę komunikacji UDP z agentami SNMP.  
- **PDUget.java** – implementuje metody do wysyłania zapytań SNMP `GET` i przetwarzania odpowiedzi.  
- **JsonControler.java** – odczytuje zewnętrzne pliki JSON, w których definiujesz adresy IP urządzeń, indeksy OID oraz listę samych OID‑ów.  
- **System_l.java** – (opcjonalnie) zawiera metody do zapisu pozyskanych danych bezpośrednio do bazy PostgreSQL.

## Najważniejsze funkcje

- Pełne wsparcie dla **SNMPv2c**.  
- Obsługa wielu agentów SNMP oraz dowolnej liczby OID‑ów konfigurowanych w pliku JSON.  
- Konfigurowalny „community string” (domyślnie `"public"`).  
- **Konfiguracja przez pliki JSON** — adresy urządzeń wraz z portami, indeksy i zestaw OID‑ów umieszczasz w jednym lub wielu plikach JSON, a `JsonControler` ładuje je przy starcie.  
- **Integracja z PostgreSQL** — w kodzie Java (w klasie `System_l.java`) definiujesz parametry połączenia do bazy (host, port, nazwa bazy, użytkownik i hasło), a dzięki driverowi JDBC (dodawanemu przez Mavena) dane SNMP mogą być zapisywane w tabelach PostgreSQL.

## Wymagania

- Java 21.0.6 lub wyższa  
- Maven 3.x  
- System operacyjny **Windows** (7/8/10/11) lub **Linux** (dowolna dystrybucja z jądrem 3.x+)  
-  Serwer PostgreSQL 15+ uruchomiony np. na `localhost:5432`, z bazą `mib_db` i użytkownikiem `mib_user` (hasło ustawiasz podczas tworzenia użytkownika)

## Budowanie i uruchamianie

1. Sklonuj repozytorium:
   ```bash
   git clone https://github.com/Taitlesonn/snpm-pr.git
   cd snpm-pr


2. Zbuduj projekt:
* **Linux**
   ```bash
   chmod +x build.sh
   ./build.sh
   ```
   * **Windows**
   ```bash
   ./build.ps1
   ```
   
3. Uruchom aplikację:

    * **Windows**

      ```bat
      cd out/
      java -jar snmp-pr-1.1.jar  #uprawnienia ADMINA są wymagane
      ```
    * **Linux**

      ```bash
      cd out/
      sudo java -jar snmp-pr-1.1.jar
      ```

## Konfiguracja

### Pliki JSON

W katalogu `config/` (utwórz go, jeśli go brak) umieść pliki `.json` o przykładowej strukturze:

```json

  [
    "udp:192.168.1.100/161",
    "udp:192.168.1.101/161"
  ]
```

```json
[
    "1.3.6.1.2.1.1.3.0",
    "1.3.6.1.4.1.2021.4.6.0"
]
```

`JsonControler` odczyta listę urządzeń i OID‑ów automatycznie podczas uruchomienia programu.

### Ustawienia SNMP

Domyślny community string to `"public"`; aby go zmienić, przekaż inny ciąg jako argument metody `PDUget.get(...)`.

### Integracja z PostgreSQL

W klasie `System_l.java` (lub dedykowanym pliku konfiguracyjnym) określ parametry połączenia:

```java
String url  = "jdbc:postgresql://localhost:5432/mib_db";
String user = "mib_user";
String pass = "TwojeHaslo";
```

Upewnij się, że w `pom.xml` masz dodany dependency:

```xml
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <version>42.5.0</version>
</dependency>
```

## Rozszerzanie i rozwój

* **Dodawanie nowych OID‑ów** – po prostu dopisz je do listy `"oids"` w pliku JSON.
* **Zmiana harmonogramu albo community string** – modyfikuj odpowiednie parametry w klasie `Main.java` bądź przy wywołaniu `PDUget.get(...)`.
* **Contributing**: forkuj repozytorium, utwórz gałąź `feature/nazwa`, wprowadź poprawki i zgłoś pull request z opisem zmian.

## Licencja

Brak pliku LICENSE 
