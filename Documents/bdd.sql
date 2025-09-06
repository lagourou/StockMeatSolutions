CREATE TABLE user (
  id INT NOT NULL AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL UNIQUE,
  username VARCHAR(255),
  password VARCHAR(255) NOT NULL,
  reset_token VARCHAR(255),
  token_expiration DATETIME,
  PRIMARY KEY (id)
);
CREATE TABLE product (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  barcode VARCHAR(255) NOT NULL UNIQUE,
  quantity INT NOT NULL,
  weight INT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  status VARCHAR(50),
  category VARCHAR(100) NOT NULL DEFAULT 'Autre',
  PRIMARY KEY (id)
);
CREATE TABLE scan (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT,
  product_id INT,
  date_scan TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  payment_id INT,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES user(id),
  FOREIGN KEY (product_id) REFERENCES product(id),
  FOREIGN KEY (payment_id) REFERENCES payment(id)

);
CREATE TABLE payment (
  id INT NOT NULL AUTO_INCREMENT,
  amount DECIMAL(10,2) NOT NULL,
  type ENUM('CARD', 'CASH') NOT NULL,
  payment_date DATETIME NOT NULL,
  status VARCHAR(50) NOT NULL,
  employee_id INT,
  PRIMARY KEY (id),
  FOREIGN KEY (employee_id) REFERENCES employee(id)
);
