create table if not exists Person (
  id identity primary key,
  name varchar(30) not null,
  lastName varchar(50) not null
);