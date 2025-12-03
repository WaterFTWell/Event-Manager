package com.example.Event_Manager.user.service;

import com.example.Event_Manager.user.repository.UserRepository;
import com.example.Event_Manager.user.User;
import com.example.Event_Manager.user.dto.request.ChangePasswordRequest;
import com.example.Event_Manager.user.dto.request.UpdateUserDTO;
import com.example.Event_Manager.user.dto.response.UserDTO;
import com.example.Event_Manager.user.exceptions.UserNotFoundException;
import com.example.Event_Manager.user.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDTO getUserProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toDTO(user);
    }

    @Transactional
    public UserDTO updateUserProfile(Long userId, UpdateUserDTO updateDTO) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // mapujemy tylko to co przyszlo w requescie
        userMapper.updateEntity(user, updateDTO);

        User savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {

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