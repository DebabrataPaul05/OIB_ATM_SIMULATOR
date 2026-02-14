import java.io.*;
import java.util.*;

class Transaction implements Serializable {
    String type;
    double amount;
    String details;
    Date date;

    public Transaction(String type, double amount, String details) {
        this.type = type;
        this.amount = amount;
        this.details = details;
        this.date = new Date();
    }

    public String toString() {
        return date + " | " + type + " | " + amount + " | " + details;
    }
}

class Account implements Serializable {
    String accountNumber;
    String name;
    int pin;
    double balance;
    ArrayList<Transaction> transactions;

    public Account(String accountNumber, String name, int pin) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.pin = pin;
        this.balance = 0;
        this.transactions = new ArrayList<>();
    }

    public void deposit(double amount) {
        if (amount <= 0) return;
        balance += amount;
        transactions.add(new Transaction("DEPOSIT", amount, "Self Deposit"));
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) return false;
        if (balance >= amount) {
            balance -= amount;
            transactions.add(new Transaction("WITHDRAW", amount, "Self Withdraw"));
            return true;
        }
        return false;
    }

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public void showTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No Transactions Yet.");
        } else {
            for (Transaction t : transactions) {
                System.out.println(t);
            }
        }
    }
}

class Bank implements Serializable {
    String bankName;
    HashMap<String, Account> accounts;

    public Bank(String bankName) {
        this.bankName = bankName;
        this.accounts = new HashMap<>();
    }

    public Account createAccount(String name, int pin) {
        Random rand = new Random();
        String accNo;

        do {
            accNo = bankName + (10000000 + rand.nextInt(90000000));
        } while (accounts.containsKey(accNo));

        Account acc = new Account(accNo, name, pin);
        accounts.put(accNo, acc);
        return acc;
    }

    public Account authenticate(String accNo, int pin) {
        Account acc = accounts.get(accNo);
        if (acc != null && acc.pin == pin) {
            return acc;
        }
        return null;
    }

    public Account getAccount(String accNo) {
        return accounts.get(accNo);
    }
}

public class ATMSystem {
    static Scanner sc = new Scanner(System.in);
    static HashMap<String, Bank> bankMap = new HashMap<>();
    static final String FILE_NAME = "atm_data.ser";

    public static void main(String[] args) {
        loadData();

        while (true) {
            System.out.println("\n===== MULTI BANK ATM SYSTEM =====");
            System.out.println("1. Open Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    openAccount();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    saveData();
                    System.out.println("Thank You!");
                    System.exit(0);
                default:
                    System.out.println("Invalid Choice");
            }
        }
    }

    static int getIntInput() {
        while (true) {
            try {
                return sc.nextInt();
            } catch (Exception e) {
                System.out.println("Invalid Input! Try again.");
                sc.nextLine();
            }
        }
    }

    static double getDoubleInput() {
        while (true) {
            try {
                return sc.nextDouble();
            } catch (Exception e) {
                System.out.println("Invalid Amount! Try again.");
                sc.nextLine();
            }
        }
    }

    static void openAccount() {
        System.out.println("Select Bank: 1.SBI 2.BOB 3.PNB");
        int ch = getIntInput();
        sc.nextLine();

        String bankName;
        if (ch == 1) bankName = "SBI";
        else if (ch == 2) bankName = "BOB";
        else if (ch == 3) bankName = "PNB";
        else {
            System.out.println("Invalid Bank Choice!");
            return;
        }

        Bank bank = bankMap.get(bankName);
        if (bank == null) {
            bank = new Bank(bankName);
            bankMap.put(bankName, bank);
        }

        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Set 4-digit PIN: ");
        int pin = getIntInput();

        Account acc = bank.createAccount(name, pin);
        saveData();

        System.out.println("Account Created Successfully!");
        System.out.println("Account Number: " + acc.accountNumber);
    }

    static void login() {
        System.out.print("Enter Account Number: ");
        String accNo = sc.next();

        if (accNo.length() < 3) {
            System.out.println("Invalid Account Number!");
            return;
        }

        System.out.print("Enter PIN: ");
        int pin = getIntInput();

        String prefix = accNo.substring(0, 3);
        Bank bank = bankMap.get(prefix);

        if (bank == null) {
            System.out.println("Invalid Bank!");
            return;
        }

        Account acc = bank.authenticate(accNo, pin);

        if (acc == null) {
            System.out.println("Authentication Failed!");
            return;
        }

        atmMenu(acc, bank.bankName);
    }

    static void atmMenu(Account acc, String bankName) {
        while (true) {
            System.out.println("\n--- ATM MENU ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Transaction History");
            System.out.println("6. Logout");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    System.out.println("Balance: " + acc.balance);
                    break;

                case 2:
                    System.out.print("Enter Amount: ");
                    double dep = getDoubleInput();
                    if (dep <= 0) {
                        System.out.println("Amount must be positive!");
                        break;
                    }
                    acc.deposit(dep);
                    saveData();
                    System.out.println("Deposited Successfully!");
                    printSlip(bankName, acc, "DEPOSIT", dep);
                    break;

                case 3:
                    System.out.print("Enter Amount: ");
                    double wd = getDoubleInput();
                    if (!acc.withdraw(wd)) {
                        System.out.println("Insufficient Balance or Invalid Amount!");
                    } else {
                        saveData();
                        System.out.println("Withdraw Successful!");
                        printSlip(bankName, acc, "WITHDRAW", wd);
                    }
                    break;

                case 4:
                    System.out.print("Enter Receiver Acc No: ");
                    String recAccNo = sc.next();

                    if (recAccNo.length() < 3) {
                        System.out.println("Invalid Account Number!");
                        break;
                    }

                    String prefix = recAccNo.substring(0, 3);
                    Bank recBank = bankMap.get(prefix);

                    if (recBank == null) {
                        System.out.println("Invalid Receiver Bank!");
                        break;
                    }

                    Account receiver = recBank.getAccount(recAccNo);

                    if (receiver == null) {
                        System.out.println("Receiver Not Found!");
                        break;
                    }

                    System.out.print("Enter Amount: ");
                    double amt = getDoubleInput();

                    if (amt <= 0 || acc.balance < amt) {
                        System.out.println("Insufficient Balance or Invalid Amount!");
                        break;
                    }

                    acc.balance -= amt;
                    receiver.balance += amt;

                    acc.addTransaction(new Transaction("TRANSFER", amt, "To " + recAccNo));
                    receiver.addTransaction(new Transaction("TRANSFER", amt, "From " + acc.accountNumber));

                    saveData();
                    System.out.println("Transfer Successful!");
                    printSlip(bankName, acc, "TRANSFER", amt);
                    break;

                case 5:
                    acc.showTransactions();
                    break;

                case 6:
                    saveData();
                    return;

                default:
                    System.out.println("Invalid Choice");
            }
        }
    }

    static void printSlip(String bankName, Account acc, String type, double amount) {
        System.out.println("\n=================================");
        System.out.println("           ATM RECEIPT           ");
        System.out.println("=================================");
        System.out.println("Bank Name      : " + bankName);
        System.out.println("Account Number : " + acc.accountNumber);
        System.out.println("Account Holder : " + acc.name);
        System.out.println("---------------------------------");
        System.out.println("Transaction    : " + type);
        System.out.println("Amount         : " + amount);
        System.out.println("Available Bal  : " + acc.balance);
        System.out.println("Date & Time    : " + new Date());
        System.out.println("---------------------------------");
        System.out.println("     THANK YOU FOR BANKING       ");
        System.out.println("=================================\n");
    }

    static void saveData() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME));
            out.writeObject(bankMap);
            out.close();
        } catch (Exception e) {
            System.out.println("Error Saving Data");
        }
    }

    static void loadData() {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME));
            bankMap = (HashMap<String, Bank>) in.readObject();
            in.close();
        } catch (Exception e) {
            bankMap = new HashMap<>();
        }
    }
}
