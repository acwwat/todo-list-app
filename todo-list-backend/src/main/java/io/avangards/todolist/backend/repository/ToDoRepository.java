package io.avangards.todolist.backend.repository;

import org.springframework.data.repository.CrudRepository;

import io.avangards.todolist.model.ToDo;

public interface ToDoRepository extends CrudRepository<ToDo, Long> {

}
