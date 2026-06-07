-- Tạo bảng roles
CREATE TABLE roles (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE
);

-- Tạo bảng users
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       enabled BOOLEAN DEFAULT TRUE
);

-- Tạo bảng trung gian user_roles (Nhiều - Nhiều)
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Thêm dữ liệu Roles mặc định
INSERT INTO roles(name) VALUES ('ROLE_DOCTOR'), ('ROLE_ADMIN'), ('ROLE_STAFF');

-- Thêm 1 tài khoản Admin mặc định để test
-- Username: admin
-- Password: password123 (Đã được mã hóa BCrypt)
INSERT INTO users(username, password, enabled)
VALUES ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGzGzU/mZGOjI8Xw28W6', true);

-- Cấp quyền ADMIN cho tài khoản admin (Giả sử admin id = 1, role_admin id = 2)
INSERT INTO user_roles(user_id, role_id) VALUES (1, 2);