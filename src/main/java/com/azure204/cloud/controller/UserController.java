package com.azure204.cloud.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azure204.cloud.model.User;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class UserController {

    @GetMapping("/user")
    public ResponseEntity<User> getUserById(String id){
        
        return ResponseEntity.status(HttpStatus.OK).body(User.builder().id("1").name("Test 1").build());
    }



}
