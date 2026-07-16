package ch.ti8m.egov.framework.persistence.base;

import jakarta.persistence.EntityManager;

public interface EntityManagerProvider {
    EntityManager getEntityManager();
}
