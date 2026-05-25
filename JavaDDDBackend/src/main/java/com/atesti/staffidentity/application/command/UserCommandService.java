package com.atesti.staffidentity.application.command;

import com.atesti.shared.exception.BadRequestException;
import com.atesti.shared.exception.ResourceNotFoundException;
import com.atesti.staffidentity.application.dto.command.CreateUserCommand;
import com.atesti.staffidentity.application.dto.command.UpdateUserCommand;
import com.atesti.staffidentity.application.dto.query.UserResponse;
import com.atesti.staffidentity.domain.model.User;
import com.atesti.staffidentity.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserCommand command) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.updateDetails(command.getUsername(), command.getIme(), command.getPrezime(), command.getEmail());
        if (command.getPassword() != null && !command.getPassword().isBlank()) {
            user.changePassword(passwordEncoder.encode(command.getPassword()));
        }

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse deactivate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.deactivate();
        return UserResponse.from(userRepository.save(user));
    }
}
