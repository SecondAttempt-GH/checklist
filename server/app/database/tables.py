users_table = """
    create table if not exists users(
        id integer primary key autoincrement,
        token int not null unique
    );
"""

shopping_list_table = """
    create table if not exists shopping_list(
        id integer primary key autoincrement,
        user_id int not null,
        product text not null,
        product_quantity int not null default 1,
        is_purchased_product bool default false,
        foreign key (user_id) references users (id)
    );
"""

