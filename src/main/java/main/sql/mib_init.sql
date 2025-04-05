-- Tworzenie tabeli
CREATE TABLE IF NOT EXISTS mib_objects (
    oid TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    type TEXT NOT NULL,
    value TEXT,
    description TEXT
);

-- Wstawianie danych
INSERT INTO mib_objects (oid, name, type, value, description) VALUES
('1.3.6.1.2.1.1.1.0', 'sysDescr', 'String', 'Linux SNMP Agent', 'System Description'),
('1.3.6.1.2.1.1.5.0', 'sysName', 'String', 'ZLinux', 'System Name')
ON CONFLICT (oid) DO NOTHING;
