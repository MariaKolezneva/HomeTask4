import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        Database database = new Database();

        User user1 = new User("Julia Ivanova", "Minsk");
        User.createUser(user1);
        int userId1 = user1.getUserId();
        Currency currency = Currency.EUR;
        Account account1 = new Account(userId1, currency);
        User.createAccount(account1);

//        TransactionDeposit transaction1 = new TransactionDeposit();
//        int accountId1 = account1.getAccountId();
//        BigDecimal depositAmount1 = new BigDecimal(10000000);
//        transaction1.deposit(accountId1, depositAmount1);


//        TransactionWithdraw transaction2 = new TransactionWithdraw();
//        BigDecimal withdrawAmount2 = new BigDecimal(1000000);
//        transaction2.withdraw(1, withdrawAmount2);


        database.disconnect();
    }
}
