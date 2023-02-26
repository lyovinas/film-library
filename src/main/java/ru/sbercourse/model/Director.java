package ru.sbercourse.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "directors")
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class Director extends GenericModel{

    @Column(name = "directors_fio", nullable = false)
    private String directorsFio;

    @Column(name = "position", nullable = false)
    private String position;

    @ToString.Exclude
    @ManyToMany(mappedBy = "directors")
    private Set<Film> films;
}
