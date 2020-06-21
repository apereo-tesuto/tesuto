create or replace view activation_view as
select activation_id, user_id, start_date, end_date, status,
	   creator_id, create_date, current_assessment_session_id,
	   assessment_title, creator_name,
	   location_id,
	   namespace, assessment_identifier, delivery_type, status_update_date,
	   null as test_event_id, null as test_event_name
from activation where activation_type='Individual'
union
select act.activation_id, act.user_id, ev.start_date, ev.end_date, act.status,
	   act.creator_id, ev.create_date, act.current_assessment_session_id,
	   act.assessment_title, act.creator_name,
	   ev.test_location_id as location_id,
	   act.namespace, act.assessment_identifier, ev.delivery_type, act.status_update_date,
	   ev.test_event_id, ev.name as test_event_name
from activation act, test_event ev
where act.activation_type='TestEvent' and
	  ev.test_event_id = act.test_event_id;
	  
