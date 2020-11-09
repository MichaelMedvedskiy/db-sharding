-- Удаляем базу данных если она есть
DROP DATABASE IF EXISTS associations;

-- И создаём её заново
CREATE DATABASE associations;


-- Установка базы данных по умолчанию
\c associations;

CREATE SEQUENCE hibernate_sequence START 1;

-- БД ассоциаций платежей пользователя с БД (нужно для сохранения id БД при увеличении кол-ва БД (когда хеш от
-- пользователя будет считаться уже другой))
CREATE TABLE ASSOCIATION
(
    SENDER bigint NOT NULL,
    DBID   int    NOT NULL,
    PRIMARY KEY (SENDER)
);

