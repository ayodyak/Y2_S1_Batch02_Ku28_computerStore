-- Flyway migration: create supplier_part table
CREATE TABLE IF NOT EXISTS supplier_part (
  part_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  brand VARCHAR(255) NOT NULL,
  category VARCHAR(255) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  stock_level INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

