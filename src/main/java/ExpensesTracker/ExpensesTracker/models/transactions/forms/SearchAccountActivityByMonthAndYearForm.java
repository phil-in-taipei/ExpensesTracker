package ExpensesTracker.ExpensesTracker.models.transactions.forms;

import ExpensesTracker.ExpensesTracker.models.search.SearchMonthAndYearForm;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class SearchAccountActivityByMonthAndYearForm extends SearchMonthAndYearForm  {
    private Long savingsAccountId;
}
