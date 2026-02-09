package com.revpay.controller;

import com.revpay.dao.UserDAO;
import com.revpay.model.User;
import com.revpay.util.DBConnection;
import com.revpay.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class MainApp {

    static Scanner sc = new Scanner(System.in);
    static UserDAO userDAO = new UserDAO();

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("===== REV PAY =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Forgot Password");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");

            choice = readInt();
            if (choice == -1) {
                System.out.println("Invalid option");
                continue;
            }

            switch (choice) {
                case 1: register(); break;
                case 2: login(); break;
                case 3: forgotPassword(); break;
                case 4: System.out.println("TATA BYE BYE GOOD BYE"); break;
                default: System.out.println("Invalid option");
            }

        } while (choice != 4);
    }

    // ================= SAFE INPUT METHODS =================

    private static int readInt() {
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }

    private static long readLongSafe(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Long.parseLong(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }
    private static long readLong() {
        try {
            return Long.parseLong(sc.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }


    private static double readDouble(String message) {
        while (true) {
            System.out.print(message);
            try {
                double value = Double.parseDouble(sc.nextLine());
                if (value <= 0) {
                    System.out.println("Amount must be greater than 0");
                    continue;
                }
                return value;
            } catch (Exception e) {
                System.out.println("Invalid amount. Enter numeric value.");
            }
        }
    }

    // ================= REGISTER =================
    private static void register() {

        System.out.print("Full Name: ");
        String name = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        if (!isValidEmail(email)) {
            System.out.println("Invalid email format");
            return;
        }

        if (userDAO.emailExists(email)) {
            System.out.println("Email already registered");
            return;
        }

        System.out.print("Phone: ");
        String phone = sc.nextLine();

        if (!phone.matches("\\d{10}")) {
            System.out.println("Phone must be 10 digits");
            return;
        }

        System.out.print("Password: ");
        String password = sc.nextLine();

        if (!isValidPassword(password)) {
            System.out.println("Weak password");
            return;
        }

        System.out.print("Transaction PIN (6 digits): ");
        String pin = sc.nextLine();

        if (!pin.matches("\\d{6}")) {
            System.out.println("PIN must be 6 digits");
            return;
        }

        System.out.print("User Type (PERSONAL/BUSINESS): ");
        String type = sc.nextLine().toUpperCase();

        System.out.print("Security Question: ");
        String question = sc.nextLine();

        System.out.print("Security Answer: ");
        String answer = sc.nextLine();

        User user = new User(
                name,
                email,
                phone,
                PasswordUtil.hashPassword(password),
                PasswordUtil.hashPassword(pin),
                type
        );

        if (userDAO.registerUser(user, question, answer))
            System.out.println("Registration Successful");
        else
            System.out.println("Registration Failed");
    }

    // ================= LOGIN =================
    private static void login() {

        System.out.print("Email: ");
        String email = sc.nextLine();

        if (userDAO.isAccountLocked(email)) {
            System.out.println("Account locked");
            return;
        }

        System.out.print("Password: ");
        String password = sc.nextLine();

        User user = userDAO.login(email,
                PasswordUtil.hashPassword(password));

        if (user != null) {

            userDAO.resetFailedAttempts(email);

            System.out.println("Login Successful");
            System.out.println("Welcome " + user.getFullName());

            if ("ADMIN".equalsIgnoreCase(user.getUserType()))
                adminMenu(user);
            else if ("BUSINESS".equalsIgnoreCase(user.getUserType()))
                businessMenu(user);
            else
                personalMenu(user);

        } else {
            userDAO.incrementFailedAttempts(email);
            System.out.println("Invalid Credentials");
        }
    }


    // ================= ADMIN MENU =================
    private static void adminMenu(User user) {

        int choice;

        do {
            System.out.println("==== ADMIN MENU ====");
            System.out.println("1. View All Users");
            System.out.println("2. Unlock User");
            System.out.println("3. View Pending Loans");
            System.out.println("4. Approve Loan");
            System.out.println("5. Logout");
            System.out.print("Choose: ");

            choice = readInt();
            if (choice == -1) {
                System.out.println("Invalid option");
                continue;
            }

            switch (choice) {

                case 1: userDAO.viewAllUsers(); break;

                case 2:
                    System.out.print("Enter email to unlock: ");
                    userDAO.unlockAccount(sc.nextLine());
                    System.out.println("Account unlocked");
                    break;

                case 3: userDAO.viewAllLoans(); break;

                case 4:
                    System.out.print("Enter Loan ID: ");
                    long loanId = readLong();
                    if (loanId == -1) {
                        System.out.println("Invalid Loan ID");
                        break;
                    }
                    userDAO.approveLoan(loanId);
                    System.out.println("Loan approved");
                    break;

                case 5: System.out.println("Logged out"); break;
                default: System.out.println("Invalid option");
            }

        } while (choice != 5);
    }


    
 // ================= EMAIL VALIDATION =================
    private static boolean isValidEmail(String email) {

        if (email == null)
            return false;

        // Proper Email Regex:
        // 1. Username part
        // 2. @ symbol
        // 3. Domain name
        // 4. Dot
        // 5. Minimum 2 character TLD

        String emailRegex =
                "^[A-Za-z0-9._%+-]+@" +          // username
                "[A-Za-z0-9.-]+\\." +            // domain name + dot
                "[A-Za-z]{2,}$";                 // top-level domain

        return email.matches(emailRegex);
    }


    // ================= PASSWORD VALIDATION =================
    private static boolean isValidPassword(String password) {

        if (password.length() < 8)
            return false;

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char ch : password.toCharArray()) {

            if (Character.isUpperCase(ch))
                hasUpper = true;
            else if (Character.isLowerCase(ch))
                hasLower = true;
            else if (Character.isDigit(ch))
                hasDigit = true;
            else
                hasSpecial = true;
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
    

 



    // ================= PERSONAL MENU =================
    private static void personalMenu(User user) {

        int choice;

        do {
            System.out.println("==== PERSONAL MENU ====");
            System.out.println("1. Wallet");
            System.out.println("2. Send Money");
            System.out.println("3. Request Money");
            System.out.println("4. Check Money Requests");
            System.out.println("5. Transaction History");
            System.out.println("6. Notifications");
            System.out.println("7. View & Pay Invoices");
            System.out.println("8. Change Password");
            System.out.println("9. Logout");
            System.out.print("Choose: ");

            choice = readInt();
            if (choice == -1) {
                System.out.println("Invalid option");
                continue;
            }

            switch (choice) {

                case 1: walletMenu(user); break;
                case 2: sendMoney(user); break;
                case 3: requestMoney(user); break;
                case 4: acceptRequest(user); break;
                case 5: viewTransactions(user.getUserId()); break;
                case 6: userDAO.viewNotifications(user.getUserId()); break;
                case 7: payInvoice(user); break;
                case 8: changePassword(user); break;
                case 9: System.out.println("Logged out successfully!"); break;
                default: System.out.println("Invalid option");
            }

        } while (choice != 9);
    }

    // ================= BUSINESS MENU =================
    private static void businessMenu(User user) {

        int choice;

        do {
            System.out.println("==== BUSINESS MENU ====");
            System.out.println("1. Wallet");
            System.out.println("2. Send Money");
            System.out.println("3. Request Money");
            System.out.println("4. Check Money Requests");
            System.out.println("5. Transaction History");
            System.out.println("6. Notifications");
            System.out.println("7. Invoice Management");
            System.out.println("8. Loan Management");
            System.out.println("9. Business Analytics");
            System.out.println("10. Change Password");
            System.out.println("11. Logout");
            System.out.print("Choose: ");

            choice = readInt();
            if (choice == -1) {
                System.out.println("Invalid option");
                continue;
            }


            switch (choice) {

                case 1: walletMenu(user); break;
                case 2: sendMoney(user); break;
                case 3: requestMoney(user); break;
                case 4: acceptRequest(user); break;
                case 5: viewTransactions(user.getUserId()); break;
                case 6: userDAO.viewNotifications(user.getUserId()); break;
                case 7: invoiceMenu(user); break;
                case 8: loanMenu(user); break;
                case 9: businessAnalytics(user); break;
                case 10: changePassword(user); break;
                case 11: System.out.println("Logged out successfully!"); break;
                default: System.out.println("Invalid option");
            }

        } while (choice != 11);
    }

    // ================= PAY INVOICE =================
    private static void payInvoice(User user) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement("SELECT * FROM invoices WHERE customer_email=? AND status='UNPAID'")) {

            ps.setString(1, user.getEmail());
            ResultSet rs = ps.executeQuery();

            System.out.println("===== YOUR UNPAID INVOICES =====");

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println("Invoice ID: " + rs.getLong("invoice_id"));
                System.out.println("Business ID: " + rs.getLong("business_id"));
                System.out.println("Amount: Rs" + rs.getDouble("amount"));
                System.out.println("----------------------------");
            }

            if (!found) {
                System.out.println("No unpaid invoices found");
                return;
            }

            System.out.print("Enter Invoice ID to pay (0 to cancel): ");
            long invoiceId = Long.parseLong(sc.nextLine());

            if (invoiceId == 0) return;

            PreparedStatement invoiceStmt =
                    con.prepareStatement("SELECT * FROM invoices WHERE invoice_id=?");

            invoiceStmt.setLong(1, invoiceId);
            ResultSet invoiceRs = invoiceStmt.executeQuery();

            if (invoiceRs.next()) {

                long businessId = invoiceRs.getLong("business_id");
                double amount = invoiceRs.getDouble("amount");

                if (amount > user.getWalletBalance()) {
                    System.out.println("Insufficient balance");
                    return;
                }

                double newCustomerBalance = user.getWalletBalance() - amount;
                user.setWalletBalance(newCustomerBalance);
                userDAO.updateBalance(user.getUserId(), newCustomerBalance);

                double businessBalance = getBalance(businessId);
                userDAO.updateBalance(businessId,
                        businessBalance + amount);

                userDAO.recordTransaction(user.getUserId(),
                        businessId,
                        amount,
                        "INVOICE_PAYMENT",
                        "SUCCESS",
                        "Invoice payment");

                PreparedStatement update =
                        con.prepareStatement("UPDATE invoices SET status='PAID' WHERE invoice_id=?");

                update.setLong(1, invoiceId);
                update.executeUpdate();

                System.out.println("Invoice paid successfully");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    



    // ================= INVOICE MODULE =================
    private static void invoiceMenu(User user) {

        System.out.println("1. Create Invoice");
        System.out.println("2. View My Invoices");
        System.out.print("Choose: ");

        int choice = Integer.parseInt(sc.nextLine());

        if (choice == 1) {

            System.out.print("Customer Email: ");
            String email = sc.nextLine();

            System.out.print("Amount: ");
            double amount = Double.parseDouble(sc.nextLine());

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps =
                         con.prepareStatement("INSERT INTO invoices(business_id,customer_email,amount,status) VALUES (?,?,?,'UNPAID')")) {

                ps.setLong(1, user.getUserId());
                ps.setString(2, email);
                ps.setDouble(3, amount);
                ps.executeUpdate();

                System.out.println("Invoice created successfully");

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (choice == 2) {

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps =
                         con.prepareStatement("SELECT * FROM invoices WHERE business_id=?")) {

                ps.setLong(1, user.getUserId());
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    System.out.println("Invoice ID: " + rs.getLong("invoice_id"));
                    System.out.println("Customer: " + rs.getString("customer_email"));
                    System.out.println("Amount: Rs" + rs.getDouble("amount"));
                    System.out.println("Status: " + rs.getString("status"));
                    System.out.println("----------------------------");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ================= LOAN MODULE =================
    private static void loanMenu(User user) {

        System.out.print("Enter Loan Amount: ");
        double amount = Double.parseDouble(sc.nextLine());

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement("INSERT INTO loans(business_id,amount,status,remaining_balance) VALUES (?,?, 'PENDING',?)")) {

            ps.setLong(1, user.getUserId());
            ps.setDouble(2, amount);
            ps.setDouble(3, amount);
            ps.executeUpdate();

            System.out.println("Loan application submitted");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= BUSINESS ANALYTICS =================
    private static void businessAnalytics(User user) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement("SELECT SUM(amount) FROM transactions WHERE receiver_id=?")) {

            ps.setLong(1, user.getUserId());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Total Revenue: Rs" + rs.getDouble(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    // ================= WALLET MENU (SAFE VERSION) =================

    private static void walletMenu(User user) {

        int choice;

        do {
            System.out.println("==== WALLET MENU ====");
            System.out.println("1. View Balance");
            System.out.println("2. Add Balance");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Add Payment Method");
            System.out.println("5. View Payment Methods");
            System.out.println("6. Set Default Payment Method");
            System.out.println("7. Back");
            System.out.print("Choose: ");

            choice = readInt();
            if (choice == -1) {
                System.out.println("Invalid option");
                continue;
            }

            switch (choice) {

                case 1:
                    System.out.println("Current Balance: Rs" + user.getWalletBalance());
                    break;

                case 2:
                    double add = readDouble("Enter amount to add: ");
                    double newBalance = user.getWalletBalance() + add;
                    user.setWalletBalance(newBalance);
                    userDAO.updateBalance(user.getUserId(), newBalance);
                    System.out.println("Money added successfully");
                    break;

                case 3:
                    double withdraw = readDouble("Enter amount to withdraw: ");

                    if (withdraw > user.getWalletBalance()) {
                        System.out.println("Insufficient balance");
                        break;
                    }

                    double remaining = user.getWalletBalance() - withdraw;
                    user.setWalletBalance(remaining);
                    userDAO.updateBalance(user.getUserId(), remaining);
                    System.out.println("Money withdrawn successfully");
                    break;

                case 4:
                    addPaymentMethod(user);
                    break;

                case 5:
                    userDAO.viewPaymentMethods(user.getUserId());
                    break;

                case 6:
                    long id = readLongSafe("Enter Payment Method ID to set default: ");
                    boolean success = userDAO.setDefaultPaymentMethod(user.getUserId(), id);

                    if (success)
                        System.out.println("Default payment method updated");
                    else
                        System.out.println("Invalid Payment Method ID");

                    break;

                case 7:
                    break;

                default:
                    System.out.println("Invalid option");
            }

        } while (choice != 7);
    }
    // ================= ADD PAYMENTS =================
    

    private static void addPaymentMethod(User user) {

        System.out.println("Select Payment Type");
        System.out.println("1. CREDIT_CARD");
        System.out.println("2. DEBIT_CARD");
        System.out.println("3. BANK_ACCOUNT");
        System.out.print("Choose: ");

        int choice = readInt();
        if (choice == -1) {
            System.out.println("Invalid type");
            return;
        }

        String type = "";

        if (choice == 1)
            type = "CREDIT_CARD";
        else if (choice == 2)
            type = "DEBIT_CARD";
        else if (choice == 3)
            type = "BANK_ACCOUNT";
        else {
            System.out.println("Invalid type");
            return;
        }

        System.out.print("Enter Account/Card Number: ");
        String number = sc.nextLine();

        // ================= CARD VALIDATION =================
        if (type.equals("CREDIT_CARD") || type.equals("DEBIT_CARD")) {

            // Must be exactly 16 digits
            if (!number.matches("\\d{16}")) {
                System.out.println("Invalid Card Number. Must be exactly 16 digits.");
                return;
            }

        } else if (type.equals("BANK_ACCOUNT")) {

            // Bank account must be 9 to 18 digits
            if (!number.matches("\\d{9,18}")) {
                System.out.println("Invalid Bank Account Number.");
                return;
            }
        }

        System.out.print("Enter Expiry Date (MM/YY or NA): ");
        String expiry = sc.nextLine();

        // ================= EXPIRY VALIDATION =================
        if (!expiry.equalsIgnoreCase("NA")) {

            if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                System.out.println("Invalid Expiry Date format. Use MM/YY.");
                return;
            }

            String[] parts = expiry.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt("20" + parts[1]);

            java.time.YearMonth expiryDate =
                    java.time.YearMonth.of(year, month);

            java.time.YearMonth currentDate =
                    java.time.YearMonth.now();

            if (expiryDate.isBefore(currentDate)) {
                System.out.println("Card is expired.");
                return;
            }
        }

        userDAO.addPaymentMethod(user.getUserId(), type, number, expiry);

        System.out.println("Payment method added successfully");
    }

 // ================= SEND MONEY =================
    private static void sendMoney(User sender) {

        System.out.print("Enter receiver email: ");
        String email = sc.nextLine();

        User receiver = userDAO.findUserByEmail(email);

        if (receiver == null) {
            System.out.println("Receiver not found");
            return;
        }

        System.out.print("Enter amount: ");
        double amount;

        try {
            amount = Double.parseDouble(sc.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid amount");
            return;
        }

        if (amount <= 0) {
            System.out.println("Amount must be greater than 0");
            return;
        }

        if (amount > sender.getWalletBalance()) {
            System.out.println("Insufficient balance");
            return;
        }

        //  TRANSACTION PIN VERIFICATION
        System.out.print("Enter 6-digit Transaction PIN: ");
        String pin = sc.nextLine();

        if (!pin.matches("\\d{6}")) {
            System.out.println("Invalid PIN format");
            return;
        }

        boolean validPin = userDAO.verifyTransactionPin(
                sender.getUserId(),
                PasswordUtil.hashPassword(pin)
        );

        if (!validPin) {
            System.out.println("Incorrect Transaction PIN");
            return;
        }

        double senderNew = sender.getWalletBalance() - amount;
        sender.setWalletBalance(senderNew);
        userDAO.updateBalance(sender.getUserId(), senderNew);

        double receiverBalance = getBalance(receiver.getUserId());
        userDAO.updateBalance(receiver.getUserId(),
                receiverBalance + amount);

        userDAO.recordTransaction(sender.getUserId(),
                receiver.getUserId(),
                amount,
                "SEND",
                "SUCCESS",
                "Money sent");

        System.out.println("Money sent successfully");
    }


    // ================= REQUEST MONEY =================
    private static void requestMoney(User requester) {

        System.out.print("Enter receiver email: ");
        String email = sc.nextLine();

        User receiver = userDAO.findUserByEmail(email);

        if (receiver == null) {
            System.out.println("User not found");
            return;
        }

        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(sc.nextLine());

        userDAO.createMoneyRequest(requester.getUserId(),
                receiver.getUserId(),
                amount);

        System.out.println("Money request sent successfully");
    }

    // ================= ACCEPT REQUEST =================
    private static void acceptRequest(User receiver) {

        boolean hasRequests = userDAO.viewMoneyRequests(receiver.getUserId());

        if (!hasRequests) {
            System.out.println("No pending requests found.");
            return;
        }

        System.out.print("Enter Request ID to accept (0 to cancel): ");
        long id = readLong();

        if (id <= 0) return;

        try {
            ResultSet rs = userDAO.getRequestById(id);

            if (rs == null || !rs.next()) {
                System.out.println("Invalid Request ID");
                return;
            }

            long requesterId = rs.getLong("requester_id");
            double amount = rs.getDouble("amount");

            if (amount > receiver.getWalletBalance()) {
                System.out.println("Insufficient balance");
                return;
            }

            double receiverNew = receiver.getWalletBalance() - amount;
            receiver.setWalletBalance(receiverNew);
            userDAO.updateBalance(receiver.getUserId(), receiverNew);

            double requesterBalance = getBalance(requesterId);
            userDAO.updateBalance(requesterId, requesterBalance + amount);

            userDAO.recordTransaction(receiver.getUserId(),
                    requesterId,
                    amount,
                    "REQUEST",
                    "SUCCESS",
                    "Request accepted");

            userDAO.updateRequestStatus(id, "ACCEPTED");

            System.out.println("Request accepted and money transferred");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ================= VIEW TRANSACTIONS =================
    private static void viewTransactions(long userId) {

        String sql = "SELECT * FROM transactions WHERE sender_id=? OR receiver_id=? ORDER BY created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, userId);

            ResultSet rs = ps.executeQuery();

            System.out.println("===== TRANSACTION HISTORY =====");

            boolean found = false;

            while (rs.next()) {

                found = true;

                long sender = rs.getLong("sender_id");
                double amount = rs.getDouble("amount");
                String type = rs.getString("type");
                String status = rs.getString("status");
                String date = rs.getString("created_at");

                String direction = (sender == userId) ? "DEBIT" : "CREDIT";

                System.out.println("Type: " + type);
                System.out.println("Amount: Rs" + amount);
                System.out.println("Direction: " + direction);
                System.out.println("Status: " + status);
                System.out.println("Date: " + date);
                System.out.println("----------------------------");
            }

            if (!found) {
                System.out.println("No transactions found.");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= SAFE GET BALANCE =================

    private static double getBalance(long userId) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement("SELECT wallet_balance FROM users WHERE user_id=?")) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return rs.getDouble("wallet_balance");

        } catch (Exception e) {
            System.out.println("Unable to fetch balance.");
        }

        return 0;
    }

    private static void forgotPassword() {

        System.out.print("Enter your email: ");
        String email = sc.nextLine();

        String question = userDAO.getSecurityQuestion(email);

        if (question == null) {
            System.out.println("Email not found");
            return;
        }

        System.out.println("Security Question: " + question);
        System.out.print("Enter your answer: ");
        String answer = sc.nextLine();

        if (!userDAO.verifySecurityAnswer(email, answer)) {
            System.out.println("Incorrect answer");
            return;
        }

        System.out.print("Enter new password: ");
        String newPass = sc.nextLine();

        userDAO.updatePasswordByEmail(email,
                PasswordUtil.hashPassword(newPass));

        System.out.println("Password reset successful");
    }

    private static void changePassword(User user) {

        System.out.print("Enter current password: ");
        String current = sc.nextLine();

        if (!userDAO.verifyCurrentPassword(user.getUserId(),
                PasswordUtil.hashPassword(current))) {

            System.out.println("Current password incorrect");
            return;
        }

        System.out.print("Enter new password: ");
        String newPass = sc.nextLine();

        userDAO.updatePassword(user.getUserId(),
                PasswordUtil.hashPassword(newPass));

        System.out.println("Password changed successfully");
    }
}
