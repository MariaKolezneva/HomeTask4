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
        assertFalse("User not created",exists);
    }

    @Test
    public void testIsValidBalance() {
        BigDecimal balance1 = new BigDecimal("100");
        BigDecimal balance2 = new BigDecimal("-100");
        BigDecimal balance3 = new BigDecimal("2000000001");

        assertTrue("Balance should be positive", TransactionDeposit.isValidBalance(balance1));
        assertFalse("Balance should not be negative", TransactionDeposit.isValidBalance(balance2));
        assertFalse("Balance exceeds limit", TransactionDeposit.isValidBalance(balance3));
    }
    }

