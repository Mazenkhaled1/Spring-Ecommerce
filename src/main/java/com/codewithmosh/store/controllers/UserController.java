package com.codewithmosh.store.controllers;


import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;


    @GetMapping("")
    // Method : GET
    public Iterable<UserDto> allUsers(
            @RequestParam(required = false , defaultValue = "" , name = "sort")  String sortBy
    ) {
        if( !Set.of("name", "email").contains(sortBy) ) {
            sortBy = "name" ;
        }
        return userRepository.findAll(Sort.by(sortBy))
                .stream()
                .map(user -> new UserDto(user.getId() , user.getName() , user.getEmail()))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
         var user =  userRepository.findById(id).orElse(null);
         if(user== null ) {
             return ResponseEntity.notFound().build();
         }
         var userDto = new  UserDto(user.getId() , user.getName() , user.getEmail());
         return ResponseEntity.ok(userDto);


    }
}
