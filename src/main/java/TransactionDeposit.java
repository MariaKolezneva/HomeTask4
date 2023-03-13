import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;

public class TransactionDeposit {
    private static final Object lock = new Object();
    public static final BigDecimal MAX_BALANCE = new BigDecimal("2000000000");
    public static final BigDecimal MAX_DEPOSIT_AMOUNT = new BigDecimal("100000000");

    public void deposit(int accountId, BigDecimal amount) throws IllegalArgumentException {
        if (amount.compareTo(MAX_DEPOSIT_AMOUNT) > 0) {
            throw new IllegalArgumentException("Transaction deposit amount exceeds maximum limit.");
        }
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Transaction amount cannot be zero.");
        }
        synchronized (lock) {
            String sql = "UPDATE Accounts SET balance = balance + ? WHERE accountId = ?";
            try (Connection conn = DriverManager.getConnection(Database.DATABASE_URL)) {
                conn.setAutoCommit(false);
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setBigDecimal(1, amount.setScale(3, RoundingMode.HALF_DOWN));
                    pstmt.setInt(2, accountId);
                    pstmt.executeUpdate();
                    BigDecimal newBalance = getAccountBalance(accountId).add(amount.setScale(3, RoundingMode.HALF_DOWN));
                    if (newBalance.compareTo(MAX_BALANCE) > 0) {
                        conn.rollback();
                        throw new IllegalArgumentException("Account balance exceeds maximum limit.");
                    }
                    conn.commit();
                    addTransactionDeposit(accountId, amount);
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                System.out.println("Account not found: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Deposit failed: " + e.getMessage());
            }
        }
    }

    public BigDecimal getAccountBalance(int accountId) {
        synchronized (lock) {
            BigDecimal balance = BigDecimal.ZERO;
            String sql = "SELECT balance FROM Accounts WHERE accountId = ?";
            try (Connection conn = DriverManager.getConnection(Database.DATABASE_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, accountId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    balance = rs.getBigDecimal("balance");
                }
            } catch (SQLException e) {
                System.out.println("Error getting account balance: " + e.getMessage());
            }
            return balance;
        }
    }


    public void addTransactionDeposit(int accountId, BigDecimal amount) {
        synchronized (lock) {
            try (Connection conn = DriverManager.getConnection(Database.DATABASE_URL)) {
                conn.setAutoCommit(false);
                PreparedStatement insertPstmt = conn.prepareStatement(
                        "INSERT INTO Transactions (accountId, amount) VALUES (?, ?)");
                insertPstmt.setInt(1, accountId);
                insertPstmt.setBigDecimal(2, amount.setScale(3, RoundingMode.HALF_DOWN));
                insertPstmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                System.out.println("Transaction failed: " + e.getMessage());

            }
        }
    }

    public static boolean isValidBalance(BigDecimal balance) {
        BigDecimal zero = BigDecimal.ZERO;
        return balance.compareTo(zero) >= 0 && balance.compareTo(MAX_BALANCE) <= 0;
    }
}

