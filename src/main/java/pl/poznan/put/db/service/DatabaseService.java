package pl.poznan.put.db.service;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import pl.poznan.put.db.File;

public class DatabaseService {
  private final EntityManagerFactory factory;
  private final EntityManager manager;

  public DatabaseService() {
    super();
    factory = Persistence.createEntityManagerFactory("pl.poznan.put.rnator");
    manager = factory.createEntityManager();
  }

  public final void beginTransaction() {
    manager.getTransaction().begin();
  }

  public final void commit() {
    manager.getTransaction().commit();
  }

  public final void close() {
    manager.close();
    factory.close();
  }

  public final void persist(final Object o) {
    manager.persist(o);
  }

  public final Optional<File> getFileByPath(final String path) {
    final TypedQuery<File> query =
        manager.createQuery("SELECT f FROM File f WHERE f.path = :path", File.class);
    query.setParameter("path", path);

    final List<File> resultList = query.getResultList();
    return resultList.isEmpty() ? Optional.empty() : Optional.of(query.getSingleResult());
  }

  public final List<File> getFilesToProcess() {
    final TypedQuery<File> query =
        manager.createQuery("SELECT f FROM File f WHERE f.processed = false", File.class);
    return query.getResultList();
  }
}
