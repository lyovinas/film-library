package ru.sbercourse.filmlibrary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "DTO для добавления фильма к создателю")
public class AddFilmDirectorDto {

    @Schema(description = "Идентификатор фильма")
    Long filmId;

    @Schema(description = "Идентификатор создателя")
    Long directorId;
}
