package pl.poznan.put.db.service;

import java.util.List;
import java.util.Optional;
import javax.persistence.TypedQuery;
import pl.poznan.put.db.File;

public class FileService extends AbstractService {
  public final Optional<File> getFileByPath(final String path) {
    final TypedQuery<File> query =
        manager.createQuery("SELECT f FROM File f WHERE f.path = :path", File.class);
    query.setParameter("path", path);

    final List<File> resultList = query.getResultList();
    return resultList.isEmpty() ? Optional.empty() : Optional.of(query.getSingleResult());
  }
}
