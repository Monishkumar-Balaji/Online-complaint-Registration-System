# 📌 Online Complaint Registration and Tracking System

A full-stack web application that allows users to register complaints, track their status, and enables administrators to manage and resolve complaints efficiently.

Developed using **HTML, CSS, JavaScript, Java Servlets, and MySQL**.

---

## 🧱 Tech Stack

**Frontend**
- HTML  
- CSS  
- JavaScript  

**Backend**
- Java Servlets  
- JDBC  

**Database**
- MySQL  

**Server**
- Apache Tomcat 10  

---

## 🚀 Features

### 👤 User Module
- User registration and login  
- Submit complaints  
- Track complaint status  
- Withdraw pending complaints  
- Search complaints  
- Session-based authentication  

### 👨‍💼 Admin Module
- Admin login  
- View all complaints  
- Update complaint status  
- Add remarks  
- Filter and search complaints  
- Analytics dashboard  
- Category-wise chart  
- Logout functionality  

---

## 🗄 Database Schema

### `users`
id (PK)
name
email
password


### `complaints`
id (PK)
user_id (FK)
category
description
status
remarks
created_at


### `admin`
id (PK)
username
password


---

## 🛠 Setup Instructions

### 1️⃣ Create Database
```sql
CREATE DATABASE complaint_system;
USE complaint_system;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(100)
);

CREATE TABLE complaints (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    category VARCHAR(50),
    description TEXT,
    status ENUM('Pending','In Progress','Resolved','Withdrawn') DEFAULT 'Pending',
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(50)
);

INSERT INTO admin(username,password)
VALUES('admin','admin123');
```


### 2️⃣ Configure Database in Servlets
Update JDBC credentials in servlets:

String url = "jdbc:mysql://localhost:3306/complaint_system";

String user = <your_username>;

String password = <your_password>;



### 3️⃣ Add MySQL Connector

Download MySQL Connector JAR and place it inside:

WEB-INF/lib


### 4️⃣  Run Project
Start Tomcat server

Deploy project

Open browser

User login

http://localhost:8080/project/login.html
Admin login

http://localhost:8080/project/admin_login.html

### 🔄 Workflow
User registers and logs in

User submits complaint

Complaint stored in database

User tracks complaint status

Admin logs in

Admin updates status and remarks

User sees updated status

### 🔐 Security Features
Session-based authentication

PreparedStatement (SQL injection safe)

Duplicate complaint prevention

Role-based access control

Logout with session invalidation

Cache disabled after logout

### 📊 Complaint Status Flow
Pending → In Progress → Resolved
Users can withdraw complaints only when status is Pending.

### 🏗 System Architecture
Browser (HTML/CSS/JS)
        ↓
Java Servlets (Tomcat)
        ↓
MySQL Database

### 👨‍💻 Author
Monishkumar Balaji
Computer Science Engineering Student

### 📄 License
This project is for academic/educational use.
