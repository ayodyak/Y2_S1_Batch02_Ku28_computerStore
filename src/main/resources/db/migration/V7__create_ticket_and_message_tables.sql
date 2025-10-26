-- Flyway migration: create tickets and ticket_messages tables

CREATE TABLE IF NOT EXISTS `tickets` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `created_at` DATETIME(6) NOT NULL,
  `updated_at` DATETIME(6) NULL,
  `priority` ENUM('HIGH', 'LOW', 'MEDIUM') NOT NULL,
  `status` ENUM('CLOSED', 'IN_PROGRESS', 'OPEN', 'RESOLVED') NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT NULL,
  `assigned_to` INT NULL,
  `created_by` INT NOT NULL,
  `is_deleted` BOOLEAN DEFAULT FALSE,
  CONSTRAINT `fk_tickets_assigned_to_user` FOREIGN KEY (`assigned_to`) REFERENCES `user`(`user_id`),
  CONSTRAINT `fk_tickets_created_by_user` FOREIGN KEY (`created_by`) REFERENCES `user`(`user_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `ticket_messages` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `created_at` DATETIME(6) NOT NULL,
  `message` TEXT NOT NULL,
  `sender_id` INT NULL,
  `ticket_id` BIGINT NOT NULL,
  `is_deleted` BOOLEAN DEFAULT FALSE,
  CONSTRAINT `fk_ticket_messages_sender` FOREIGN KEY (`sender_id`) REFERENCES `user`(`user_id`),
  CONSTRAINT `fk_ticket_messages_ticket` FOREIGN KEY (`ticket_id`) REFERENCES `tickets`(`id`),
  INDEX `idx_ticket_id` (`ticket_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

