ALTER TABLE registrations 
DROP COLUMN driver_id;

DROP TABLE IF EXISTS drivers;

ALTER TABLE registrations 
ADD COLUMN driver_name VARCHAR(50),
ADD COLUMN driver_phone VARCHAR(20);