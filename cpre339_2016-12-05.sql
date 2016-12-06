# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: 127.0.0.1 (MySQL 5.6.13)
# Database: cpre339
# Generation Time: 2016-12-06 04:19:14 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table BookComments
# ------------------------------------------------------------

DROP TABLE IF EXISTS `BookComments`;

CREATE TABLE `BookComments` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `CommentText` varchar(500) DEFAULT NULL,
  `Poster` varchar(200) DEFAULT NULL,
  `BookID` int(11) DEFAULT NULL,
  `TimePosted` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



# Dump of table Books
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Books`;

CREATE TABLE `Books` (
  `BookID` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `Description` varchar(200) DEFAULT NULL,
  `BookName` varchar(200) DEFAULT NULL,
  `ISBN` int(20) DEFAULT NULL,
  `OwnerEmail` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`BookID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

LOCK TABLES `Books` WRITE;
/*!40000 ALTER TABLE `Books` DISABLE KEYS */;

INSERT INTO `Books` (`BookID`, `Description`, `BookName`, `ISBN`, `OwnerEmail`)
VALUES
	(1,'tfs','geg',123457963,'chris@bild.org');

/*!40000 ALTER TABLE `Books` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table Events
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Events`;

CREATE TABLE `Events` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `EventName` varchar(200) DEFAULT NULL,
  `EventDescription` varchar(200) DEFAULT NULL,
  `OwnerEmail` varchar(200) DEFAULT NULL,
  `Ranking` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

LOCK TABLES `Events` WRITE;
/*!40000 ALTER TABLE `Events` DISABLE KEYS */;

INSERT INTO `Events` (`id`, `EventName`, `EventDescription`, `OwnerEmail`, `Ranking`)
VALUES
	(1,'h','d','chris@bild.org',0);

/*!40000 ALTER TABLE `Events` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table GroupComments
# ------------------------------------------------------------

DROP TABLE IF EXISTS `GroupComments`;

CREATE TABLE `GroupComments` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `CommentText` varchar(500) DEFAULT NULL,
  `Poster` varchar(200) DEFAULT NULL,
  `GroupID` int(11) DEFAULT NULL,
  `TimePosted` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



# Dump of table Groups
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Groups`;

CREATE TABLE `Groups` (
  `GroupName` varchar(200) DEFAULT NULL,
  `GroupOwnerEmail` varchar(200) DEFAULT NULL,
  `Description` varchar(500) DEFAULT NULL,
  `Tag` varchar(200) DEFAULT NULL,
  `GroupID` int(11) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`GroupID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

LOCK TABLES `Groups` WRITE;
/*!40000 ALTER TABLE `Groups` DISABLE KEYS */;

INSERT INTO `Groups` (`GroupName`, `GroupOwnerEmail`, `Description`, `Tag`, `GroupID`)
VALUES
	('df','chris@bild.org','lljf','some tag',1),
	('dsj','chris@bild.org','hsc','some tag',2),
	('group1','chris@bild.org','first real group!!!','some tag',3);

/*!40000 ALTER TABLE `Groups` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table Notifications
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Notifications`;

CREATE TABLE `Notifications` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `NotificationText` varchar(200) DEFAULT NULL,
  `Recipient` varchar(200) DEFAULT NULL,
  `TimeSent` timestamp NULL DEFAULT NULL,
  `Tag` varchar(200) DEFAULT NULL,
  `Identifier` int(11) DEFAULT NULL,
  `Sender` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



# Dump of table Users
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Users`;

CREATE TABLE `Users` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `FirstName` varchar(200) DEFAULT NULL,
  `LastName` varchar(200) DEFAULT NULL,
  `UserEmail` varchar(200) DEFAULT NULL,
  `Description` varchar(500) DEFAULT NULL,
  `EventName` varchar(200) DEFAULT NULL,
  `Password` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

LOCK TABLES `Users` WRITE;
/*!40000 ALTER TABLE `Users` DISABLE KEYS */;

INSERT INTO `Users` (`id`, `FirstName`, `LastName`, `UserEmail`, `Description`, `EventName`, `Password`)
VALUES
	(4,'chris','smith','chris@bild.org','Nothing','none','password'),
	(6,'boris','boris','boris@fake.fake','Boris','none','password');

/*!40000 ALTER TABLE `Users` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
