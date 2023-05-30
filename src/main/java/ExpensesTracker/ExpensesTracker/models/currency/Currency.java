package ExpensesTracker.ExpensesTracker.models.currency;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String currencyName;

    @Column(nullable = false)
    private String currencyCode;

}
