create schema main;

-- set search_path to main;

create table products (
    id bigserial primary key,
    name text unique not null,
    full_price integer not null
);

create table multi_item_discount (
    product_id bigint not null references products (id),
    num_units decimal (2,0) not null,
    price integer not null
)
