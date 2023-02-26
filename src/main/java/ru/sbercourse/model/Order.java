package ru.sbercourse.model;

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
public class Order extends GenericModel{

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_ORDERS_USERS"), nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "film_id", foreignKey = @ForeignKey(name = "FK_ORDERS_FILMS"), nullable = false)
    private Film film;

    @Column(name = "rent_date", nullable = false)
    private LocalDate rentDate;

    @Column(name = "rent_period", nullable = false)
    private Integer rentPeriod;

    @Column(name = "purchase", nullable = false)
    private boolean purchase;
}
