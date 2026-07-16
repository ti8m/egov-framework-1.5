package ch.ti8m.egov.framework.persistence.healthcheck.model;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "EGOV_PER_AA_HealthCheckTest")
@NoArgsConstructor
@Getter
@Setter
public class HealthCheckTestEntity extends ModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Text")
    private String text;

}