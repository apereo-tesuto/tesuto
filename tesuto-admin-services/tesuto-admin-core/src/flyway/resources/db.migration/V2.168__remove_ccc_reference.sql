delete from user_account_district where ccc_id NOT LIKE '%ZZ%';
delete from user_account_college where college_ccc_id NOT LIKE '%ZZ%';
delete from college where ccc_id NOT LIKE '%ZZ%';
delete from district where ccc_id NOT LIKE '%ZZ%';

update college set name = 'Demo College A', street_address_1 = '1760 East Pecos Road', street_address_2 = 'Suite 432', city = 'Gilbert', postal_code = '85295', url = 'http://www.unicon.net' where ccc_id ='ZZ3';
update college set name = 'Demo College B', street_address_1 = '1760 East Pecos Road', street_address_2 = 'Suite 432', city = 'Gilbert', postal_code = '85295', url = 'http://www.unicon.net' where ccc_id ='ZZ4';
update college set name = 'Demo College C', street_address_1 = '1760 East Pecos Road', street_address_2 = 'Suite 432', city = 'Gilbert', postal_code = '85295', url = 'http://www.unicon.net' where ccc_id ='ZZ5';

update test_location set name = 'Demo College A Test Center', street_address_1 = '1760 East Pecos Road', street_address_2 = 'Suite 432', city = 'Gilbert', postal_code = '85295' where id ='1000003';
update test_location set name = 'Demo College A Remote Test Center', street_address_1 = '1760 East Pecos Road', street_address_2 = 'Suite 432', city = 'Gilbert', postal_code = '85295' where id ='1000004';
update test_location set name = 'Demo College B Test Center', street_address_1 = '1760 East Pecos Road', street_address_2 = 'Suite 432', city = 'Gilbert', postal_code = '85295' where id ='2000003';
update test_location set name = 'Demo College B Remote Test Center', street_address_1 = '1760 East Pecos Road', street_address_2 = 'Suite 432', city = 'Gilbert', postal_code = '85295' where id ='2000004';