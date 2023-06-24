package com.noteverso.application;

import com.noteverso.note.service.NoteService;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication(scanBasePackages = {"com.noteverso", "com.noteverso.note"})
@RestController
@AllArgsConstructor
public class NoteversoApplication {
	private NoteService noteService;

	@GetMapping("/")
	public String createNote() {
		return noteService.sayHello();
	}

	public static void main(String[] args) {
		SpringApplication.run(NoteversoApplication.class, args);
	}

}

