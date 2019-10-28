--
-- Turn off autocommit and start a transaction so that we can use the temp tables
--

SET AUTOCOMMIT FALSE;

START TRANSACTION;

--
-- Insert client information into the temporary tables. To add clients to the HSQL database, edit things here.
-- 

INSERT INTO client_details_TEMP (client_id, client_secret, client_name, dynamically_registered, refresh_token_validity_seconds, access_token_validity_seconds, id_token_validity_seconds, allow_introspection) VALUES
	('tesuto_api', '2b4af75b-a77b-45cf-baee-e316ac2f6eb6', 'Generic Tesuto Services Client For Development', true, null, 3600, 600, false);

INSERT INTO client_scope_TEMP (owner_id, scope) VALUES
	('tesuto_api', 'superuser'),
	('tesuto_api', 'openid'),
    ('tesuto_api', 'eppn'),
    ('tesuto_api', 'profile'),
    ('tesuto_api', 'email'),
    ('tesuto_api', 'address'),
    ('tesuto_api', 'cccid'),
    ('tesuto_api', 'godtool'),
	('tesuto_api', 'tesuto_service');

INSERT INTO client_redirect_uri_TEMP (owner_id, redirect_uri) VALUES
	('tesuto_api', 'http://localhost:8443/'),
	('tesuto_api', 'http://localhost:8081/activation-service'),
    ('tesuto_api', 'http://localhost:8082/admin-service'),
    ('tesuto_api', 'http://localhost:8083/'),
    ('tesuto_api', 'http://localhost:8084/content-service'),
    ('tesuto_api', 'http://localhost:8085/delivery-service'),
    ('tesuto_api', 'http://localhost:8086/placement-service'),
    ('tesuto_api', 'http://localhost:8087/preview-service'),
    ('tesuto_api', 'http://localhost:8088/rules-editor'),
    ('tesuto_api', 'http://localhost:8089/reports-service'),
    ('tesuto_api', 'http://localhost:8090/rules-service'),
    ('tesuto_api', 'http://localhost:8092/');

	
INSERT INTO client_grant_type_TEMP (owner_id, grant_type) VALUES
	('tesuto_api', 'authorization_code'),
	('tesuto_api', 'client_credentials');
	
--
-- Merge the temporary clients safely into the database. This is a two-step process to keep clients from being created on every startup with a persistent store.
--

MERGE INTO client_details 
  USING (SELECT client_id, client_secret, client_name, dynamically_registered, refresh_token_validity_seconds, access_token_validity_seconds, id_token_validity_seconds, allow_introspection FROM client_details_TEMP) AS vals(client_id, client_secret, client_name, dynamically_registered, refresh_token_validity_seconds, access_token_validity_seconds, id_token_validity_seconds, allow_introspection)
  ON vals.client_id = client_details.client_id
  WHEN NOT MATCHED THEN 
    INSERT (client_id, client_secret, client_name, dynamically_registered, refresh_token_validity_seconds, access_token_validity_seconds, id_token_validity_seconds, allow_introspection) VALUES(client_id, client_secret, client_name, dynamically_registered, refresh_token_validity_seconds, access_token_validity_seconds, id_token_validity_seconds, allow_introspection);

MERGE INTO client_scope 
  USING (SELECT id, scope FROM client_scope_TEMP, client_details WHERE client_details.client_id = client_scope_TEMP.owner_id) AS vals(id, scope)
  ON vals.id = client_scope.owner_id AND vals.scope = client_scope.scope
  WHEN NOT MATCHED THEN 
    INSERT (owner_id, scope) values (vals.id, vals.scope);

MERGE INTO client_redirect_uri 
  USING (SELECT id, redirect_uri FROM client_redirect_uri_TEMP, client_details WHERE client_details.client_id = client_redirect_uri_TEMP.owner_id) AS vals(id, redirect_uri)
  ON vals.id = client_redirect_uri.owner_id AND vals.redirect_uri = client_redirect_uri.redirect_uri
  WHEN NOT MATCHED THEN 
    INSERT (owner_id, redirect_uri) values (vals.id, vals.redirect_uri);

MERGE INTO client_grant_type 
  USING (SELECT id, grant_type FROM client_grant_type_TEMP, client_details WHERE client_details.client_id = client_grant_type_TEMP.owner_id) AS vals(id, grant_type)
  ON vals.id = client_grant_type.owner_id AND vals.grant_type = client_grant_type.grant_type
  WHEN NOT MATCHED THEN 
    INSERT (owner_id, grant_type) values (vals.id, vals.grant_type);
    
-- 
-- Close the transaction and turn autocommit back on
-- 
    
COMMIT;

SET AUTOCOMMIT TRUE;

