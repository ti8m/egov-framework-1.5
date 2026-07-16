package ch.ti8m.egov.framework.persistence.healthcheck.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "EGOV_PER_AA_RowCount")
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class RowCount {

    @Id
    @Column(name = "TableName")
    private String tableName;

    @Column(name = "RowCount")
    private Integer rowCount;

}