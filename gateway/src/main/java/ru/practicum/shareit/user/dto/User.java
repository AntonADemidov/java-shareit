package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class User {
    @NonFinal
    Long id;
    @NotBlank
    String name;
    @Email
    @NotNull
    String email;
}