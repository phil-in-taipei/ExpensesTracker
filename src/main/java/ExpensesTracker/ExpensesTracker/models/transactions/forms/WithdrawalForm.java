package ExpensesTracker.ExpensesTracker.models.transactions.forms;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class WithdrawalForm {

    private Long savingsAccountId;

    private String date;

    private BigDecimal amount;

}
