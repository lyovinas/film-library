package ru.sbercourse.filmlibrary.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sbercourse.filmlibrary.model.Genre;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class FilmDto extends GenericDto {

    private String title;

    private Short premierYear;

    private String country;

    private Genre genre;

    private Set<Long> ordersIds;

    private Set<Long> directorsIds;
}
