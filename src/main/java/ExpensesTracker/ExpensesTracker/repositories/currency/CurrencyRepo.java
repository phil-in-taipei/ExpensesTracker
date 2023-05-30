package ExpensesTracker.ExpensesTracker.repositories.currency;

import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CurrencyRepo extends JpaRepository<Currency, Long>{

    List<Currency> findAllByOrderByCurrencyCode();
}
