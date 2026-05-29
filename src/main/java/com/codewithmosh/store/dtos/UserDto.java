package com.codewithmosh.store.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor

public class UserDto {
    private Long id;
    private String name;
    private String email;

}
