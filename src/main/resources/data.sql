insert into roles (id, role_name) values
                                  (1,'ROLE_ADMIN'),
                                  (2,'ROLE_CUSTOMER')
on conflict (id) do nothing;

insert into categories(name) values ('Electronics');

insert into products(price, category_name, stock, description, name)
VALUES
    (102,'Electronics', 10,'a laptop', 'laptop1'),
    (102,'Electronics',10, 'a laptop', 'laptop2'),
    (102,'Electronics',10, 'a laptop', 'laptop4'),
    (102,'Electronics',10, 'a laptop', 'laptop5'),
    (102,'Electronics',10, 'a laptop', 'laptop6'),
    (102,'Electronics',10, 'a laptop', 'laptop7'),
    (102,'Electronics',10, 'a laptop', 'laptop8'),
    (102,'Electronics',10, 'a laptop', 'laptop9');