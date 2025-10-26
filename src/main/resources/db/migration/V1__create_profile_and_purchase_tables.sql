-- Create employee, supplier, customer profiles (if not present)
CREATE TABLE IF NOT EXISTS `employee` (
  `employee_id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `name` VARCHAR(255),
  `hire_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_employee_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `supplier` (
  `supplier_id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `name` VARCHAR(255),
  `contact` VARCHAR(255),
  CONSTRAINT `fk_supplier_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `customer` (
  `customer_id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `name` VARCHAR(255),
  CONSTRAINT `fk_customer_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Purchase request tables
CREATE TABLE IF NOT EXISTS `purchase_request` (
  `request_id` INT PRIMARY KEY AUTO_INCREMENT,
  `manager_id` INT NOT NULL,
  `supplier_id` INT NOT NULL,
  `request_date` DATETIME NOT NULL,
  `status` VARCHAR(255) NOT NULL,
  `total_amount` DECIMAL(10,2) NOT NULL,
  CONSTRAINT `fk_pr_manager` FOREIGN KEY (`manager_id`) REFERENCES `employee`(`employee_id`),
  CONSTRAINT `fk_pr_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `supplier`(`supplier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `purchase_request_item` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `request_id` INT NOT NULL,
  `part_id` INT NOT NULL,
  `quantity` INT NOT NULL,
  `unit_price` DECIMAL(10,2) NOT NULL,
  `status` VARCHAR(255) NOT NULL,
  CONSTRAINT `fk_pri_request` FOREIGN KEY (`request_id`) REFERENCES `purchase_request`(`request_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_pri_part` FOREIGN KEY (`part_id`) REFERENCES `part`(`part_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

