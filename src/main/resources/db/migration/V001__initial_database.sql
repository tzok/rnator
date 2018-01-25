CREATE TABLE file (
  id       SERIAL PRIMARY KEY,
  path     TEXT,
  checksum VARCHAR(255) UNIQUE,
  has_rna  BOOLEAN
);
