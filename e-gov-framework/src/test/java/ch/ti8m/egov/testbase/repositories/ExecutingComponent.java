package ch.ti8m.egov.testbase.repositories;

import ch.ti8m.egov.testbase.entities.relationship.TestEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExecutingComponent {

    private final TestEntityRepository repository;

    // SAVE
    @Transactional
    public void save(final TestEntity entity, final boolean throwException) {
        repository.save(entity);
        if (throwException) {
            throw new RuntimeException();
        }
    }

    @Transactional
    public void saveWithTx(final TestEntity entity, final boolean throwException) {
        repository.saveWithTx(entity);
        if (throwException) {
            throw new RuntimeException();
        }
    }

    @Transactional
    public void save(final List<TestEntity> entities, final boolean throwException) {
        repository.save(entities);
        if (throwException) {
            throw new RuntimeException();
        }
    }

    @Transactional
    public void saveWithTx(final List<TestEntity> entities, final boolean throwException) {
        repository.saveWithTx(entities);
        if (throwException) {
            throw new RuntimeException();
        }
    }

    // UPDATE
    @Transactional
    public void update(final TestEntity entity, final boolean throwException) {
        repository.update(entity);
        if (throwException) {
            throw new RuntimeException();
        }
    }

    @Transactional
    public void updateWithTx(final TestEntity entity, final boolean throwException) {
        repository.updateWithTx(entity);
        if (throwException) {
            throw new RuntimeException();
        }
    }

    @Transactional
    public void update(final List<TestEntity> entities, final boolean throwException) {
        repository.update(entities);
        if (throwException) {
            throw new RuntimeException();
        }
    }

    @Transactional
    public void updateWithTx(final List<TestEntity> entities, final boolean throwException) {
        repository.updateWithTx(entities);
        if (throwException) {
            throw new RuntimeException();
        }
    }

    // DELETE
    @Transactional
    public void delete(final Long id, final boolean throwException) {
        repository.delete(id);
        if (throwException) {
            throw new RuntimeException();
        }
    }

    @Transactional
    public void deleteWithTx(final Long id, final boolean throwException) {
        repository.deleteWithTx(id);
        if (throwException) {
            throw new RuntimeException();
        }
    }

    @Transactional
    public void delete(final Integer id, final boolean throwException) {
        repository.delete(id);
        if (throwException) {
            throw new RuntimeException();
        }
    }

    @Transactional
    public void deleteWithTx(final Integer id, final boolean throwException) {
        repository.deleteWithTx(id);
        if (throwException) {
            throw new RuntimeException();
        }
    }

    @Transactional
    public void delete(final List<TestEntity> entities, final boolean throwException) {
        repository.delete(entities);
        if (throwException) {
            throw new RuntimeException();
        }
    }

    @Transactional
    public void deleteWithTx(final List<TestEntity> entities, final boolean throwException) {
        repository.deleteWithTx(entities);
        if (throwException) {
            throw new RuntimeException();
        }
    }

    @Transactional
    public void delete(final TestEntity entity, final boolean throwException) {
        repository.delete(entity);
        if (throwException) {
            throw new RuntimeException();
        }
    }

    @Transactional
    public void deleteWithTx(final TestEntity entity, final boolean throwException) {
        repository.deleteWithTx(entity);
        if (throwException) {
            throw new RuntimeException();
        }
    }

    @Transactional
    public void deleteAll(final boolean throwException) {
        repository.deleteAll();
        if (throwException) {
            throw new RuntimeException();
        }
    }

    @Transactional
    public void deleteAllWithTx(final boolean throwException) {
        repository.deleteAllWithTx();
        if (throwException) {
            throw new RuntimeException();
        }
    }


}
