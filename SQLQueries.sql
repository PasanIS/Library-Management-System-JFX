-- create database library_db;
-- use library_db;

-- CREATE TABLE books (
--     book_id INT AUTO_INCREMENT PRIMARY KEY,
--     isbn VARCHAR(20) UNIQUE NOT NULL,
--     title VARCHAR(100) NOT NULL,
--     author VARCHAR(100) NOT NULL,
--     category VARCHAR(50),
--     copies INT NOT NULL CHECK (copies >= 0)
-- );

-- CREATE TABLE members (
--     member_id INT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(100) NOT NULL,
--     contact VARCHAR(15) NOT NULL,
--     nic VARCHAR(20) UNIQUE NOT NULL
-- );

-- CREATE TABLE borrowings (
--     borrow_id INT AUTO_INCREMENT PRIMARY KEY,
--     member_id INT NOT NULL,
--     book_id INT NOT NULL,
--     issue_date DATE NOT NULL,
--     return_date DATE,
--     fine DECIMAL(10,2) DEFAULT 0.0,
--     FOREIGN KEY (member_id) REFERENCES members(member_id),
--     FOREIGN KEY (book_id) REFERENCES books(book_id)
-- );

-- ALTER TABLE borrowings
-- MODIFY COLUMN issue_date DATETIME NOT NULL;

-- INSERT INTO books (isbn, title, author, category, copies) VALUES
-- ('9780140449136', 'The Odyssey', 'Homer', 'Literature', 5),
-- ('9780596009205', 'Head First Java', 'Kathy Sierra', 'Programming', 3),
-- ('9780321356680', 'Effective Java', 'Joshua Bloch', 'Programming', 2),
-- ('9780007350896', 'A Tale of Two Cities', 'Charles Dickens', 'Literature', 4),
-- ('9780192833983', 'The Republic', 'Plato', 'Philosophy', 6);

-- INSERT INTO members (name, contact, nic) VALUES
-- ('Nimal Perera', '0711234567', '941234567V'),
-- ('Suneth Ranasinghe', '0776543210', '952345678V'),
-- ('Tharushi Dissanayake', '0709876543', '962345671V'),
-- ('Kavindu Madushanka', '0751122334', '971234561V'),
-- ('Dinithi Karunaratne', '0729988776', '981234567V');

-- select * from members;
-- select * from books;

-- SELECT * FROM members;
-- SELECT DISTINCT category FROM books;

-- select * from borrowings;

-- Use Library_db;

-- ALTER TABLE borrowings 
-- MODIFY COLUMN return_date datetime;

-- Desc borrowings;

-- Desc books;

-- Select * From books;

-- Select * From borrowings;

-- Select * From members;

-- ALTER TABLE books ADD COLUMN status VARCHAR(20) DEFAULT 'AVAILABLE';

-- ---------------------------------------------------------------------------------------
-- Drop and recreate the database
DROP DATABASE IF EXISTS library_db;
CREATE DATABASE library_db;
USE library_db;

-- 1. Authors
CREATE TABLE authors (
    author_id VARCHAR(10) PRIMARY KEY, -- e.g., A001
    name VARCHAR(100) NOT NULL
);

-- 2. Categories
CREATE TABLE categories (
    category_id VARCHAR(10) PRIMARY KEY, -- e.g., C001
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 3. Publishers
CREATE TABLE publishers (
    publisher_id VARCHAR(10) PRIMARY KEY, -- e.g., P001
    name VARCHAR(100) NOT NULL
);

-- 4. Books
CREATE TABLE books (
    book_id VARCHAR(10) PRIMARY KEY, -- e.g., B001
    title VARCHAR(255) NOT NULL,
    author_id VARCHAR(10),
    category_id VARCHAR(10),
    publisher_id VARCHAR(10),
    total_quantity INT NOT NULL,
    available_quantity INT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES authors(author_id),
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (publisher_id) REFERENCES publishers(publisher_id)
);

-- 5. Members
CREATE TABLE members (
    member_id VARCHAR(10) PRIMARY KEY, -- e.g., M001
    full_name VARCHAR(100) NOT NULL,
    nic VARCHAR(12) UNIQUE,
    registered_date DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 6. Contacts
CREATE TABLE contacts (
    contact_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(10),
    address TEXT,
    phone VARCHAR(15),
    email VARCHAR(100),
    FOREIGN KEY (member_id) REFERENCES members(member_id)
);

-- 7. Borrowings
CREATE TABLE borrowings (
    borrow_id VARCHAR(10) PRIMARY KEY, -- e.g., BR001
    member_id VARCHAR(10),
    book_id VARCHAR(10),
    borrow_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    due_date DATE,
    return_date DATE,
    FOREIGN KEY (member_id) REFERENCES members(member_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id)
);

-- 8. Fines
CREATE TABLE fines (
    fine_id VARCHAR(10) PRIMARY KEY, -- e.g., F001
    borrow_id VARCHAR(10),
    fine_amount DECIMAL(10, 2) NOT NULL,
    calculated_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    paid BOOLEAN DEFAULT FALSE,
    paid_date DATE,
    FOREIGN KEY (borrow_id) REFERENCES borrowings(borrow_id)
);





