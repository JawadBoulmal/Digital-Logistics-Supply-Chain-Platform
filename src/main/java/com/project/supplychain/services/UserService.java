package com.project.supplychain.services;

import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.SalesOrderMapper;
import com.project.supplychain.mappers.usersMappers.UserMapper;
import com.project.supplychain.models.SalesOrder;
import com.project.supplychain.models.user.User;
import com.project.supplychain.repositories.SalesOrderRepository;
import com.project.supplychain.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public HashMap<String, Object> get(UUID id) {
        User found = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));
        HashMap<String, Object> result = new HashMap<>();
        result.put("User", userMapper.toDTO(found));
        return result;
    }

}
