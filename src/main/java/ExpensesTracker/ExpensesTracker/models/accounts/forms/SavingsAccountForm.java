package ExpensesTracker.ExpensesTracker.models.accounts.forms;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class SavingsAccountForm {

    private String accountName;

    Long bankId;

    Long currencyId;

}
