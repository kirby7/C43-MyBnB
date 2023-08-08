# Insert 5 addresses for users/properties
INSERT INTO address (address, city, country, postal_code) VALUES ("180 Test Lane", "Toronto",
"Canada", "A1A 2B2");
INSERT INTO address (address, city, country, postal_code) VALUES ("25 Default Street",
"Toronto", "Canada", "B2B 3C3");
INSERT INTO address (address, city, country, postal_code) VALUES ("5000 Default Way",
"Toronto", "Canada", "C3C 4D4");
INSERT INTO address (address, city, country, postal_code) VALUES ("127 Distant Avenue",
"Vancouver", "Canada", "X1X 2Z2");
INSERT INTO address (address, city, country, postal_code) VALUES ("3 Country Ridge Drive",
"Markham", "Canada", "D4D 5E5");

# Insert 3 generic hosting users
INSERT INTO user (username, first_name, last_name, dob, sin, occupation, aid, `password`)
VALUES ("testman", "Tester", "Mann", "2000-01-01", "230358855", "Professional Tester",
1, "123");
INSERT INTO user (username, first_name, last_name, dob, sin, occupation, aid, `password`)
VALUES ("propertymogul", "Tim", "Apple", "1980-12-01", "149821365", "CEO", 3, "123");
INSERT INTO user (username, first_name, last_name, dob, sin, occupation, aid, `password`)
VALUES ("richman", "Richard", "Dicksom", "1962-03-27", "310961966", "Retired", 5, "123");

# Insert 5 listings and list dates
INSERT INTO listing (listing_type, latitude, longitude, aid, username) VALUES (0, 50.1200,
49.88156, 1, "testman");
INSERT INTO list_date (lid, list_date, price) VALUES (1, '2023-08-14', 200.00);
INSERT INTO list_date (lid, list_date, price) VALUES (1, '2023-08-15', 255.25);
INSERT INTO list_date (lid, list_date, price) VALUES (1, '2023-08-16', 220.00);
INSERT INTO list_date (lid, list_date, price) VALUES (1, '2023-08-17', 205.90);

INSERT INTO listing (listing_type, latitude, longitude, aid, username) VALUES (1, 49.9900,
49.82955, 2, "testman");
INSERT INTO list_date (lid, list_date, price) VALUES (2, '2023-08-14', 182.00);
INSERT INTO list_date (lid, list_date, price) VALUES (2, '2023-08-15', 181.50);
INSERT INTO list_date (lid, list_date, price) VALUES (2, '2023-08-16', 182.00);
INSERT INTO list_date (lid, list_date, price) VALUES (2, '2023-08-17', 181.50);

INSERT INTO listing (listing_type, latitude, longitude, aid, username) VALUES (2, 50.00,
51.01211142, 3, "propertymogul");
INSERT INTO list_date (lid, list_date, price) VALUES (3, '2023-08-14', 552.00);
INSERT INTO list_date (lid, list_date, price) VALUES (3, '2023-08-15', 560.00);
INSERT INTO list_date (lid, list_date, price) VALUES (3, '2023-08-16', 575.00);
INSERT INTO list_date (lid, list_date, price) VALUES (3, '2023-08-17', 600.00);

INSERT INTO listing (listing_type, latitude, longitude, aid, username) VALUES (3, -27.2901,
104.992435, 4, "propertymogul");
INSERT INTO list_date (lid, list_date, price) VALUES (4, '2023-08-14', 900.00);
INSERT INTO list_date (lid, list_date, price) VALUES (4, '2023-08-15', 900.00);
INSERT INTO list_date (lid, list_date, price) VALUES (4, '2023-08-16', 900.00);
INSERT INTO list_date (lid, list_date, price) VALUES (4, '2023-08-17', 900.90);

INSERT INTO listing (listing_type, latitude, longitude, aid, username) VALUES (0, 49.3675,
51.2222222, 5, "richman");
INSERT INTO list_date (lid, list_date, price) VALUES (5, '2023-08-21', 200.00);
INSERT INTO list_date (lid, list_date, price) VALUES (5, '2023-08-22', 255.25);
INSERT INTO list_date (lid, list_date, price) VALUES (5, '2023-08-23', 220.00);
INSERT INTO list_date (lid, list_date, price) VALUES (5, '2023-08-24', 205.90);
