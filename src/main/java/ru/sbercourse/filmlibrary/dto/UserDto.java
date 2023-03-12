package ru.sbercourse.filmlibrary.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sbercourse.filmlibrary.model.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserDto extends GenericDto {

    private String login;

    private String password;

    private String firstName;

    private String lastName;

    private String middleName;

    private LocalDate birthDate;

    private String phone;

    private String address;

    private String email;

    private LocalDateTime createdWhen;

    private Role role;

    private Set<Long> ordersIds;
}
