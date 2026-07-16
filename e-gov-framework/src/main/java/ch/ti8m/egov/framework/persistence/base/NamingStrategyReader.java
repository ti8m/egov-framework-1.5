package ch.ti8m.egov.framework.persistence.base;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NamingStrategyReader {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public void test(final Class<? extends ModifiableEntity> entityClass) {
        final ServiceRegistry serviceRegistry = entityManagerFactory.unwrap(SessionFactory.class).getSessionFactoryOptions().getServiceRegistry();
        final MetadataSources metadataSources = new MetadataSources(serviceRegistry);

        metadataSources.addAnnotatedClass(entityClass);

        final Metadata metadata = metadataSources.buildMetadata();

        for (final PersistentClass entity : metadata.getEntityBindings()) {
            System.out.println("Table name: " + entity.getTable().getName());
            entity.getTable().getColumns().forEach(column -> System.out.println("Column name: " + column.getName()));
        }
    }

}
