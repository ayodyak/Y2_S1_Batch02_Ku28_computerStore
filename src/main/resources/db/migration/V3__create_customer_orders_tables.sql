-- Create customer_orders and customer_order_detail tables
CREATE TABLE IF NOT EXISTS `customer_orders` (
  `order_id` INT PRIMARY KEY AUTO_INCREMENT,
  `customer_id` INT NOT NULL,
  `order_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `status` VARCHAR(25) DEFAULT 'PENDING',
  `payment_status` VARCHAR(255) DEFAULT 'UNPAID',
  `total` DECIMAL(10,2) NOT NULL,
  CONSTRAINT `fk_order_customer` FOREIGN KEY (`customer_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `customer_order_detail` (
  `order_detail_id` INT PRIMARY KEY AUTO_INCREMENT,
  `order_id` INT NOT NULL,
  `part_id` INT NOT NULL,
  `quantity` INT NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  CONSTRAINT `fk_cod_order` FOREIGN KEY (`order_id`) REFERENCES `customer_orders`(`order_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cod_part` FOREIGN KEY (`part_id`) REFERENCES `part`(`part_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

