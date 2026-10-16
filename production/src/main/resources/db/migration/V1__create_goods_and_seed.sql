CREATE TABLE goods (
                       goods_type VARCHAR(50) PRIMARY KEY,
                       timestamp  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       price      INT NOT NULL,
                       quantity   INT NOT NULL
);

INSERT INTO goods (goods_type, price, quantity) VALUES ('CUCUMBER', 150, 50);
INSERT INTO goods (goods_type, price, quantity) VALUES ('CARROTS', 100, 100);
INSERT INTO goods (goods_type, price, quantity) VALUES ('TOBACCO', 100, 50);
INSERT INTO goods (goods_type, price, quantity) VALUES ('EGGS', 80, 75);
INSERT INTO goods (goods_type, price, quantity) VALUES ('POTATO', 60, 200);
INSERT INTO goods (goods_type, price, quantity) VALUES ('PLUMS', 120, 80);