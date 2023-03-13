import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        Database database = new Database();

        User user1 = new User("Julia Ivanova", "Minsk");
        User.createUser(user1);
        int userId1 = user1.getUserId();
        Currency currencyEUR = Currency.EUR;
        Currency currencyUSD = Currency.USD;
        Currency currencyRUB = Currency.RUB;
        Currency currencyBYN = Currency.BYN;
        Account account1 = new Account(userId1, currencyEUR);
        User.createAccount(account1);
        Account account2 = new Account(userId1, currencyUSD);
        User.createAccount(account2);
        Account account3 = new Account(userId1, currencyRUB);
        User.createAccount(account3);
        Account account4 = new Account(userId1, currencyBYN);
        User.createAccount(account4);

        int accountId1 = account1.getAccountId();

        TransactionDeposit transaction1 = new TransactionDeposit();
        BigDecimal depositAmount1 = new BigDecimal(10000000);
        transaction1.deposit(accountId1, depositAmount1);

        TransactionWithdraw transaction2 = new TransactionWithdraw();
        BigDecimal withdrawAmount2 = new BigDecimal(1000000);
        transaction2.withdraw(accountId1, withdrawAmount2);

        database.disconnect();
    }
}
