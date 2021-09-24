-- Similar, but separate, structures for managing API permissions in the system.

-- Groupings of API Permissions
create table security_group_api (
  security_group_api_id integer not null,
  created_on_date timestamp without time zone,
  last_updated_date timestamp without time zone,
  description character varying(255),
  group_name character varying(255)
);

-- Join table for Spring Security GrantedAuthority's and security_group_api's
create table security_group_api_security_permission (
  security_permission_id character varying(255) not null,
  security_group_api_id integer not null
);

-- API users
-- TODO: Determine whether we need to implement UserDetails
create table user_account_api (
  user_account_api_id character varying(255) not null,
  created_on_date timestamp without time zone,
  last_updated_date timestamp without time zone,
  account_locked boolean,
  display_name character varying(255) not null,
  enabled boolean,
  expired boolean,
  failed_logins integer,
  last_login_date timestamp without time zone,
  password character varying(255),
  username character varying(255) not null
);


create table user_account_api_security_group_api (
  user_account_api_id character varying(255) not null,
  security_group_api_id integer not null
);