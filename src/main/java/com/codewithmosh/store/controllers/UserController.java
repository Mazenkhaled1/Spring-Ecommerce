package com.codewithmosh.store.controllers;


import com.codewithmosh.store.dtos.*;
import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


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

    @PostMapping
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder) {

        if(userRepository.existsByEmail(request.getEmail()))
        {
            return ResponseEntity.badRequest().body(
                    Map.of("email" , " email is already registerd. ")
            );
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

          var userDto = new  UserDto(user.getId() , user.getName() , user.getEmail());
          var uri =  uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
          return ResponseEntity.created(uri).body(userDto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable(name = "id") Long id, @RequestBody UpdateUserRequest request)
    {
        var user = userRepository.findById(id).orElse(null);
        if(user == null ) {
            return ResponseEntity.notFound().build();
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        userRepository.save(user);
        var userDto = new  UserDto(user.getId() , user.getName() , user.getEmail());
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") Long id) {
        var user = userRepository.findById(id).orElse(null);
        if(user == null ) {
            return ResponseEntity.notFound().build();
        }
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable(name = "id") Long id ,
            @RequestBody ChangePasswordRequest request
    )
    {
        var user = userRepository.findById(id).orElse(null);
        if(user == null ) {
            return ResponseEntity.notFound().build();
        }
        if(! user.getPassword().equals(request.getOldPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }





}
