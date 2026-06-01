package com.codewithmosh.store.dtos;

import com.codewithmosh.store.validation.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {

    @NotBlank(message = "name is required")
    @Size(max = 255 , message = " name must be less than 255 character" )
    private String name ;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    @Lowercase(message = "email must be in lowercase")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 6 , max = 25 , message = "password must be at least between 6 and 25 character")
    private String password;
}
