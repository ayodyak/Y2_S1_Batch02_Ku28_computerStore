-- Seed data for `part` table
-- Matches schema: part(name, brand, category, price, stock_level, reorder_level, image_url, description)

INSERT INTO part (name, brand, category, price, stock_level, reorder_level, image_url, description) VALUES
('Intel Core i5-12400F', 'Intel', 'CPU', 179.99, 25, 5, 'https://example.com/images/intel_i5_12400f.jpg', '12th Gen 6-core desktop CPU, great price/performance for gaming and productivity.'),
('AMD Ryzen 5 5600X', 'AMD', 'CPU', 199.99, 18, 4, 'https://example.com/images/ryzen_5_5600x.jpg', '6-core Zen 3 processor, excellent single-threaded performance.'),
('NVIDIA GeForce RTX 3060', 'NVIDIA', 'GPU', 329.99, 10, 2, 'https://example.com/images/rtx_3060.jpg', '12GB GDDR6, solid 1080p and 1440p performance.'),
('Corsair Vengeance LPX 16GB (2x8GB) DDR4-3200', 'Corsair', 'RAM', 69.99, 40, 10, 'https://example.com/images/corsair_vengeance_16gb.jpg', 'Reliable low-profile DDR4 kit for most builds.'),
('Samsung 970 EVO Plus 1TB', 'Samsung', 'Storage', 129.99, 30, 5, 'https://example.com/images/970_evo_plus_1tb.jpg', 'NVMe M.2 SSD with fast read/write speeds.'),
('Seasonic S12III 650W', 'Seasonic', 'PSU', 79.99, 15, 3, 'https://example.com/images/seasonic_s12iii_650w.jpg', '80+ Bronze certified PSU, reliable for mid-range systems.'),
('ASUS TUF Gaming B550-PLUS', 'ASUS', 'Motherboard', 139.99, 12, 2, 'https://example.com/images/asus_tuf_b550_plus.jpg', 'AM4 ATX motherboard with solid VRM and features for gamers.'),
('Arctic P12 PWM Fan (120mm)', 'Arctic', 'Cooling', 8.99, 120, 20, NULL, 'High airflow PWM fan for case cooling. Quiet and budget-friendly.');

-- End of seed data

