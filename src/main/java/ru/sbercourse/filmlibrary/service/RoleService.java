package ru.sbercourse.filmlibrary.service;

import org.springframework.stereotype.Service;
import ru.sbercourse.filmlibrary.model.Role;
import ru.sbercourse.filmlibrary.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository repository;

    public RoleService(RoleRepository repository) {
        this.repository = repository;
    }


    public Role create(Role newRole) {
        Role existingRole = repository.getRoleByTitle(newRole.getTitle());
        return existingRole != null
                ? existingRole
                : repository.save(newRole);
    }

    public Role getByTitle(String title) {
        return repository.getRoleByTitle(title);
    }

}
