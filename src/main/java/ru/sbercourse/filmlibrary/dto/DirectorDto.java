package ru.sbercourse.filmlibrary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "DTO создателя фильма")
public class DirectorDto extends GenericDto {

    @Schema(description = "Полное имя", example = "Роберт Земекис")
    private String directorsFio;

    @Schema(description = "Позиция при создании", example = "Режиссер")
    private String position;

    @Schema(description = "Идентификаторы фильмов")
    private Set<Long> filmsIds;
}
