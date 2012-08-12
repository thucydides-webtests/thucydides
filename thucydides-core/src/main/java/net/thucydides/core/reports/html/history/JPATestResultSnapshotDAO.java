package net.thucydides.core.reports.html.history;

import com.google.inject.Inject;
import net.thucydides.core.Thucydides;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.statistics.service.TagProviderService;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rahul
 * Date: 7/14/12
 * Time: 7:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class JPATestResultSnapshotDAO implements TestResultSnapshotDAO {

    protected EntityManagerFactory entityManagerFactory;

    private final EnvironmentVariables environmentVariables;

    private static final Logger LOGGER = LoggerFactory.getLogger(JPATestResultSnapshotDAO.class);
    private static final String FIND_ALL_TEST_RESULT_SNAPSHOTS = "select t from TestResultSnapshot t where t.projectKey = :projectKey order by t.time";
    private static final String CLEAR_ALL_TEST_RESULT_SNAPSHOTS = "delete from TestResultSnapshot t where t.projectKey = :projectKey";


    @Inject
    public JPATestResultSnapshotDAO(EntityManagerFactory entityManagerFactory,
                                    EnvironmentVariables environmentVariables) {
        this.entityManagerFactory = entityManagerFactory;
        this.environmentVariables = environmentVariables;
    }    


    @Override
    public void saveSnapshot(TestResultSnapshot testResultSnapshot) {
      EntityManager entityManager = entityManagerFactory.createEntityManager();
      entityManager.getTransaction().begin();
      try {
          entityManager.persist(testResultSnapshot);
          entityManager.getTransaction().commit();
      } catch (RuntimeException e) {
          entityManager.getTransaction().rollback();
          throw e;
      } finally {
          entityManager.close();
      }                  
    }
    
    @Override
    public List<TestResultSnapshot> findAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.createQuery(FIND_ALL_TEST_RESULT_SNAPSHOTS)
                    .setParameter("projectKey", getProjectKey())
                    .getResultList();
        }finally {
            entityManager.close();
        }

    }

    @Override
    public void clearAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            entityManager.createQuery(CLEAR_ALL_TEST_RESULT_SNAPSHOTS)
                    .setParameter("projectKey", getProjectKey())
                    .executeUpdate();
            entityManager.getTransaction().commit();
        }catch (RuntimeException e) {
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }

    private String getProjectKey() {
        return ThucydidesSystemProperty.PROJECT_KEY.from(environmentVariables,
                Thucydides.getDefaultProjectKey());
    }    


}
