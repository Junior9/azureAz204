package com.azure204.cloud.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.azure204.cloud.model.User;
import com.azure204.cloud.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping("/user")   
    public CosmosPagedIterable<Object> getUsers(){
        return this.userService.getUsers();
    }

    @GetMapping("/user/{id}")
    public CosmosPagedIterable<Object> getUserById(@PathVariable String id){
        return this.userService.getUserById(id);
    }

    @PostMapping("/user")
    public void add(@RequestBody User user){
        this.userService.addUser(user);
    }

    @PutMapping("/user")
    public void update(@RequestBody User user){
        this.userService.update(user);
    }

    @DeleteMapping("/user/{id}")
    public void delete(@PathVariable String id){
        this.userService.delete(id);
    }
}
