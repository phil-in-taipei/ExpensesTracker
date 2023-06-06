package ExpensesTracker.ExpensesTracker.models.accounts.forms;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class SavingsAccountUpdateForm {

    private String accountName;

    private BigDecimal accountBalance;

}
