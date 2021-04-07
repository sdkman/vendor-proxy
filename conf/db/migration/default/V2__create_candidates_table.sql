CREATE TABLE credentials
(
    id    serial primary key,
    key   varchar(32),
    token varchar(64),
    owner varchar(100)
);


CREATE TABLE candidates
(
    id            serial primary key,
    credential_id integer REFERENCES credentials (id),
    name          varchar(100)
);

INSERT INTO credentials(key, token, owner)
SELECT id, token, name
from vendors;

INSERT INTO candidates(credential_id, name)
SELECT id, owner
from credentials;