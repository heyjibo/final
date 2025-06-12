-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: dp
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `administrator`
--

DROP TABLE IF EXISTS `administrator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `administrator` (
  `id` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `administrator`
--

LOCK TABLES `administrator` WRITE;
/*!40000 ALTER TABLE `administrator` DISABLE KEYS */;
INSERT INTO `administrator` VALUES ('Alice','123456'),('Bob','123456');
/*!40000 ALTER TABLE `administrator` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `customerid` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `num_of_order` int NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phonenumber` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`customerid`),
  UNIQUE KEY `customerID_UNIQUE` (`customerid`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES ('100000','Boyd','ST405',0,'123456','13565629356'),('100001','tom','wasd',0,'123456aaA','13345678954'),('100002','DASD','asdas',0,'1234567Aaa','13546135442');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `deliver`
--

DROP TABLE IF EXISTS `deliver`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deliver` (
  `work_id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `age` int NOT NULL,
  `numofindent` int NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phonenumber` varchar(255) DEFAULT NULL,
  `score` double DEFAULT NULL,
  PRIMARY KEY (`work_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deliver`
--

LOCK TABLES `deliver` WRITE;
/*!40000 ALTER TABLE `deliver` DISABLE KEYS */;
INSERT INTO `deliver` VALUES ('10001','Jack',23,1,'123456','13265462648',0),('10002','Clove',21,1,'123456','15856546266',2),('10003','Joe',20,0,'123456','16542658954',0);
/*!40000 ALTER TABLE `deliver` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dishs`
--

DROP TABLE IF EXISTS `dishs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dishs` (
  `id` varchar(255) NOT NULL,
  `merchantid` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `unit-price` int DEFAULT NULL,
  `sales-volume` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dishs`
--

LOCK TABLES `dishs` WRITE;
/*!40000 ALTER TABLE `dishs` DISABLE KEYS */;
INSERT INTO `dishs` VALUES ('1','1','badapple',2,7),('2','1','banana',3,10),('3','1','water',1,7),('4','1','watermelon',2,3),('5','2','apple',3,1),('6','2','bluebarry',5,3),('7','2','A4paper',12,3);
/*!40000 ALTER TABLE `dishs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `informationofindent`
--

DROP TABLE IF EXISTS `informationofindent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `informationofindent` (
  `id` varchar(255) NOT NULL,
  `content` varchar(255) NOT NULL,
  `customerID` varchar(255) NOT NULL,
  `Distance` int NOT NULL,
  `merchant_core` double DEFAULT NULL,
  `deliverid` varchar(255) DEFAULT NULL,
  `Money` int NOT NULL,
  `Time` datetime DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `merchantid` varchar(255) DEFAULT NULL,
  `completion_time` datetime(6) DEFAULT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `pickup_time` datetime(6) DEFAULT NULL,
  `deliver_money` int DEFAULT NULL,
  `deliver_score` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `informationofindent`
--

LOCK TABLES `informationofindent` WRITE;
/*!40000 ALTER TABLE `informationofindent` DISABLE KEYS */;
INSERT INTO `informationofindent` VALUES ('1','1,2,3,4','100000',17,3.2,'10002',8,'2025-05-19 22:17:32','Completed','1','2025-05-19 22:21:32.628407','2025-05-19 22:15:43.031360','2025-05-19 22:18:21.474993',3,2),('10','1,2,3','100000',21,0,NULL,6,'2025-05-22 07:01:07','Pending','1',NULL,'2025-05-22 06:58:17.181171',NULL,3,0),('2','7,6','100000',12,0,'10001',17,'2025-05-22 00:47:18','Picking Up','2',NULL,'2025-05-22 00:46:26.996780','2025-05-22 00:47:33.148467',3,0),('4','1,2','100000',21,0,'10002',5,'2025-05-22 02:38:15','Pending','1',NULL,'2025-05-22 00:51:41.940104',NULL,3,0),('5','1,2','100000',10,0,'10002',5,'2025-05-22 02:38:19','Pending','1',NULL,'2025-05-22 00:51:45.300659',NULL,3,0),('6','1,2,3','100000',12,0,'10001',6,'2025-05-22 02:38:21','Picking Up','1',NULL,'2025-05-22 00:51:49.928628','2025-05-22 02:38:31.609429',3,0),('7','4,3,2','100000',14,0,'10001',6,'2025-05-22 02:45:17','Picking Up','1',NULL,'2025-05-22 00:52:03.994397','2025-05-22 02:46:39.221701',3,0),('8','1,2','100000',22,0,'10001',5,'2025-05-22 02:45:19','Completed','1','2025-05-22 02:46:46.795548','2025-05-22 02:44:22.848822','2025-05-22 02:46:41.805350',3,0),('9','3,2','100000',21,0,'10001',4,'2025-05-22 07:01:15','Picking Up','1',NULL,'2025-05-22 02:44:28.842795','2025-05-22 07:02:30.166313',3,0);
/*!40000 ALTER TABLE `informationofindent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant`
--

DROP TABLE IF EXISTS `merchant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant` (
  `id` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `score` double DEFAULT NULL,
  `numof_indent` int DEFAULT NULL,
  `phonenumber` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant`
--

LOCK TABLES `merchant` WRITE;
/*!40000 ALTER TABLE `merchant` DISABLE KEYS */;
INSERT INTO `merchant` VALUES ('1','123456','store',0.32,10,'13546465689'),('2','123456','Store1',0,3,'13546268624'),('3','123456','store2',0,0,'16549326542');
/*!40000 ALTER TABLE `merchant` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-30  0:48:33
