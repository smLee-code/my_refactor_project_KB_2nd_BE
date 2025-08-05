-- fund_DB 스키마 생성 (만약 이미 있다면 무시해도 됩니다)
CREATE DATABASE IF NOT EXISTS fund_DB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- fund_DB 스키마 사용
USE fund_DB;

-- tbl_member 테이블 생성
CREATE TABLE IF NOT EXISTS tbl_member (
                                          user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    nickname VARCHAR(100),
    role VARCHAR(50) NOT NULL,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );
ALTER TABLE tbl_member
    ADD COLUMN phone_number VARCHAR(20) AFTER role;

ALTER TABLE tbl_member
    ADD COLUMN phone_number VARCHAR(20) AFTER role;
