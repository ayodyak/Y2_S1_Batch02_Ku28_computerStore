-- Create supplier delivery and delivery item tables
CREATE TABLE IF NOT EXISTS supplier_delivery (
  delivery_id INT PRIMARY KEY AUTO_INCREMENT,
  supplier_id INT NOT NULL,
  request_id INT NOT NULL,
  delivery_date DATETIME,
  received_by INT,
  status VARCHAR(20) DEFAULT 'PENDING',
  tracking_info VARCHAR(255),
  CONSTRAINT fk_sd_supplier
    FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_sd_request
    FOREIGN KEY (request_id) REFERENCES purchase_request(request_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_sd_receiver
    FOREIGN KEY (received_by) REFERENCES user(user_id)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS supplier_delivery_item (
  id INT PRIMARY KEY AUTO_INCREMENT,
  delivery_id INT NOT NULL,
  part_id INT NOT NULL,
  quantity_received INT NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_sdi_delivery
    FOREIGN KEY (delivery_id) REFERENCES supplier_delivery(delivery_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_sdi_part
    FOREIGN KEY (part_id) REFERENCES supplier_part(part_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

