package io.avangards.todolist.backend.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.avangards.todolist.backend.exception.NotFoundException;
import io.avangards.todolist.backend.repository.ToDoRepository;
import io.avangards.todolist.model.ToDo;

@RestController
public class ToDoController {

	private final ToDoRepository toDoRepository;

	public ToDoController(ToDoRepository toDoRepository) {
		this.toDoRepository = toDoRepository;
	}

	@GetMapping("/api/todos")
	@CrossOrigin(origins = { "*" })
	public Iterable<ToDo> getItems() {
		return toDoRepository.findAll();
	}

	@PostMapping("/api/todos")
	@CrossOrigin(origins = { "*" }, exposedHeaders = { "Location" })
	public void createItem(@RequestBody ToDo toDo, HttpServletRequest request, HttpServletResponse response) {
		toDo.setCreatedDt(new Date());
		ToDo createdItem = toDoRepository.save(toDo);
		response.addHeader(HttpHeaders.LOCATION,
				request.getRequestURL().append("/").append(createdItem.getId()).toString());
		response.setStatus(HttpStatus.CREATED.value());
	}

	@GetMapping("/api/todos/{id}")
	@CrossOrigin(origins = { "*" })
	public ToDo getItems(@PathVariable long id) {
		return toDoRepository.findById(id).orElseThrow(() -> new NotFoundException());
	}

	@PutMapping("/api/todos/{id}")
	@CrossOrigin(origins = { "*" })
	public ToDo updateItem(@RequestBody ToDo toDo) {
		return toDoRepository.save(toDo);
	}

	@DeleteMapping("/api/todos/{id}")
	@CrossOrigin(origins = { "*" })
	public void deleteItem(@PathVariable long id) {
		toDoRepository.findById(id).orElseThrow(() -> new NotFoundException());
		toDoRepository.deleteById(id);
	}

}
