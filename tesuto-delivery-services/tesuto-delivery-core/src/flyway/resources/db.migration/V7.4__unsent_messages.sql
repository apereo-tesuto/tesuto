CREATE TABLE unsent_message (
       id varchar(100) primary key,
       queue_url varchar(1000),
       last_sent_on timestamp,
       message text
);

create index unsent_message_queue_url_idx on unsent_message(queue_url);

