package ch.ti8m.egov.testbase.tools;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.persistence.base.BaseRepositoryImpl;
import ch.ti8m.egov.testbase.TestApplicationContext;
import ch.ti8m.egov.testbase.repositories.TestEntityRepository;
import ch.ti8m.egov.testbase.repositories.TestImplementingEntityRepository;
import ch.ti8m.egov.testbase.repositories.TestSubEntityOneToManyRepository;
import ch.ti8m.egov.testbase.repositories.TestTablePerClassImplementingEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class RepositoryTestBase extends TestApplicationContext {


    @Autowired
    protected TestEntityRepository repository;

    @Autowired
    protected TestSubEntityOneToManyRepository testSubEntityOneToManyRepository;

    @Autowired
    protected TestImplementingEntityRepository testImplementingEntityRepository;

    @Autowired
    protected TestTablePerClassImplementingEntityRepository testTablePerClassImplementingEntityRepository;

    @AfterEach
    public void cleanUp() {
        try {
            cleanRepo(repository);
            cleanRepo(testSubEntityOneToManyRepository);
            cleanRepo(testImplementingEntityRepository);
            cleanRepo(testTablePerClassImplementingEntityRepository);

            DataHolder.cleanUp();
        } catch (final Exception e) {
            RepositoryTestBase.log.warn("Error cleaning up", e);
        }
    }

    private void cleanRepo(final BaseRepositoryImpl<?> repo) {
        repo.deactivatePermissions();
        repo.deleteAllWithTx();
        repo.activatePermissions();
    }


}
