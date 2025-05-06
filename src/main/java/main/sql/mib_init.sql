CREATE TABLE IF NOT EXISTS public.mib_objects (
    ip_address TEXT NOT NULL,
    oid        TEXT NOT NULL,
    val        TEXT,
    comment    TEXT,
    PRIMARY KEY (ip_address, oid)
);
