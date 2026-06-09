# Online Voting System

## Project Description
Online Voting System is a console-based Java application developed using Java, JDBC, and MySQL. This project allows voter registration, voter login, candidate management, and viewing candidate details through a database-connected system.

## Technologies Used
- Java
- JDBC
- MySQL
- IntelliJ IDEA

## Features
- Voter Registration
- Voter Login
- Add Candidate
- View Candidates
- Database Connectivity using JDBC
- MySQL Integration

## Database Tables

### voters
```sql
CREATE TABLE voters (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    username VARCHAR(100),
    password VARCHAR(100)
);
```

### candidates
```sql
CREATE TABLE candidates (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    party VARCHAR(100)
);
```

## Project Structure

```text
src
│
├── Main.java
├── DBConnection.java
├── Voter.java
├── voterDAO.java
├── candidate.java
└── candidateDAO.java
```

## How to Run
1. Install MySQL and create the database.
2. Update database credentials in `DBConnection.java`.
3. Add MySQL JDBC Connector JAR.
4. Run `Main.java`.
5. Select menu options to use the application.

## Sample Menu

```text
===== ONLINE VOTING SYSTEM =====
1. Register Voter
2. Login Voter
3. Add Candidate
4. View Candidates
5. Exit
```

## Author
Mahesh

## Future Enhancements
- Cast Vote Feature
- Vote Result Display
- Prevent Duplicate Voting
- Admin Login
- GUI using Java Swing
