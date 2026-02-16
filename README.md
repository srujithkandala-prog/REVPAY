RevPay – Digital Wallet & Payment Management System
📌 Project Overview

RevPay is a console-based Digital Wallet and Payment Management System built using Java (Core Java + JDBC) and MySQL.

The system simulates real-world fintech applications like Paytm and PhonePe by providing secure wallet transactions, invoice management, money requests, and loan processing with role-based access control.

🛠️ Tech Stack

Language: Java (Core Java)

Database: MySQL

Connectivity: JDBC

Security: SHA-256 Password Hashing

Architecture: DAO Pattern

IDE: Eclipse

🏗️ Project Architecture
RevPayApp
│
├── controller
│   └── MainApp.java
│
├── dao
│   └── UserDAO.java
│
├── model
│   └── User.java
│
├── util
│   ├── DBConnection.java
│   └── PasswordUtil.java


Controller Layer → Handles user interaction

DAO Layer → Handles database operations

Model Layer → Represents data objects

Utility Layer → Database connection & hashing logic

👥 User Roles
1️⃣ Personal User

Register & Login

Wallet management

Add / Withdraw money

Send money (with 6-digit Transaction PIN)

Request money

Accept money requests

View transaction history

View & Pay invoices

Change password

Forgot password (Security Question based)

2️⃣ Business User

All Personal user features

Create invoices

View invoices

Apply for loans

View business revenue analytics

Receive invoice payments

3️⃣ Admin User

View all users

Unlock locked accounts

View pending loan applications

Approve loans

🔐 Security Features

SHA-256 password hashing

6-digit Transaction PIN verification for money transfer

Account locking after 3 failed login attempts

Email format validation

Strong password validation

Payment method validation

Foreign key constraints for data integrity

🗄️ Database Design

Tables Used:

users

payment_methods

transactions

money_requests

notifications

invoices

loans

Features:

ENUM constraints

Foreign key relationships

Account status control

Transaction history tracking

💰 Core Functional Modules
Wallet System

Add money

Withdraw money

View balance

Transaction System

SEND

REQUEST

INVOICE_PAYMENT

ADD

WITHDRAW

Invoice Flow

Business creates invoice

Customer views unpaid invoices

Customer pays invoice

System updates balances

Transaction recorded

Loan Flow

Business applies for loan

Admin approves loan

Loan status updated

📊 ER Diagram

<img width="1024" height="1024" alt="image" src="https://github.com/user-attachments/assets/f57c30d1-7753-4c80-bbae-c6c247d281cb" /># REVPAY



🚀 How to Run the Project

1️⃣ Clone the Repository

git clone https://github.com/srujithkandala-prog/REVPAY.git

2️⃣ Setup MySQL Database

Create database RevPayApp

Run provided SQL schema file

3️⃣ Configure Database Connection

Update DBConnection.java:

private static final String URL = "jdbc:mysql://localhost:3306/RevPayApp";
private static final String USER = "root";
private static final String PASSWORD = "your_password";





4️⃣ Run Application

Run MainApp.java

📈 Key Learning Outcomes

JDBC database integration

Role-based access control

Secure password storage

Financial transaction logic

DAO architecture pattern

Input validation & error handling

Foreign key relationship management

📌 Future Enhancements

Convert to Spring Boot REST API

Web-based frontend (React / Angular)

JWT Authentication

AES encryption for card numbers

Payment gateway integration

Docker deployment

📷 Sample Console Output
===== REV PAY =====
1. Register
2. Login
3. Forgot Password
4. Exit
