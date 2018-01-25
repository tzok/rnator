CREATE TABLE file (
  id            SERIAL PRIMARY KEY,
  absolute_path TEXT,
  checksum      VARCHAR(255),
  has_rna       BOOLEAN
);
