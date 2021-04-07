CREATE TABLE credentials
(
    id    uuid primary key,
    key   varchar(32),
    token varchar(64),
    owner varchar(100) UNIQUE
);


CREATE TABLE candidates
(
    credential_id uuid REFERENCES credentials (id),
    name          varchar(100),
    UNIQUE (credential_id, name)
);

INSERT INTO credentials(key, token, owner)
SELECT id, token, name
from vendors;

INSERT INTO candidates(credential_id, name)
SELECT id, owner
from credentials;