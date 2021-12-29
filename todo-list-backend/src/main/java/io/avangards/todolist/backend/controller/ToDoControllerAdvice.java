package io.avangards.todolist.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.avangards.todolist.backend.exception.NotFoundException;

@ControllerAdvice
public class ToDoControllerAdvice {

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	void employeeNotFoundHandler(NotFoundException e) {
		// Do nothing
	}

}
