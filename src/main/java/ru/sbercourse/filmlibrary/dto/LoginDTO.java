package ru.sbercourse.filmlibrary.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
  private String login;
  private String password;
}
