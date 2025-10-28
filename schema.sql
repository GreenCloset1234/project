-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: greencloset-db.cdmuuiu6cqu4.ap-northeast-2.rds.amazonaws.com    Database: green_closet_db
-- ------------------------------------------------------
-- Server version	8.0.42

-- MySQL 버전이 4.01.01 이상일 경우에만 주석 처리된 코드 실행하.
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
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '';

--
-- Table structure for table `Chat_Messages`
--

-- (`) 백틱 기호는 테이블이나 컬럼 이름이 SQL 예약어와 겹치거나 특수문자를 포함할 때 감싸주는 역할.
DROP TABLE IF EXISTS `Chat_Messages`; -- 이 코드를 여러번 실행해도 오류가 나지 않게 Chat_Messages 테이블이 이미 있다면 먼저 삭제.
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Chat_Messages` (
  `message_id` bigint NOT NULL AUTO_INCREMENT, -- AUTO_INCREMENT는 새 데이터가 추가될 때마다 DB가 자동으로 1, 2, 3, ... 숫자 부여.
  `room_id` bigint DEFAULT NULL,
  `sender_id` bigint NOT NULL,
  `content` text,
  `sent_at` datetime NOT NULL,
  PRIMARY KEY (`message_id`),
  KEY `room_id` (`room_id`), -- room_id를 KEY로 지정하면 데이터를 검색할때 속도가 빨라짐.
  KEY `sender_id` (`sender_id`),
  CONSTRAINT `Chat_Messages_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `Chat_rooms` (`room_id`),
  CONSTRAINT `Chat_Messages_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `Users` (`user_id`)
  -- Chat_Message_ibfk_1라는 이름의 제약 조건(CONSTRAINT) 만들기.
  -- room_id는 Chat_rooms 테이블의 roon_id를 참조(REFERENCES).
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
-- ENGINE=InnoDB DEFAULT : 테이블 작동 방식(엔진)을 InnoDB로 설정.
-- CHARSET=utf8mb4 : 문자 저장 방식을 utf8mb4로 설정. (utf8는 3byte, utf8mb4는 4byte여서 utf8mb4로는 이모지를 포함한 전 세계 모든 문자 표현 가능.)
-- COLLATE=utf8mb4_0900_ai_ci : 문자 정렬 규칙 설정. utf8mb4_0900_ai_ci는 유니코드 9.0버전을 기반으로 ai(악센트(억양부호))와 ci(대소문자)를 무시함.

/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Chat_rooms`
--

DROP TABLE IF EXISTS `Chat_rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Chat_rooms` (
  `room_id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `buyer_id` bigint NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`room_id`),
  KEY `product_id` (`product_id`), -- product_id와 buyer_id에도 KEY로 지정해서 검색 속도 향상.
  KEY `buyer_id` (`buyer_id`),
  CONSTRAINT `Chat_rooms_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `Products` (`product_id`),
  CONSTRAINT `Chat_rooms_ibfk_2` FOREIGN KEY (`buyer_id`) REFERENCES `Users` (`user_id`)
  -- Chatrooms는 특정 Product와 특정 Users를 연결해줌.
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Products`
--

DROP TABLE IF EXISTS `Products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Products` (
  `product_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `content` text,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `product_image_url` varchar(500) NOT NULL,
  PRIMARY KEY (`product_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `Products_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Trades`
--

DROP TABLE IF EXISTS `Trades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Trades` (
  `trade_id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint DEFAULT NULL,
  `buyer_id` bigint DEFAULT NULL,
  `completed_at` datetime NOT NULL,
  PRIMARY KEY (`trade_id`),
  UNIQUE KEY `product_id` (`product_id`), -- UNIQUE KEY(고유키)
  -- KEY(인덱스)와 비슷.
  -- 하지만 UNIQUE는 이 컬럼에 중복된 값이 들어올 수 없다는 뜻.
  -- 하나의 product_id는 한 번만 Trades 될 수 있다.
  KEY `buyer_id` (`buyer_id`),
  CONSTRAINT `Trades_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `Products` (`product_id`),
  CONSTRAINT `Trades_ibfk_2` FOREIGN KEY (`buyer_id`) REFERENCES `Users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `profile_image_url` varchar(500) DEFAULT NULL,
  `introduction` text,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`) -- email을 UNIQUE로 설정해서 동일한 이메일로 중복 가입되는 것을 방지.
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

-- 덤프 시작에서 변경했던 서버 설정 값들을 원래대로 복원.
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-28  9:30:17
