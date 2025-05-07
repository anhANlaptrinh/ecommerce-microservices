-- Tạo bảng categories
CREATE TABLE categories (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    img VARCHAR(255)
);

-- Tạo bảng products
CREATE TABLE products (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL,
    price_old INTEGER,
    category BIGINT,
    brand VARCHAR(255),
    img VARCHAR(255),
    description TEXT,
    CONSTRAINT fk_product_category FOREIGN KEY (category) REFERENCES categories(id)
);
