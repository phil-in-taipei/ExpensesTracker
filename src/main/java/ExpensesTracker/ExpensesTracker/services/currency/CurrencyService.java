package ExpensesTracker.ExpensesTracker.services.currency;
import ExpensesTracker.ExpensesTracker.models.accounts.Bank;
import ExpensesTracker.ExpensesTracker.models.currency.Currency;
import ExpensesTracker.ExpensesTracker.repositories.currency.CurrencyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CurrencyService {

    @Autowired
    CurrencyRepo currencyRepo;

    public List<Currency> getAllCurrencies() {
        return currencyRepo.findAllByOrderByCurrencyCode();
    }

    public Currency getCurrency(Long id) {
        return currencyRepo.findById(id)
                .orElse(null);
    }
    @Transactional
    public void saveCurrency(Currency currency)
            throws IllegalArgumentException {
        currencyRepo.save(currency);
    }

}

