package ExpensesTracker.ExpensesTracker.repositories.income;
import ExpensesTracker.ExpensesTracker.models.income.IncomeSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncomeSourceRepo extends JpaRepository<IncomeSource, Long> {
}
