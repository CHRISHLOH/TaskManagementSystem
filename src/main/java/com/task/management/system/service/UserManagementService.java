package com.task.management.system.service;

import com.task.management.system.exception.EntityNotFoundException;
import com.task.management.system.model.entity.User;
import com.task.management.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserManagementService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws EntityNotFoundException {
        User user = userRepository.findUserWithRolesByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь не найден с email: " + email);
        }
        return user;
    }
}
