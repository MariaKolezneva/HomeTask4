import java.math.BigDecimal;

public class Account {
    private int accountId;
    private int userId;
    private BigDecimal balance;
    private Currency currency;

    public Account(int userId, Currency currency) {
        this.userId = userId;
        this.currency = currency;
    }

    public int getAccountId() {
        return accountId;

    }

    public void setAccountId(int accountId) {

        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;

    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;

    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

}
