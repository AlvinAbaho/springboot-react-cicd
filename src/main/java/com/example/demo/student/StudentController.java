package com.example.demo.student;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "api/v1/students")
@AllArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<?> getAllStudents() {
        return new ResponseEntity<>(
                studentService.getAllStudents(PageRequest.of(0, 100)),
                HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addStudent(@Valid @RequestBody StudentDTO studentDTO) {
        return new ResponseEntity<>(
                studentService.addStudent(studentDTO),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id){
        studentService.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
