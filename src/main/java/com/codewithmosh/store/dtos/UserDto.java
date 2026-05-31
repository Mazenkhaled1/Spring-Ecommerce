package com.codewithmosh.store.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter


public class UserDto {
    private Long id;
    private String name;
    private String email;

}
