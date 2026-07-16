package ch.ti8m.egov.framework.persistence.healthcheck.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
//@Entity
@Table(name = "\"flyway_schema_history\"")
public class FlywayMigration {

    @Id
    @Column(name = "installed_rank")
    private Integer installedRank;

    @Column(name = "version")
    private String version;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private String type;

    @Column(name = "script")
    private String script;

    @Column(name = "checksum")
    private Integer checksum;

    @Column(name = "installed_by")
    private String installedBy;

    @Column(name = "installed_on")
    private LocalDateTime installedOn;

    @Column(name = "execution_time")
    private Integer executionTime;

    @Column(name = "success")
    private Boolean success;

}
