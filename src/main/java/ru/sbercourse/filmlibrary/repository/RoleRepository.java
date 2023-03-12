package ru.sbercourse.filmlibrary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sbercourse.filmlibrary.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role getRoleByTitle(String title);
}
