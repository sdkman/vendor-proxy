ALTER TABLE candidates
    DROP CONSTRAINT candidates_credential_id_fkey,
    ADD CONSTRAINT candidates_credential_id_fkey
        FOREIGN KEY (credential_id)
            REFERENCES credentials (id)
            ON DELETE CASCADE;