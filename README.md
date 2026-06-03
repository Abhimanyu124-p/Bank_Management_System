The Bank System Architecture: How It Works
This project follows a decoupled, structured design pattern. Instead of putting all the code into one massive file, the responsibilities are separated into distinct modules.

Here is how the data flows through your application:

Plaintext
[ User Interface ]  --->  [ Business Logic Layer ]  --->  [ Database Access Layer ]
(BankingSystem.java)      (AccountManager.java)           (JDBC Driver / MySQL)
                          (Accounts.java / Users.java)

The Entry Point (BankingSystem.java): This is the heart of the user experience. It handles the interactive CLI console loops, prompts users for options, catches their inputs using Scanner, and manages the application state (e.g., keeping track of who is logged in).

The Entities (Users.java & Accounts.java): These classes act as the structural managers. Users.java handles identity (registration and login checking), while Accounts.java manages physical bank accounts (opening an account and generating unique account numbers sequential to the base 10000100).

The Core Engine (AccountManager.java): This module handles the raw banking logic. It handles all critical state changes like adding money (credit), removing money (debit), and handling security pin validations.

Technical Highlights & Design Challenges Solved
When sharing this project, these are the technical achievements you should highlight to demonstrate your engineering skills:

1. Robust Transaction Management (ACID Compliance)
In a banking application, a fund transfer involves two distinct actions: deducting money from Account A and adding it to Account B. If the system crashes right after the deduction, the money vanishes into thin air.

To solve this, this project implements strict transactional integrity using explicit database commits and rollbacks:

Java
con.setAutoCommit(false); // Starts a secure manual transaction block
// ... Execute Debit ...
// ... Execute Credit ...
con.commit();            // Only saves changes if BOTH operations succeed!
If either query fails or an unexpected network exception occurs, the system catches the error and executes con.rollback(), safely reverting the database back to its original state as if nothing happened.

2. Guarding Against SQL Injection
Security is paramount in financial software. Hardcoding user inputs directly into standard SQL queries makes a system vulnerable to dangerous SQL Injection attacks. This project utilizes compiled PreparedStatement interfaces across all database modules.

Instead of concatenating raw strings, the queries use parameters (?), forcing the database to treat user input strictly as literal data rather than executable code:

SQL
SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?;
3. Graceful Input Buffer Management
Handling user interaction via the console stream poses unique text token challenges. Mixing numerical data methods like nextInt() or nextDouble() leaves trailing newline characters (\n) trapped inside the input buffer stream, which often causes subsequent nextLine() invocations to skip prompts entirely.

This project carefully structures its console ingestion, strategically cleansing the stream with preventative token checks to ensure input flow remains smooth:

Java
if (sc.hasNextLine()) {
    sc.nextLine(); // Clears residual newline artifacts before reading text
}
4. Zero-Leak Credential Isolation
Industrial software environments require production database credentials to remain hidden from source control. This project enforces absolute separation of configuration data from execution code.

By building a file-loader via Java's Properties utility, database URLs and secret local passwords reside cleanly within an offline config.properties file, which is actively ignored by version tracking via explicit .gitignore rules.

🎯 Use-Case Walkthrough
To give readers a clear picture of the live execution, here is a functional walkthrough of a standard user journey through the system:

Registration: A guest logs onto the console interface, types their full name, email, and password. Users.java checks for identity duplication, then commits the user data to the system.

Account Opening: Once logged in, the user creates an account profile. The system reviews the database, dynamically calculates the next available ID sequence (e.g., 10000101), establishes a starting balance, and sets a security PIN.

P2P Transfer: The user chooses to move funds. They enter a recipient's ID, an amount, and their secret PIN. The system validates the recipient's existence, checks if the sender has enough funds, ensures the PIN is correct, and processes the transfer inside a protected transaction wrapper.
