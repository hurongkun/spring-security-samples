CREATE TABLE users
(
    username VARCHAR(50)  NOT NULL PRIMARY KEY,
    PASSWORD VARCHAR(500) NOT NULL,
    enabled  boolean      NOT NULL
);
create table authorities
(
    username  varchar(50) not null,
    authority varchar(50) not null,
    constraint fk_authorities_users foreign key (username) references users (username)
);
create unique index ix_auth_username on authorities (username, authority);



INSERT INTO `spring_security`.`users`(`username`, `password`, `enabled`) VALUES ('javaUser', '123', 1);
INSERT INTO `spring_security`.`users`(`username`, `password`, `enabled`) VALUES ('javaAdmin', '123', 1);



INSERT INTO `spring_security`.`authorities`(`username`, `authority`) VALUES ('javaAdmin', 'ROLE_admin');
INSERT INTO `spring_security`.`authorities`(`username`, `authority`) VALUES ('javaUser', 'ROLE_user');
