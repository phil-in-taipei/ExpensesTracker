package ExpensesTracker.ExpensesTracker.repositories.user;
import ExpensesTracker.ExpensesTracker.models.user.UserMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMetaRepo extends JpaRepository<UserMeta, Long>  {
}
