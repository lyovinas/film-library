package ru.sbercourse.filmlibrary.constants;

import java.util.List;

public interface SecurityConstants {
  List<String> RESOURCES_WHITE_LIST = List.of(
      "/resources/**",
      "/js/**",
      "/css/**",
      "/",
      "/login",
      "/users/registration",
      "/users/remember-password",
      "/users/change-password",
      "/error",
      // -- Swagger UI v3 (OpenAPI)
      "/swagger-ui/**",
      "/v3/api-docs/**");

  List<String> FILMS_WHITE_LIST = List.of(
      "/films",
      "/films/{id}",
      "/films/search"
  );

  List<String> FILMS_PERMISSION_LIST = List.of(
      "/films/add",
      "/films/update",
      "/films/update/{id}",
      "/films/delete/{id}",
      "/films/restore/{id}",
      "/films/addDirector",
      "/films/addDirector/{filmId}"
//      "/publish/get-film/*",
//      "/films/download/*"
  );

  List<String> DIRECTORS_WHITE_LIST = List.of(
      "/directors",
//      "/directors/{id}",
      "/directors/search"
  );

  List<String> DIRECTORS_PERMISSION_LIST = List.of(
      "/directors/add",
      "/directors/delete/{id}",
      "/directors/restore/{id}",
      "/directors/update/{id}",
      "/directors/update",
      "/directors/addFilm/{directorId}",
      "/directors/addFilm"
  );

  List<String> USERS_WHITE_LIST = List.of(
      "/users/profile/{id}",
      "/users/profile/update",
      "/users/profile/update/{id}"
  );

  List<String> USERS_PERMISSION_LIST = List.of(
      "/users",
      "/users/add",
      "/users/delete",
      "/users/add-manager"
  );
}
