USE green_closet_db;

-- 1. Users (회원) 테이블 생성
CREATE TABLE Users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    nickname VARCHAR(500),
    profile_image_url VARCHAR(500),
    introduction TEXT,
    created_at DATETIME NOT NULL
);

-- 2. Products (상품) 테이블 생성
CREATE TABLE Products (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    product_image_url VARCHAR(500) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- 3. Trades (교환) 테이블 생성
CREATE TABLE Trades (
    trade_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT UNIQUE,
    buyer_id BIGINT,
    completed_at DATETIME NOT NULL,
    total BIGINT NOT NULL DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES Products(product_id),
    FOREIGN KEY (buyer_id) REFERENCES Users(user_id)
);

-- 4. Chat_rooms (채팅방) 테이블 생성
CREATE TABLE Chat_rooms (
    room_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (product_id) REFERENCES Products(product_id),
    FOREIGN KEY (buyer_id) REFERENCES Users(user_id)
);

-- 5. Chat_Messages (채팅 메시지) 테이블 생성
CREATE TABLE Chat_Messages (
    message_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT,
    sender_id BIGINT NOT NULL,
    content TEXT,
    sent_at DATETIME NOT NULL,
    FOREIGN KEY (room_id) REFERENCES Chat_rooms(room_id),
    FOREIGN KEY (sender_id) REFERENCES Users(user_id)
);