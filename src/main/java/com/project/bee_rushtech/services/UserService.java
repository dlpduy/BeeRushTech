package com.project.bee_rushtech.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    public User handleUpdateUser(User user) {
        return this.userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public boolean checkUserExists(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.getUserByEmail(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndId(String token, Long id) {
        return this.userRepository.findByRefreshTokenAndId(token, id);
    }

    public void updatePasswordResetToken(String token, User user) {
        user.setPasswordResetToken(token);
        this.userRepository.save(user);
    }

    public User getUserByPasswordResetToken(String token) {
        return this.userRepository.findByPasswordResetToken(token);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

}
