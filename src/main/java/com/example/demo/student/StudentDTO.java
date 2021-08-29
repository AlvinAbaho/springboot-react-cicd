package com.example.demo.student;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@AllArgsConstructor
public class StudentDTO {
    @Null
    private Long id;
    @NotBlank
    private String name;
    @Email
    private String email;
    @NotNull
    private Gender gender;
}
