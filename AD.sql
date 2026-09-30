CREATE DATABASE AD;

use AD;



CREATE TABLE drone (
    id          VARCHAR(50)  PRIMARY KEY,
    serial      VARCHAR(100) NOT NULL UNIQUE,
    modelo      VARCHAR(100) NOT NULL,
    fabricante  VARCHAR(100) NOT NULL,
    peso        DOUBLE       NOT NULL,
    tipo        VARCHAR(20)  NOT NULL
);