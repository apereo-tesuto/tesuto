-- new permissions

insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
	('LAUNCH_PAPER_ACTIVATION', timestamp 'now', timestamp 'now', 'Launch a paper activation'),
	('VIEW_PROCTOR_ACTIVATION', timestamp 'now', timestamp 'now', 'View an activation as a proctor'),
	('VIEW_PROCESSOR_DELIVERY_ASSESSMENT_SESSION', timestamp 'now', timestamp 'now', 'View a delivery assessment session as a processor'),
	('TRACK_USER_HISTORY', timestamp 'now', timestamp 'now', 'Allow a user to appear in activity tracking');
