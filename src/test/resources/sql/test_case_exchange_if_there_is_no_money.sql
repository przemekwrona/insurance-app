insert into app_user (user_id, firstname, surname) values (100, 'john', 'evans');

insert into account(account_number, user_id) values ('784dea12-6940-4873-8bcf-216a68b57638', 100);

insert into sub_account(account_number, currency_code, amount) values ('784dea12-6940-4873-8bcf-216a68b57638', 'PLN', 100.00);
insert into sub_account(account_number, currency_code, amount) values ('784dea12-6940-4873-8bcf-216a68b57638', 'USD', 50.00);
