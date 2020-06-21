-- Namespacing for content authors
-- This is a way to specify a namespace to keep assessments and their items,
-- authored by different content providers, separated and managed by those authors.
-- This will allow them to author assessments with related items, item banks
-- without naming collisions.

CREATE TABLE author_namespace (
  namespace_id bigint NOT NULL,
  created_on_date timestamp without time zone,
  last_updated_date timestamp without time zone,
  namespace character varying(255) NOT NULL
);

ALTER TABLE ONLY author_namespace
ADD CONSTRAINT author_namespace_pkey PRIMARY KEY (namespace_id);

ALTER TABLE user_account ADD COLUMN author_namespace_id BIGINT DEFAULT NULL;

ALTER TABLE ONLY user_account
ADD CONSTRAINT fk_user_account FOREIGN KEY (author_namespace_id) REFERENCES author_namespace(namespace_id);