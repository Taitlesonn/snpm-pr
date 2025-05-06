-- Tworzenie tabeli do przechowywania danych SNMP
CREATE TABLE IF NOT EXISTS mib_objects (
    ip_address TEXT NOT NULL,        -- Adres IP urządzenia (IPv4 jako tekst)
    oid TEXT NOT NULL,               -- OID (Object Identifier)
    val TEXT,                        -- Wartość (string)
    comment TEXT,                    -- Komentarz (np. opis pola)
    PRIMARY KEY (ip_address, oid)    -- Klucz główny: kombinacja IP i OID
);
