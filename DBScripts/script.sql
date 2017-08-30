CREATE TABLE `author` (
  `id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE book (
  id INT NOT NULL AUTO_INCREMENT,
  language varchar(255) DEFAULT NULL,
	published date DEFAULT NULL,
	author_id INT NOT NULL,
	PRIMARY KEY (`id`),
  FOREIGN KEY fk_author(author_id) REFERENCES author(id)
);

INSERT INTO `author` VALUES ('1', 'asim', 'irshad');
INSERT INTO `author` VALUES ('2', 'ali', 'aslam');
INSERT INTO `author` VALUES ('3', 'mark', 'gim');

INSERT INTO `book` VALUES ('1', 'English', '2017-08-02', '1');
INSERT INTO `book` VALUES ('2', 'Urdu', '2017-04-11', '3');
INSERT INTO `book` VALUES ('3', 'English', '2016-11-03', '2');