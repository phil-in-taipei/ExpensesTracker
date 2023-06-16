package ExpensesTracker.ExpensesTracker.models.search;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Month;

@Getter
@Setter
@NoArgsConstructor
public class SearchMonthAndYearForm {
    private Month month;
    private Integer year;
}
