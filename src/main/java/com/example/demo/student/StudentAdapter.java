package com.example.demo.student;

public class StudentAdapter {

    public static Student studentFromStudentDTO(StudentDTO studentDTO) {
        return new Student(
                studentDTO.getName(),
                studentDTO.getEmail(),
                studentDTO.getGender());
    }

    public static StudentDTO studentDTOFromStudent(Student student) {
        return new StudentDTO(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getGender());
    }
}
