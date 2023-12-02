CREATE TABLE noteverso_note (
  id int  NOT NULL,
  note_id varchar(50) NOT NULL CONSTRAINT noteverso_note_pk UNIQUE,
  note_type smallint DEFAULT 0,
  content varchar(255) NOT NULL,
  is_pin smallint DEFAULT 0,
  is_deleted smallint DEFAULT 0,
  is_archived smallint DEFAULT 0,
  is_favorite smallint DEFAULT 0,
  project_id varchar(50) NOT NULL,
    status smallint DEFAULT 1,
    creator varchar(50) NOT NULL,
    updater varchar(50) NOT NULL,
    url varchar(100) DEFAULT NULL,
    added_at timestamp DEFAULT NULL,
    updated_at timestamp DEFAULT NULL,
    PRIMARY KEY (id)
    );
