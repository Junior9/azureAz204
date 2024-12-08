package com.azure204.cloud.service;

import org.springframework.stereotype.Service;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.azure204.cloud.model.User;
import com.azure204.cloud.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    public CosmosPagedIterable<Object>  getUsers(){
        return this.userRepository.getUsers();
    }

    public CosmosPagedIterable<Object>  getUserById(String id){
        return this.userRepository.getUserById(id);
    }

    public void addUser(User user){
        this.userRepository.add(user);
    }

    public void update(User user) {
        this.userRepository.updateUser(user);
    }

    public void delete(String id) {
        this.userRepository.delete(id);
    }

}
