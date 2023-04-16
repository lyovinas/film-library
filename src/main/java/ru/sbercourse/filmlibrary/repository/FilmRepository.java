package ru.sbercourse.filmlibrary.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sbercourse.filmlibrary.model.Film;

import java.util.List;

@Repository
public interface FilmRepository extends GenericRepository<Film> {

    @Query(nativeQuery = true,
            value = """
                    select distinct f.*
                    from films f
                    left join films_directors fd on f.id = fd.film_id
                    join directors d on d.id = fd.director_id
                    where f.title ilike '%' || coalesce(:title, '%') || '%'
                    and cast(f.genre as varchar(2)) like coalesce(:genre,'%')
                    and d.directors_fio ilike '%' || :fio || '%'
                    and f.is_deleted = false
                         """)
    Page<Film> searchFilms(
            @Param(value = "genre") String genre,
            @Param(value = "title") String title,
            @Param(value = "fio") String fio,
            Pageable pageable);

    List<Film> getByTitle(String title);
}
