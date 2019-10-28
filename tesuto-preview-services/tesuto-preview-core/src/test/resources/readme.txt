Filename: readme.txt
Date: 5-12-15
Author: Richard Scott Smith

Quick instructions on setting up the database for tesuto.

MySQL:
Note: MySQL defaults to Latin1, ISO-8859-1 for it's character set.  Be sure to force UTF-8 encoding when creating tables.
Login as the root user:
mysql -u root -p
From the mysql prompt:
create user tesuto;
Note, identified by 'tesuto' is a terrible password, but this is development.
grant all on `tesuto`.* to 'tesuto'@'localhost' identified by 'tesuto';
exit
mysql -u tesuto -ptesuto
create database tesuto;

Postgres:
Note: Postgresql defaults to UTF-8 so nothing special needs to be done there.
Login as the root user:
psql -U postgres
\c template1
create database tesuto;
Create a user for your application:
create user tesuto with password 'tesuto';
grant all privileges on database "tesuto" to tesuto;
Quit and login again:
\q
psql -Utesuto
\c tesuto;

Resetting the Postgres Database:
psql -U postgres
drop database tesuto;
create database tesuto;
grant all privileges on database "tesuto" to tesuto;
\q
psql -U tesuto tesuto
\dt
No relations found.
