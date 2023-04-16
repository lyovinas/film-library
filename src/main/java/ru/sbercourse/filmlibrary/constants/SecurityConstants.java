package ru.sbercourse.filmlibrary.constants;

import java.util.List;

public interface SecurityConstants {
  class REST {
    public static List<String> FILMS_WHITE_LIST = List.of(
            "/rest/films",
            "/rest/films/getById"
    );

    public static List<String> DIRECTORS_WHITE_LIST = List.of("" +
            "/rest/directors",
            "/rest/directors/getById"
    );

    public static List<String> ORDERS_WHITE_LIST = List.of("" +
            "/rest/orders",
            "/rest/orders/getById"
    );

    public static List<String> USERS_WHITE_LIST = List.of(
            "/rest/users/auth"
    );

    public static List<String> FILMS_PERMISSION_LIST = List.of("/rest/films/add",
            "/rest/films/create",
            "/rest/films/update",
            "/rest/films/deleteById",
            "/rest/films/softDelete/{id}",
            "/rest/films/restore/{id}",
            "/rest/films/addDirector",
            "/rest/films/getFilmsWithDirectors"
    );

    public static List<String> DIRECTORS_PERMISSION_LIST = List.of(
            "/rest/directors/create",
            "/rest/directors/update",
            "/rest/directors/deleteById",
            "/rest/directors/softDelete/{id}",
            "/rest/directors/restore/{id}",
            "/rest/directors/addFilm",
            "/rest/directors/getDirectorsWithFilms"
    );

    public static List<String> ORDERS_PERMISSION_LIST = List.of(
            "/rest/orders/create",
            "/rest/orders/update",
            "/rest/orders/deleteById",
            "/rest/orders/softDelete/{id}",
            "/rest/orders/restore/{id}",
            "/rest/orders/getOrdersByUserId/{userId}",
            "/rest/orders/getRentedAndPurchasedFilmsByUserId/{userId}",
            "/rest/orders/getRentedFilmsByUserId/{userId}",
            "/rest/orders/getPurchasedFilmsByUserId/{userId}"
    );

    public static List<String> USERS_PERMISSION_LIST = List.of(
            "/rest/users",
            "/rest/users/getById",
            "/rest/users/create",
            "/rest/users/update",
            "/rest/users/deleteById",
            "/rest/users/softDelete/{id}",
            "/rest/users/restore/{id}"
    );
  }

  List<String> RESOURCES_WHITE_LIST = List.of(
      "/resources/**",
      "/js/**",
      "/css/**",
      "/",
      "/login",
      "/users/auth",
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
      "/directors/addFilm",
      "/directors/softDelete/{id}"
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

  List<String> ORDERS_WHITE_LIST = List.of(
      "/orders/user",
      "/orders/rentFilm/{filmId}",
      "/orders/rentFilm",
      "/orders/purchaseFilm/{filmId}"
//      "/orders/delete/{id}"
  );

  List<String> ORDERS_PERMISSION_LIST = List.of(
      "/orders/delete/{id}",
      "/orders/restore/{id}"
  );
}
