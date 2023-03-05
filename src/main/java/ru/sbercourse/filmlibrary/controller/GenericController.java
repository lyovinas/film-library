package ru.sbercourse.filmlibrary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sbercourse.filmlibrary.model.GenericModel;
import ru.sbercourse.filmlibrary.repository.GenericRepository;

import java.util.List;

//@RestController
public abstract class GenericController<T extends GenericModel> {
    private GenericRepository<T> repository;


    @Operation(summary = "Получить все записи", description = "Позволяет получить полный список записей",
            method = "getAll")
    @GetMapping(value = "getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<T>> getAll() {
//        return ResponseEntity.status(HttpStatus.OK).body(repository.findAll());
        List<T> resultingList = repository.findAll();
        return resultingList.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.status(HttpStatus.OK).body(resultingList);
    }

    @Operation(summary = "Получить запись по ID", description = "Позволяет получить одну запись по заданному ID",
            method = "getById")
    @GetMapping(value = "/getById", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<T> getById(@RequestParam("id") @Parameter(description = "Идентификатор записи") Long id) {
        return repository.findById(id).map(e -> ResponseEntity.status(HttpStatus.OK).body(e))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Создать новую запись", description = "Позволяет создать переданную запись",
            method = "create")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<T> create(@RequestBody T newEntity) {
        return newEntity.getId() != null && repository.existsById(newEntity.getId()) ?
                ResponseEntity.status(HttpStatus.CONFLICT).build()
                : ResponseEntity.status(HttpStatus.CREATED).body(repository.save(newEntity));
    }

    @Operation(summary = "Изменить существующую запись", description = "Позволяет заменить существующую запись на обновленную",
            method = "update")
    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<T> update(@RequestBody T updatedEntity) {
        return updatedEntity.getId() != null && repository.existsById(updatedEntity.getId()) ?
                ResponseEntity.ok(repository.save(updatedEntity))
                : ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }

    @Operation(summary = "Удалить запись по ID", description = "Позволяет удалить запись по заданному ID",
            method = "deleteById")
    @DeleteMapping(value = "/deleteById")
    public ResponseEntity<T> deleteById(@RequestParam("id") @Parameter(description = "Идентификатор записи") Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        } else return ResponseEntity.notFound().build();
    }


    protected void setRepository(GenericRepository<T> repository) {
        this.repository = repository;
    }
}
