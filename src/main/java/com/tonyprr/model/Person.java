package com.tonyprr.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Person {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message="El nombre no debe ser nullo o en blanco")
    private String name;

    @Email(message = "No es un email válido")
    private String email;

    @Pattern(regexp = "C|S|V|D", message = "No es un estado civil válido")
    @Column(name = "marital_status", length = 1)
    private String maritalStatus;

    private LocalDate birth;
}