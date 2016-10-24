CREATE DATABASE sdkman_vendor_proxy;
\c sdkman_vendor_proxy
CREATE TABLE vendors(id varchar(50) primary key, token varchar(100), name varchar(50));
