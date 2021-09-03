package com.example.demo.student;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(StudentAdapter::studentDTOFromStudent)
                .collect(Collectors.toList());
    }

    public StudentDTO addStudent(StudentDTO studentDTO) {
        Student student = StudentAdapter.studentFromStudentDTO(studentDTO);

        Boolean existsEmail = studentRepository
                .selectExistsEmail(student.getEmail());
        if (existsEmail) {
            throw new BadRequestException(
                    "Email " + student.getEmail() + " taken");
        }
        return StudentAdapter
                .studentDTOFromStudent(studentRepository.save(student));
    }

    public void deleteStudent(Long id) {
        if(!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Student with id " + id + " does not exists");
        }
        studentRepository.deleteById(id);
    }

    public StudentDTO findById(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Student with id " + id + " does not exists");
        }
        return StudentAdapter.studentDTOFromStudent(
                studentRepository.findById(id).orElse(null)
        );
    }
}