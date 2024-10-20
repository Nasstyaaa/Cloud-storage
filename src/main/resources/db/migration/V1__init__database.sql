CREATE TABLE users
(
    id       INT AUTO_INCREMENT NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(250) NOT NULL,
    `role`   VARCHAR(50) NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
);