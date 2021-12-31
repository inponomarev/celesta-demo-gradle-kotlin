create schema orders version '1.0';

create sequence customer_id;

create table customer (
    id int not null default nextval(customer_id) primary key,
    name varchar(50) not null,
    email varchar(50),
    description varchar(200)
);

create index customer_idx on customer(name);

create table item (
    id varchar(12) not null primary key,
    name varchar(100) not null,
    default_price int
);

create sequence order_id;

create table order (
    id int not null default nextval(order_id) primary key,
    customer_id int not null foreign key references customer(id),
    item_id varchar(12) not null foreign key references item(id),
    quantity int not null default 0,
    price int not null default 0,
    amount int not null default 0
);

create materialized view item_orders as
    select item_id, sum(quantity) as ordered_quantity from order group by item_id;

create view item_view as
    select
      i.id as id,
      i.name as name,
      o.ordered_quantity as ordered_quantity
    from item as i left join item_orders as o on i.id = o.item_id;
