Create database library_ms_db;
Use library_ms_db;

Select * From Book;
Select * From Member;

-- Create BOOK table
CREATE TABLE Book (
    book_id VARCHAR(10) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100),
    category VARCHAR(100),
    publisher VARCHAR(100),
    isbn VARCHAR(20) UNIQUE,
    copies_available INT NOT NULL
);

ALTER TABLE book
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;


-- Create MEMBER table
CREATE TABLE Member (
    member_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(15),
    address TEXT,
    registered_date DATE
);

ALTER TABLE Member
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE Member
ADD COLUMN nic VARCHAR(20) UNIQUE; -- Or appropriate length and constraints

-- Create STAFF table
CREATE TABLE Staff (
    staff_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'librarian') NOT NULL,
    email VARCHAR(100)
);

-- Create BORROWING table
CREATE TABLE Borrowing (
    borrow_id VARCHAR(10) PRIMARY KEY,
    member_id VARCHAR(10),
    book_id VARCHAR(10),
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status ENUM('borrowed', 'returned', 'late') DEFAULT 'borrowed',
    FOREIGN KEY (member_id) REFERENCES Member(member_id),
    FOREIGN KEY (book_id) REFERENCES Book(book_id)
);

-- Create FINE table
CREATE TABLE Fine (
    fine_id VARCHAR(10) PRIMARY KEY,
    borrow_id VARCHAR(10) UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    paid ENUM('yes', 'no') DEFAULT 'no',
    FOREIGN KEY (borrow_id) REFERENCES Borrowing(borrow_id)
);

