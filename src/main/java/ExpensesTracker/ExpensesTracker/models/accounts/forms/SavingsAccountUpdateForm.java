package ExpensesTracker.ExpensesTracker.models.accounts.forms;
import lombok.*;

import java.math.BigDecimal;

// this form only includes the updatable fields of the SavingsAccount object
@Getter
@Builder
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class SavingsAccountUpdateForm {

    private String accountName;

    private BigDecimal accountBalance;

}
