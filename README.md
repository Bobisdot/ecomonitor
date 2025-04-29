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

### 🔹 Users Table

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL DEFAULT 'user'
);
