package pl.poznan.put.db.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class AbstractService {
  protected final EntityManagerFactory factory;
  protected final EntityManager manager;

  public AbstractService() {
    super();
    factory = Persistence.createEntityManagerFactory("pl.poznan.put.rnator");
    manager = factory.createEntityManager();
  }

  public final void close() {
    manager.close();
    factory.close();
  }

  public EntityTransaction getTransaction() {
    return manager.getTransaction();
  }

  public final void persist(final Object o) {
    manager.persist(o);
  }
}
