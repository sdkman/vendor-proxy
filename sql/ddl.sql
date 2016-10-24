CREATE DATABASE sdkman_vendor_proxy;
\c sdkman_vendor_proxy
CREATE TABLE vendors(id varchar(32) primary key, token varchar(64), name varchar(100));
