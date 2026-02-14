# OIB_ATM_SIMULATOR
Multi-Bank ATM System (Java)
Overview

The Multi-Bank ATM System is a console-based banking simulation developed in Java. It models real-world ATM operations including account creation, authentication, deposits, withdrawals, fund transfers, and transaction history management.

The system supports multiple banks and maintains persistent data using Java Serialization, allowing account information and transactions to be stored and reloaded across program executions.

Supported Banks

State Bank of India (SBI)

Bank of Baroda (BOB)

Punjab National Bank (PNB)

Features

Multi-bank account management

Secure login using account number and PIN

Account creation with auto-generated account numbers

Deposit functionality

Withdrawal functionality with balance validation

Inter-bank fund transfer

Transaction history with timestamps

ATM receipt generation

Persistent storage using serialized files

Technologies Used

Java (Core Java)

Object-Oriented Programming (OOP)

Java Collections Framework (HashMap, ArrayList)

File Handling

Java Serialization

Exception Handling

Project Structure

The entire implementation is contained in a single file:

ATMSystem.java

The following classes are included:

Transaction – Represents individual banking transactions

Account – Manages account details and operations

Bank – Handles multiple accounts within a bank

ATMSystem – Main driver class that manages user interaction and system control

A serialized data file named atm_data.ser is automatically created to store account data.

How the System Works

The user selects a bank.

A new account can be created with a 4-digit PIN.

The system generates a unique account number using the bank prefix and a random number.

Users log in using their account number and PIN.

After authentication, users can:

Check balance

Deposit money

Withdraw money

Transfer funds

View transaction history

All data is saved automatically after every operation.
