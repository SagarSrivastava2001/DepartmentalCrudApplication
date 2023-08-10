CREATE TABLE IF NOT EXISTS user_table{
    id SERIAL,
    first_name varchar(50) NOT NULL,
    last_name varchar(50) NOT NULL,
    email varchar(50) NOT NULL,
    PRIMARY KEY (id)
};