package com.example.demo.integration;


import com.example.demo.student.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-it.properties"
)
@AutoConfigureMockMvc
public class StudentIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    private final Faker faker = new Faker();

    @Test
    public void itCanRegisterNewStudent() throws Exception {
        // given
        String name = String.format("%s %s", faker.name().firstName(), faker.name().lastName());
        String email = String.format("%s@miu.edu", StringUtils.trimAllWhitespace(name).toLowerCase());
        StudentDTO studentDTO = new StudentDTO(
                null,
                name,
                email,
                Gender.FEMALE
        );

        // when
        ResultActions resultActions = mockMvc
                .perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)));

        // then
        resultActions.andExpect(status().isCreated());

        List<StudentDTO> studentDTOS = studentRepository.findAll()
                .stream().map(StudentAdapter::studentDTOFromStudent)
                .collect(Collectors.toList());

        assertThat(studentDTOS)
                .usingElementComparatorIgnoringFields("id")
                .contains(studentDTO);
    }

    @Test
    void canDeleteStudent() throws Exception {
        // given
        String name = String.format(
                "%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );

        String email = String.format("%s@amigoscode.edu",
                StringUtils.trimAllWhitespace(name.trim().toLowerCase()));

        Student student = new Student(
                name,
                email,
                Gender.FEMALE
        );

        MvcResult result = mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
                .andReturn();

        String contentAsString = result
                .getResponse()
                .getContentAsString();

        StudentDTO returnedStudentDTO = objectMapper
                .readValue(contentAsString, new TypeReference<>() {
                });

        long id = returnedStudentDTO.getId();

        // when
        ResultActions resultActions = mockMvc
                .perform(delete("/api/v1/students/" + id));

        // then
        resultActions.andExpect(status().isNoContent());
        boolean exists = studentRepository.existsById(id);
        assertThat(exists).isFalse();
    }
}
