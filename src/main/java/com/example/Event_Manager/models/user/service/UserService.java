package com.example.Event_Manager.models.user.service;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.dto.request.ChangePasswordRequest;
import com.example.Event_Manager.models.user.dto.request.UpdateUserDTO;
import com.example.Event_Manager.models.user.dto.response.UserDTO;
import com.example.Event_Manager.models.user.enums.Status;
import com.example.Event_Manager.models.user.exceptions.UserNotFoundException;
import com.example.Event_Manager.models.user.mapper.UserMapper;
import com.example.Event_Manager.models.user.validation.UserValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidation userValidation;
    private final PasswordEncoder passwordEncoder;

    public UserDTO getUserProfile(Long userId) {
        // sprawdzamy czy id ma sens
        userValidation.checkIfIdValid(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toDTO(user);
    }

    @Transactional
    public UserDTO updateUserProfile(Long userId, UpdateUserDTO updateDTO) {
        userValidation.checkIfIdValid(userId);
        userValidation.checkIfRequestNotNull(updateDTO);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // mapujemy tylko to co przyszlo w requescie
        userMapper.updateEntity(user, updateDTO);

        User savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userValidation.checkIfIdValid(userId);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        userValidation.checkIfIdValid(userId);

        //czy nowe hasła są takie same
        if (!request.newPassword().equals(request.confirmationPassword())) {
            throw new IllegalArgumentException("New password and confirmation password do not match");
        }

        User user = getUserByIdOrThrow(userId);

        //czy stare hasło jest poprawne
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect current password");
        }

        //Ustawiamy nowe hasło
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private User getUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
//nie bedzie tworzenia użytkowników przez adminow przez API