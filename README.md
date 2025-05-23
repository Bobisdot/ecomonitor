# EcoMonitor

EcoMonitor is a Java GUI CRUD application for managing environmental complaints and news. It uses a MySQL database and provides user-friendly interfaces for submitting, reviewing, and managing environmental issues.

## 📦 Features

- User registration and login
- Submit environmental complaints (title, description, location, type)
- Admin panel to manage complaints and publish news
- View news and complaint status
- Basic role-based access (admin/user)

## 🛠️ Technologies Used

- Java (Swing for GUI)
- MySQL
- JDBC

## 🧩 Database Structure

Database name: **eco_monitor**


###   Table

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL DEFAULT 'user'
);

```
###   Complaints Table
```sql
CREATE TABLE complaints (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(100) NOT NULL,
    complaint_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

###  News Table
```sql
CREATE TABLE news (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

```
How to Run
Import Database

Use eco_monitor file to create the database in MySQL.

Open Project

Open the Java project in NetBeans or IntelliJ IDEA.

Configure DB Connection & Maven

Set your MySQL credentials (username, password) in the JDBC connection file (pom.xml) 

Run the Application

Launch the EcoMonitorGUI.java file to start the application.

Roles
Admin: Manages all complaints and news.

User: Submits and views complaints, reads news.

