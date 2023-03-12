package ru.sbercourse.filmlibrary.service;

import org.springframework.stereotype.Service;
import ru.sbercourse.filmlibrary.model.Director;
import ru.sbercourse.filmlibrary.repository.DirectorRepository;

@Service
public class DirectorService extends GenericService<Director> {

  protected DirectorService(DirectorRepository directorRepository) {
    super(directorRepository);
  }
}
