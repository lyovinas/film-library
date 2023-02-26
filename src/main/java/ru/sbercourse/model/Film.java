package ru.sbercourse.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "films")
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class Film extends GenericModel{

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "premier_year", nullable = false)
    private LocalDate premierYear;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "genre", nullable = false)
    @Enumerated
    private Genre genre;

    @ToString.Exclude
    @OneToMany(mappedBy = "film")
    private Set<Order> orders;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "films_directors",
            joinColumns = @JoinColumn(name = "film_id"), foreignKey = @ForeignKey(name = "FK_FILMS_DIRECTORS"),
            inverseJoinColumns = @JoinColumn(name = "director_id"), inverseForeignKey = @ForeignKey(name = "FK_DIRECTORS_FILMS"))
    private Set<Director> directors;
}
