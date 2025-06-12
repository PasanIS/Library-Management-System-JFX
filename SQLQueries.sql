create database library_db;
use library_db;

CREATE TABLE books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    copies INT NOT NULL CHECK (copies >= 0)
);

CREATE TABLE members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(15) NOT NULL,
    nic VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE borrowings (
    borrow_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    book_id INT NOT NULL,
    issue_date DATE NOT NULL,
    return_date DATE,
    fine DECIMAL(10,2) DEFAULT 0.0,
    FOREIGN KEY (member_id) REFERENCES members(member_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id)
);

ALTER TABLE borrowings
MODIFY COLUMN issue_date DATETIME NOT NULL;

INSERT INTO books (isbn, title, author, category, copies) VALUES
('9780140449136', 'The Odyssey', 'Homer', 'Literature', 5),
('9780596009205', 'Head First Java', 'Kathy Sierra', 'Programming', 3),
('9780321356680', 'Effective Java', 'Joshua Bloch', 'Programming', 2),
('9780007350896', 'A Tale of Two Cities', 'Charles Dickens', 'Literature', 4),
('9780192833983', 'The Republic', 'Plato', 'Philosophy', 6);

INSERT INTO members (name, contact, nic) VALUES
('Nimal Perera', '0711234567', '941234567V'),
('Suneth Ranasinghe', '0776543210', '952345678V'),
('Tharushi Dissanayake', '0709876543', '962345671V'),
('Kavindu Madushanka', '0751122334', '971234561V'),
('Dinithi Karunaratne', '0729988776', '981234567V');

select * from members;
select * from books;

SELECT * FROM members;
SELECT DISTINCT category FROM books;

select * from borrowings;

Use Library_db;

ALTER TABLE borrowings 
MODIFY COLUMN return_date datetime;

Desc borrowings;

Desc books;

Select * From books;

Select * From borrowings;

Select * From members;

ALTER TABLE books ADD COLUMN status VARCHAR(20) DEFAULT 'AVAILABLE';





