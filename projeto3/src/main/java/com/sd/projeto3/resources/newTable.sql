CREATE TABLE snapshot (
    id   INTEGER PRIMARY KEY AUTOINCREMENT
                 NOT NULL,
    data DATE    NOT NULL
);


CREATE TABLE mapa (
    id         INTEGER PRIMARY KEY AUTOINCREMENT
                       NOT NULL,
    chave      INTEGER NOT NULL,
    tipo       INTEGER NOT NULL,
    texto      TEXT,
    data       DATE    NOT NULL,
    snapshotid INTEGER  CONSTRAINT fk_snapshot REFERENCES snapshot (id) ON DELETE CASCADE
                                                                       ON UPDATE NO ACTION
                       NOT NULL
);
