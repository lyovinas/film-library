package ru.sbercourse.filmlibrary.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class OrderDto extends GenericDto {

    private Long userId;

    private Long filmId;

    private LocalDate rentDate;

    private Integer rentPeriod;

    private boolean purchase;
}
