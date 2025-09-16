-- Migration V2: Insert roles and initial users
-- Compatible with PostgreSQL

-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create user_roles junction table for many-to-many relationship
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Insert roles
INSERT INTO roles (name, description) VALUES 
('ADMIN', 'Administrator with full system access'),
('CUSTOMER', 'Regular customer with limited access');

-- Insert initial users
-- Password for both users is 'password123' (should be hashed in real application)
INSERT INTO users (username, email, password_hash, first_name, last_name, active, created_at, updated_at) VALUES 
('admin', 'admin@techbra.com', '$2a$10$Zl5kTLz/VFflO7rRZBygNO7xY7YWpvO2Rtpu9mMvUowf8Zz7qs7jC', 'Admin', 'User', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('customer', 'customer@example.com', '$2a$10$Zl5kTLz/VFflO7rRZBygNO7xY7YWpvO2Rtpu9mMvUowf8Zz7qs7jC', 'Customer', 'User', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Assign roles to users
-- Admin User gets ADMIN role
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.email = 'admin@techbra.com' AND r.name = 'ADMIN';

-- Customer User gets CUSTOMER role
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.email = 'customer@example.com' AND r.name = 'CUSTOMER';

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_roles_name ON roles(name);
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);