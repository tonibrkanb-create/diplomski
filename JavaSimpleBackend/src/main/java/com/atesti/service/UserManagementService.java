package com.atesti.service;

import com.atesti.dto.CreateUserRequest;
import com.atesti.dto.UpdateUserRequest;
import com.atesti.entity.User;
import com.atesti.exception.BadRequestException;
import com.atesti.exception.ResourceNotFoundException;
import com.atesti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAll() {
        return userRepository.findAllByOrderByIdAsc();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public User create(CreateUserRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new BadRequestException("Username i password su obavezni");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username već postoji");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .ime(request.getIme())
                .prezime(request.getPrezime())
                .email(request.getEmail())
                .isActive(true)
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getIme() != null) user.setIme(request.getIme());
        if (request.getPrezime() != null) user.setPrezime(request.getPrezime());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(user);
    }

    @Transactional
    public User deactivate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsActive(false);
        return userRepository.save(user);
    }
}
