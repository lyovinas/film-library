package ru.sbercourse.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class User extends GenericModel{

    @Column(name = "login", nullable = false)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "created_when", nullable = false)
    private LocalDateTime createdWhen;

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "FK_USERS_ROLES"), nullable = false)
    private Role role;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", orphanRemoval = true,
            cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    private Set<Order> orders;
}
