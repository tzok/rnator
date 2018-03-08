CREATE TABLE file (
  id        SERIAL PRIMARY KEY,
  path      TEXT NOT NULL,
  processed BOOLEAN
);

CREATE TABLE structure (
  id      SERIAL PRIMARY KEY,
  file_id INTEGER REFERENCES file (id)
);

CREATE TABLE model (
  id           SERIAL PRIMARY KEY,
  structure_id INTEGER REFERENCES structure (id),
  number       INTEGER
);

CREATE TABLE chain (
  id       SERIAL PRIMARY KEY,
  model_id INTEGER REFERENCES model (id),
  name     VARCHAR(255) NOT NULL,
  is_rna   BOOLEAN,
);

CREATE TABLE residue (
  id             SERIAL PRIMARY KEY,
  chain_id       INTEGER REFERENCES chain (id),
  number         INTEGER NOT NULL,
  insertion_code VARCHAR(255)
);

CREATE TABLE dihedral (
  id         SERIAL PRIMARY KEY,
  residue_id INTEGER REFERENCES residue (id),
  alpha      DOUBLE PRECISION,
  beta       DOUBLE PRECISION,
  gamma      DOUBLE PRECISION,
  delta      DOUBLE PRECISION,
  epsilon    DOUBLE PRECISION,
  zeta       DOUBLE PRECISION,
  nu0        DOUBLE PRECISION,
  nu1        DOUBLE PRECISION,
  nu2        DOUBLE PRECISION,
  nu3        DOUBLE PRECISION,
  nu4        DOUBLE PRECISION,
  chi        DOUBLE PRECISION,
  eta        DOUBLE PRECISION,
  theta      DOUBLE PRECISION
);

CREATE TABLE pucker (
  id             SERIAL PRIMARY KEY,
  residue_id     INTEGER REFERENCES residue (id),
  pseudorotation DOUBLE PRECISION
);
