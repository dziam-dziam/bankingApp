CREATE TABLE items (
    id BIGINT NOT NULL DEFAULT nextval('items_seq'),
    name VARCHAR(255) NOT NULL,
    price NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT items_key PRIMARY KEY (id),
    CONSTRAINT chk_items_price CHECK (price >= 0)
);