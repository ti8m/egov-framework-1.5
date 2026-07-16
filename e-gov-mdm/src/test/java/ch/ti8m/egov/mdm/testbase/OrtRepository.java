package ch.ti8m.egov.mdm.testbase;

import ch.ti8m.egov.framework.persistence.base.ClassType;
import ch.ti8m.egov.mdm.api.abstraction.MasterDataRepository;
import org.springframework.stereotype.Repository;

@ClassType(entityClass = Ort.class)
@Repository
public class OrtRepository extends MasterDataRepository<Ort> {
}
