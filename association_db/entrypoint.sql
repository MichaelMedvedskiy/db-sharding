
-- Удаляем базу данных если она есть
DROP DATABASE IF EXISTS association;

-- И создаём её заново
CREATE DATABASE association;


-- Установка базы данных по умолчанию
\c association;

CREATE SEQUENCE hibernate_sequence START 1;

CREATE TABLE ASSOCIATION
(
    SENDER   bigint   NOT NULL,
    DBID     int   NOT NULL,
    PRIMARY KEY (SENDER)
);

