insert into rule(id, application, title, version, active, freetext) values(1, 'sns-listener', 'is_veteran', null, false, 'List<String> users = new ArrayList<String>();\nusers.add(cccid);\n');

insert into condition_row(id, published_name, published_object, condition, value1, rule_row_id, condition_rows_order) values(2, 'map', 'Map()', null, null, 1, 1);
insert into condition_row(id, published_name, published_object, condition, value1, rule_row_id, condition_rows_order) values(3, 'cccid', 'String()', 'from', 'map.get("cccid")', 1, 2);
insert into condition_row(id, published_name, published_object, condition, value1, rule_row_id, condition_rows_order) values(4, 'studentProfile', 'Map()', 'from', 'map.get("studentProfile")', 1, 3);	
insert into condition_row(id, published_name, published_object, condition, value1, rule_row_id, condition_rows_order) values(5, 'attrs', 'Map()', 'from', 'studentProfile.get("attrs")', 1, 4);
insert into condition_row(id, published_name, published_object, condition, value1, rule_row_id, condition_rows_order) values(6, 'attributeMap', 'Map()', 'from', 'attrs.get("attributeMap")', 1, 5);
insert into condition_row(id, published_name, published_object, condition, value1, rule_row_id, condition_rows_order) values(7, 'veteran', 'Object(this == true)', 'from', 'attributeMap.get("veteran_services")', 1, 6);

insert into action_row(id, action_type, rule_row_id, action_rows_order) values(8, 'MESSAGE', 1, 1);
    
insert into action_parameter(id, name, value, action_row_id, parameters_order) values(9, 'subject', '"Thank you for your service"', 8, 1);
insert into action_parameter(id, name, value, action_row_id, parameters_order) values(10, 'message-body', '"To all of our veterans, we wish to show our appreciation for their dedication and sacrifice"', 8, 2);
insert into action_parameter(id, name, value, action_row_id, parameters_order) values(11, 'message-body-html', '"To all of our veterans, we wish to show our appreciation for their dedication and sacrifice"', 8, 3);
insert into action_parameter(id, name, value, action_row_id, parameters_order) values(12, 'users', 'users', 8, 4);

insert into rule_set(id, application, ccc_mis_code, version) values(13, 'sns-listener', 'ZZ1', '1');

insert into rule_set_row(id, application, rule_id, title, version, rule_set_id, rule_set_rows_order) values(14, 'sns-listener', 1, 'is_veteran', '1', 13, 1); 
