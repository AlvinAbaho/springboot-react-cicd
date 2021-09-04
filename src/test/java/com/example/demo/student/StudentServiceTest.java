package com.example.demo.student;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    private StudentService underTest;

    @Mock
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        underTest = new StudentService(studentRepository);
    }

    @Test
    void getAllStudents() {
        // given
        Pageable pageable = PageRequest.of(0, 100);

        given(studentRepository.findAll(pageable))
                .willReturn(new PageImpl<>(new ArrayList<>()));

        // when
        underTest.getAllStudents(pageable);
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        // then
        verify(studentRepository).findAll(pageableArgumentCaptor.capture());

        Pageable capturedPageable = pageableArgumentCaptor.getValue();

        assertThat(capturedPageable).isEqualTo(pageable);
    }

    @Test
    void addStudent() {
        // given
        StudentDTO studentDTO = new StudentDTO(
                1L,
                "Jamila",
                "jamila@gmail.com",
                Gender.FEMALE
        );

        // when
        underTest.addStudent(studentDTO);

        // then
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);

        verify(studentRepository).save(studentArgumentCaptor.capture());

        Student capturedStudent = studentArgumentCaptor.getValue();

        assertThat(capturedStudent).isEqualTo(
                StudentAdapter.studentFromStudentDTO(studentDTO)
        );
    }

    @Test
    void willThrowWhenEmailIsTaken() {
        // given
        StudentDTO studentDTO = new StudentDTO(
                1L,
                "Jamila",
                "jamila@gmail.com",
                Gender.FEMALE
        );

        given(studentRepository.selectExistsEmail(anyString())).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.addStudent(studentDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + studentDTO.getEmail() + " taken");

        verify(studentRepository, never()).save(any());
    }

    @Test
    void itShouldDeleteStudent() {
        // given
        Long studentId = 1L;
        given(studentRepository.existsById(studentId)).willReturn(true);

        // when
        underTest.deleteStudent(studentId);

        // then
        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(studentRepository).deleteById(longArgumentCaptor.capture());

        Long capturedId = longArgumentCaptor.getValue();

        assertThat(capturedId).isEqualTo(studentId);
    }

    @Test
    void itShouldFindStudentGivenId() {
        // given
        Long studentId = 1L;

        given(studentRepository.existsById(studentId)).willReturn(true);

        // when
        underTest.findById(studentId);

        // then
        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(studentRepository).findById(longArgumentCaptor.capture());

        Long capturedId = longArgumentCaptor.getValue();

        assertThat(capturedId).isEqualTo(studentId);
    }


    @Test
    void itShouldThrowIfDeleteNonExistingStudent() {
        // given
        Long studentId = 1L;
        given(studentRepository.existsById(studentId)).willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteStudent(studentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student with id " + studentId + " does not exists");

        verify(studentRepository, never()).deleteById(any());

    }
}