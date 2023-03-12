package ru.sbercourse.filmlibrary.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@SequenceGenerator(name = "default_gen", sequenceName = "orders_seq", allocationSize = 1)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "jsonId")
public class Order extends GenericModel{

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_ORDERS_USERS"), nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "film_id", foreignKey = @ForeignKey(name = "FK_ORDERS_FILMS"), nullable = false)
    private Film film;

    @Column(name = "rent_date")
    private LocalDate rentDate;

    @Column(name = "rent_period")
    private Integer rentPeriod;

    @Column(name = "purchase", nullable = false)
    private boolean purchase;
}
