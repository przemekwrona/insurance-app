insert into app_user (user_id, firstname, surname) values (102, 'Laren', 'Pateman');

insert into account(account_number, user_id) values ('5079bcf9-d913-4fa5-ac96-4bb89f452775', 102);

insert into sub_account(account_number, currency_code, amount) values ('5079bcf9-d913-4fa5-ac96-4bb89f452775', 'PLN', 120.00);
insert into sub_account(account_number, currency_code, amount) values ('5079bcf9-d913-4fa5-ac96-4bb89f452775', 'USD', 40.00);
