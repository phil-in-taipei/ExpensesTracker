package ExpensesTracker.ExpensesTracker.models.accounts.forms;
import lombok.*;

// this is used as a form to get the IDS of the associated fields
// rather than the associated objects themselves to allow for easier
// testing by mocking the service method that queries the associated
// field object by ID
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
