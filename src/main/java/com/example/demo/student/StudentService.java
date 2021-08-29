package com.example.demo.student;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@AllArgsConstructor
@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public Page<StudentDTO> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable)
                .map(StudentAdapter::studentDTOFromStudent);
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
}