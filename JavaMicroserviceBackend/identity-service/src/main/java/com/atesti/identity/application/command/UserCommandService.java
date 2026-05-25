package com.atesti.identity.application.command;

import com.atesti.identity.application.dto.CreateUserCommand;
import com.atesti.identity.application.dto.UpdateUserCommand;
import com.atesti.identity.application.dto.UserResponse;
import com.atesti.identity.domain.model.User;
import com.atesti.identity.domain.repository.UserRepository;
import com.atesti.identity.exception.BadRequestException;
import com.atesti.identity.exception.ResourceNotFoundException;
import com.atesti.identity.infrastructure.kafka.UserEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher userEventPublisher;

    @Transactional
    public UserResponse create(CreateUserCommand command) {
        if (command.getUsername() == null || command.getPassword() == null) {
            throw new BadRequestException("Username i password su obavezni");
        }
        if (userRepository.existsByUsername(command.getUsername())) {
            throw new BadRequestException("Username već postoji");
        }

        User user = User.builder()
                .username(command.getUsername())
                .password(passwordEncoder.encode(command.getPassword()))
                .ime(command.getIme())
                .prezime(command.getPrezime())
                .email(command.getEmail())
                .isActive(true)
                .build();

        user = userRepository.save(user);
        userEventPublisher.publish("CREATED", user);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserCommand command) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.updateDetails(command.getUsername(), command.getIme(), command.getPrezime(), command.getEmail());
        if (command.getPassword() != null && !command.getPassword().isBlank()) {
            user.changePassword(passwordEncoder.encode(command.getPassword()));
        }

        user = userRepository.save(user);
        userEventPublisher.publish("UPDATED", user);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse deactivate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.deactivate();
        user = userRepository.save(user);
        userEventPublisher.publish("UPDATED", user);
        return UserResponse.from(user);
    }
}
