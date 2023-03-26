package ru.sbercourse.filmlibrary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sbercourse.filmlibrary.model.Genre;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "DTO для поиска фильма")
public class FilmSearchDTO {

    @Schema(description = "Название фильма", example = "Назад в будущее")
    private String filmTitle;

    @Schema(description = "Полное имя создателя фильма", example = "Роберт Земекис")
    private String directorsFio;

    @Schema(description = "Жанр фильма")
    private Genre genre;
}
