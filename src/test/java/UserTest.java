import org.testng.annotations.Test;
import java.math.BigDecimal;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

public class UserTest {

    @Test
    public void testAccountExists() {
        Currency currency = Currency.EUR;
        Account account = new Account(1, currency);
        boolean exists = User.accountExists(account);
        assertFalse(exists);
    }

    @Test
    public void testIsValidBalance() {
        BigDecimal balance1 = new BigDecimal("100");
        BigDecimal balance2 = new BigDecimal("-100");
        BigDecimal balance3 = new BigDecimal("2000000001");

        assertTrue(TransactionDeposit.isValidBalance(balance1));
        assertFalse(TransactionDeposit.isValidBalance(balance2));
        assertFalse(TransactionDeposit.isValidBalance(balance3));
    }
}
