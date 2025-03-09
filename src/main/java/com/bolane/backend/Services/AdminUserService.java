package com.bolane.backend.Services;

import com.bolane.backend.DTOs.UserRoleDTO;
import com.bolane.backend.Entities.User;
import com.bolane.backend.Exceptions.ResourceNotFoundException;
import com.bolane.backend.Repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class AdminUserService {
    private final UserRepository userRepository;
    private final List<String> validRoles = Arrays.asList("USER", "ADMIN");

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public User updateUserRole(int id, UserRoleDTO userRoleDTO) {
        // validate role

        if (!validRoles.contains(userRoleDTO.getRole())) {
            throw new IllegalArgumentException("Invalid role: " + userRoleDTO.getRole());
        }

        User user = getUserById(id);
        user.setRole(userRoleDTO.getRole());

        return userRepository.save(user);
    }
}
