package ExpensesTracker.ExpensesTracker.models.accounts.forms;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class SavingsAccountCreateForm {

    private String accountName;

    private Long bankId;

    private Long currencyId;

}
