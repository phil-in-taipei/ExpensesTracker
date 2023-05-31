package ExpensesTracker.ExpensesTracker.models.accounts.forms;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SavingsAccountForm {

    private String accountName;

    Long bankId;

    Long currencyId;

}
