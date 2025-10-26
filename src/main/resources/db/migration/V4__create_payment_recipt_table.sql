-- Flyway migration: create payment_recipt table
-- Matches the PaymentReceipt entity (@Table(name = "payment_recipt"))

CREATE TABLE IF NOT EXISTS payment_recipt (
  payment_id INT PRIMARY KEY AUTO_INCREMENT,
  order_id INT NOT NULL,
  paid_by INT NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  paid_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  method VARCHAR(15) NOT NULL,
  received_by INT,
  CONSTRAINT fk_payment_order
    FOREIGN KEY (order_id) REFERENCES customer_orders(order_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_payment_customer
    FOREIGN KEY (paid_by) REFERENCES user(user_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_payment_staff
    FOREIGN KEY (received_by) REFERENCES user(user_id)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

