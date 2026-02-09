package com.revpay.dao;

import com.revpay.model.User;
import com.revpay.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    // ================= REGISTER =================
    public boolean registerUser(User user,
                                String securityQuestion,
                                String securityAnswer) {

        String sql = "INSERT INTO users(full_name,email,phone,password_hash,transaction_pin,user_type,security_question,security_answer) VALUES (?,?,?,?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getTransactionPin());
            ps.setString(6, user.getUserType());
            ps.setString(7, securityQuestion);
            ps.setString(8, securityAnswer);

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= LOGIN =================
    public User login(String email, String passwordHash) {

        String sql = "SELECT * FROM users WHERE email=? AND password_hash=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, passwordHash);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getLong("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setUserType(rs.getString("user_type"));
                user.setWalletBalance(rs.getDouble("wallet_balance"));
                return user;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ================= FIND USER =================
    public User findUserByEmail(String email) {

        String sql = "SELECT * FROM users WHERE email=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getLong("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setWalletBalance(rs.getDouble("wallet_balance"));
                return user;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ================= UPDATE BALANCE =================
    public void updateBalance(long userId, double newBalance) {

        String sql = "UPDATE users SET wallet_balance=? WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, newBalance);
            ps.setLong(2, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= RECORD TRANSACTION =================
    public void recordTransaction(long senderId,
                                  long receiverId,
                                  double amount,
                                  String type,
                                  String status,
                                  String note) {

        String sql = "INSERT INTO transactions(sender_id,receiver_id,amount,type,status,note) VALUES (?,?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, senderId);
            ps.setLong(2, receiverId);
            ps.setDouble(3, amount);
            ps.setString(4, type);
            ps.setString(5, status);
            ps.setString(6, note);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= CREATE MONEY REQUEST =================
    public void createMoneyRequest(long requesterId,
                                   long receiverId,
                                   double amount) {

        String sql = "INSERT INTO money_requests(requester_id,receiver_id,amount,status) VALUES (?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, requesterId);
            ps.setLong(2, receiverId);
            ps.setDouble(3, amount);
            ps.setString(4, "PENDING");

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= VIEW PENDING REQUESTS =================
    public boolean viewMoneyRequests(long userId) {

        String sql = "SELECT * FROM money_requests WHERE receiver_id=? AND status='PENDING'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            boolean found = false;

            System.out.println("===== PENDING REQUESTS =====");

            while (rs.next()) {
                found = true;

                System.out.println("Request ID: " + rs.getLong("id"));
                System.out.println("From User ID: " + rs.getLong("requester_id"));
                System.out.println("Amount: Rs" + rs.getDouble("amount"));
                System.out.println("----------------------------");
            }

            return found;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    // ================= GET REQUEST BY ID =================
    public ResultSet getRequestById(long requestId) {

        String sql = "SELECT * FROM money_requests WHERE id=?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, requestId);
            return ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ================= UPDATE REQUEST STATUS =================
    public void updateRequestStatus(long requestId, String status) {

        String sql = "UPDATE money_requests SET status=? WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setLong(2, requestId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 // ================= ADD PAYMENT METHOD =================
    public void addPaymentMethod(long userId,
                                 String type,
                                 String number,
                                 String expiry) {

        String sql = "INSERT INTO payment_methods(user_id,type,encrypted_number,expiry_date,is_default) VALUES (?,?,?,?,false)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setString(2, type);
            ps.setString(3, number);
            ps.setString(4, expiry);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 // ================= ADD BALANCE =================
    public boolean addBalance(long userId, double amount) {

        if (amount <= 0) {
            System.out.println("Invalid amount!");
            return false;
        }

        String sql = "UPDATE users SET wallet_balance = wallet_balance + ? WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setLong(2, userId);
            ps.executeUpdate();

            recordTransaction(userId, userId, amount, "ADD", "SUCCESS", "Wallet top-up");

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
 // ================= WITHDRAW MONEY =================
    public boolean withdrawMoney(long userId, double amount) {

        if (amount <= 0) {
            System.out.println("Invalid amount!");
            return false;
        }

        double currentBalance = getWalletBalance(userId);

        if (currentBalance < amount) {
            System.out.println("Insufficient balance!");
            return false;
        }

        String sql = "UPDATE users SET wallet_balance = wallet_balance - ? WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setLong(2, userId);
            ps.executeUpdate();

            recordTransaction(userId, userId, amount, "WITHDRAW", "SUCCESS", "Wallet withdrawal");

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
 // ================= SEND MONEY =================
    public boolean sendMoney(long senderId,
                             long receiverId,
                             double amount) {

        if (amount <= 0) {
            System.out.println("Invalid amount!");
            return false;
        }

        double senderBalance = getWalletBalance(senderId);

        if (senderBalance < amount) {
            System.out.println("Insufficient balance!");
            return false;
        }

        Connection con = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);   // START TRANSACTION

            // Deduct from sender
            PreparedStatement deduct =
                    con.prepareStatement(
                            "UPDATE users SET wallet_balance = wallet_balance - ? WHERE user_id=?");

            deduct.setDouble(1, amount);
            deduct.setLong(2, senderId);
            deduct.executeUpdate();

            // Add to receiver
            PreparedStatement credit =
                    con.prepareStatement(
                            "UPDATE users SET wallet_balance = wallet_balance + ? WHERE user_id=?");

            credit.setDouble(1, amount);
            credit.setLong(2, receiverId);
            credit.executeUpdate();

            // Record transaction
            PreparedStatement tx =
                    con.prepareStatement(
                            "INSERT INTO transactions(sender_id,receiver_id,amount,type,status,note) VALUES (?,?,?,?,?,?)");

            tx.setLong(1, senderId);
            tx.setLong(2, receiverId);
            tx.setDouble(3, amount);
            tx.setString(4, "SEND");
            tx.setString(5, "SUCCESS");
            tx.setString(6, "Money transfer");
            tx.executeUpdate();

            con.commit();  // COMMIT

            return true;

        } catch (Exception e) {

            try {
                if (con != null) con.rollback();  // ROLLBACK IF ERROR
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }



 // ================= GET WALLET BALANCE =================
    public double getWalletBalance(long userId) {

        String sql = "SELECT wallet_balance FROM users WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("wallet_balance");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    
 // ================= VIEW PAYMENT METHODS =================
    public void viewPaymentMethods(long userId) {

        String sql = "SELECT id, type, expiry_date, is_default FROM payment_methods WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            boolean found = false;

            System.out.println("==== YOUR PAYMENT METHODS ====");

            while (rs.next()) {
                found = true;

                System.out.println("ID: " + rs.getLong("id"));
                System.out.println("Type: " + rs.getString("type"));
                System.out.println("Expiry: " + rs.getString("expiry_date"));
                System.out.println("Default: " + rs.getBoolean("is_default"));
                System.out.println("----------------------------");
            }

            if (!found) {
                System.out.println("No payment methods added yet.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    
 // ================= SET DEFAULT PAYMENT METHOD =================
    public boolean setDefaultPaymentMethod(long userId, long methodId) {

        try (Connection con = DBConnection.getConnection()) {

            // First check if method exists for this user
            String checkSql = "SELECT id FROM payment_methods WHERE id=? AND user_id=?";
            PreparedStatement checkPs = con.prepareStatement(checkSql);
            checkPs.setLong(1, methodId);
            checkPs.setLong(2, userId);

            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {
                return false; // method not found
            }

            // Remove previous default
            String resetSql = "UPDATE payment_methods SET is_default=false WHERE user_id=?";
            PreparedStatement resetPs = con.prepareStatement(resetSql);
            resetPs.setLong(1, userId);
            resetPs.executeUpdate();

            // Set new default
            String updateSql = "UPDATE payment_methods SET is_default=true WHERE id=? AND user_id=?";
            PreparedStatement updatePs = con.prepareStatement(updateSql);
            updatePs.setLong(1, methodId);
            updatePs.setLong(2, userId);

            int rows = updatePs.executeUpdate();

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }



    // ================= NOTIFICATIONS =================
    public void addNotification(long userId, String message) {

        String sql = "INSERT INTO notifications(user_id,message,type,is_read) VALUES (?,?,?,false)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setString(2, message);
            ps.setString(3, "INFO");
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewNotifications(long userId) {

        String sql = "SELECT * FROM notifications WHERE user_id=? ORDER BY created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            System.out.println("===== NOTIFICATIONS =====");
            boolean found= false;

            while (rs.next()) {
            	found=true;
                System.out.println(rs.getString("message"));
                System.out.println("Date: " + rs.getString("created_at"));
                System.out.println("----------------------------");
            }
            if(!found) {
            	System.out.println("No notification found.....");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= PASSWORD CHANGE =================
    public boolean verifyCurrentPassword(long userId, String passwordHash) {

        String sql = "SELECT * FROM users WHERE user_id=? AND password_hash=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setString(2, passwordHash);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void updatePassword(long userId, String newPasswordHash) {

        String sql = "UPDATE users SET password_hash=? WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPasswordHash);
            ps.setLong(2, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= FORGOT PASSWORD =================
    public String getSecurityQuestion(String email) {

        String sql = "SELECT security_question FROM users WHERE email=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return rs.getString("security_question");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public boolean emailExists(String email) {
        String sql = "SELECT user_id FROM users WHERE email=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void incrementFailedAttempts(String email) {
        String sql = "UPDATE users SET failed_attempts = failed_attempts + 1 WHERE email=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
            lockAccountIfNeeded(email);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetFailedAttempts(String email) {
        String sql = "UPDATE users SET failed_attempts = 0 WHERE email=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAccountLocked(String email) {
        String sql = "SELECT account_locked FROM users WHERE email=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getBoolean("account_locked");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void lockAccountIfNeeded(String email) {
        String sql = "SELECT failed_attempts FROM users WHERE email=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("failed_attempts") >= 3) {
                    PreparedStatement lock =
                            con.prepareStatement("UPDATE users SET account_locked=TRUE WHERE email=?");
                    lock.setString(1, email);
                    lock.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void unlockAccount(String email) {

        String sql = "UPDATE users SET failed_attempts = 0, account_locked = false WHERE email=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean verifySecurityAnswer(String email, String answer) {

        String sql = "SELECT * FROM users WHERE email=? AND security_answer=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, answer);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void updatePasswordByEmail(String email, String newPasswordHash) {

        String sql = "UPDATE users SET password_hash=? WHERE email=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPasswordHash);
            ps.setString(2, email);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean verifyTransactionPin(long userId, String pinHash) {

        String sql = "SELECT * FROM users WHERE user_id=? AND transaction_pin=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setString(2, pinHash);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void viewAllUsers() {

        String sql = "SELECT user_id, full_name, email, user_type, wallet_balance FROM users";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                System.out.println("ID: " + rs.getLong("user_id"));
                System.out.println("Name: " + rs.getString("full_name"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Type: " + rs.getString("user_type"));
                System.out.println("Balance: Rs" + rs.getDouble("wallet_balance"));
                System.out.println("----------------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void viewAllLoans() {

        String sql = "SELECT * FROM loans WHERE status='PENDING'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                System.out.println("Loan ID: " + rs.getLong("loan_id"));
                System.out.println("Business ID: " + rs.getLong("business_id"));
                System.out.println("Amount: Rs" + rs.getDouble("amount"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("----------------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        public void approveLoan(long loanId) {

            try (Connection con = DBConnection.getConnection()) {

                PreparedStatement ps =
                        con.prepareStatement("SELECT * FROM loans WHERE loan_id=?");

                ps.setLong(1, loanId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {

                    long businessId = rs.getLong("business_id");
                    double amount = rs.getDouble("amount");

                    PreparedStatement credit =
                            con.prepareStatement("UPDATE users SET wallet_balance = wallet_balance + ? WHERE user_id=?");

                    credit.setDouble(1, amount);
                    credit.setLong(2, businessId);
                    credit.executeUpdate();

                    PreparedStatement updateLoan =
                            con.prepareStatement("UPDATE loans SET status='APPROVED' WHERE loan_id=?");

                    updateLoan.setLong(1, loanId);
                    updateLoan.executeUpdate();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }



