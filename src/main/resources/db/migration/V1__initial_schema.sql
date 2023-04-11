create table products (
    id bigint primary key,
    name text not null,
    unit_price integer not null check (unit_price > 0)
);

create table multi_unit_discount (
    product_id bigint not null references products (id),
    num_units integer not null check (num_units > 0),
    price integer not null check (price > 0)
)
