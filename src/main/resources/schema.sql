DROP TABLE IF EXISTS user
DROP TABLE IF EXISTS account
DROP TABLE IF EXISTS check
DROP TABLE IF EXISTS message

CREATE TABLE user (id VARCHAR(64) PRIMARY KEY, name VARCHAR(256), email VARCHAR(256), username VARCHAR(64), password VARCHAR(64), is_admin BOOLEAN)
CREATE TABLE account (id VARCHAR(64) PRIMARY KEY, amount NUMERIC(12,4), number INTEGER, owner_id VARCHAR(64))
CREATE TABLE check (id VARCHAR(64) PRIMARY KEY, amount NUMERIC(12,4), number VARCHAR(16), account_id VARCHAR(64))
CREATE TABLE message (id VARCHAR(62) PRIMARY KEY, name VARCHAR(256), email VARCHAR(256), subject VARCHAR(128), message VARCHAR(2048))

INSERT INTO user (id, name, email, username, password, is_admin) VALUES (1, 'John Coltraine', 'john@coltraine.com', 'john.coltraine', 'j0hn', false);
INSERT INTO user (id, name, email, username, password, is_admin) VALUES (2, 'Upton Sinclair', 'upton@sinclair.com', 'upton.sinclair', 'upt0n', false);
INSERT INTO user (id, name, email, username, password, is_admin) VALUES (3, 'Admin Admin', 'admin@terracottabank.com', 'admin', 'admin', true);

INSERT INTO account (id, amount, number, owner_id) VALUES (1, 2500, 987654321, 1);
INSERT INTO account (id, amount, number, owner_id) VALUES (2, 25, 987654322, 2);

INSERT INTO message (id, name, email, subject, message) VALUES (1, '<script>alert("contact name is vulnerable to reflected xss")</script>', '<script>alert("contact email is vulnerable to reflected xss")</script>', '<script>alert("message subject is vulnerable to reflected xss")</script>', '<script>alert("message content is vulnerable to reflected xss")</script>');
	