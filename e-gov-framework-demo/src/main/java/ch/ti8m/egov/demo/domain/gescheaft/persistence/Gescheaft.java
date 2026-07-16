package ch.ti8m.egov.demo.domain.gescheaft.persistence;

import ch.ti8m.egov.framework.persistence.base.ArchivedModifiableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "Gescheaft")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE Gescheaft SET Archived = TRUE WHERE gescheaft_id = ?")
public class Gescheaft extends ArchivedModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gescheaftId;

    // Add your fields here

}
