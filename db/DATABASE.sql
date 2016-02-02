DROP TABLE USERS;
DROP TABLE ROUTES;

CREATE TABLE USERS(
	ID INT PRIMARY KEY NOT NULL,
	USERNAME VARCHAR NOT NULL,
	PASSWORD VARCHAR NOT NULL,
	NAME VARCHAR NOT NULL,
	EMAIL VARCHAR NOT NULL

);

CREATE TABLE ROUTES(
	ID INT PRIMARY KEY NOT NULL,
	NICKNAME VARCHAR NOT NULL,
	ROUTE VARCHAR NOT NULL
);

DROP SEQUENCE USERS_ID_SEQ;
DROP SEQUENCE ROUTES_ID_SEQ;

CREATE SEQUENCE USERS_ID_SEQ START 1;
CREATE SEQUENCE ROUTES_ID_SEQ START 1;

INSERT INTO USERS VALUES (NEXTVAL('USERS_ID_SEQ'), 'heronsilva', 'password', 'Heron Silva', 'heronsilva@poli.ufrj.br');
INSERT INTO USERS VALUES (NEXTVAL('USERS_ID_SEQ'), 'sbvillasboas', 'password', 'Sergio Barbosa Villas Boas', 'sbvillas@poli.ufrj.br');

COMMIT;