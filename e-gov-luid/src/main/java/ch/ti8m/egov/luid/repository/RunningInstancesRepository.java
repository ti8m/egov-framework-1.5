package ch.ti8m.egov.luid.repository;


import ch.ti8m.egov.luid.entity.RunningInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RunningInstancesRepository extends JpaRepository<RunningInstance, Long> {
    Optional<RunningInstance> findByInstanceId(UUID instanceId);
}
