package ch.ti8m.egov.luid.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Table(name = "EGOV_PER_Running_Instance")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RunningInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID instanceId;
    private Long heartbeat;
    private Integer segmentId;

}