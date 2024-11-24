insert into app_user (user_id, firstname, surname) values (101, 'Laren', 'Pateman');

insert into account(account_number, user_id) values ('db9d12f4-d3ee-4baf-8a16-f21f969deb9d', 101);

insert into sub_account(account_number, currency_code, amount) values ('db9d12f4-d3ee-4baf-8a16-f21f969deb9d', 'PLN', 120.00);
insert into sub_account(account_number, currency_code, amount) values ('db9d12f4-d3ee-4baf-8a16-f21f969deb9d', 'USD', 40.00);
