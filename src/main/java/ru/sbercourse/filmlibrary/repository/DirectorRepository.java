package ru.sbercourse.filmlibrary.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.sbercourse.filmlibrary.model.Director;

@Repository
public interface DirectorRepository extends GenericRepository<Director> {

    Page<Director> getAllByDirectorsFioContainsIgnoreCaseAndIsDeletedFalse(String fio, Pageable pageable);
}
