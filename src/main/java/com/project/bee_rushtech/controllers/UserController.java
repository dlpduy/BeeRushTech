package com.project.bee_rushtech.controllers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.bee_rushtech.dtos.AuthorizeDTO;
import com.project.bee_rushtech.dtos.ChangePasswordDTO;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.responses.UserResponse;
import com.project.bee_rushtech.services.GoogleService;
import com.project.bee_rushtech.services.UserService;
import com.project.bee_rushtech.utils.SecurityUtil;
import com.project.bee_rushtech.utils.annotation.ApiMessage;
import com.project.bee_rushtech.utils.errors.InvalidException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("${api.prefix}")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final GoogleService googleUserInfoService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, SecurityUtil securityUtil,
            OAuth2AuthorizedClientService authorizedClientService, GoogleService googleUserInfoService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.securityUtil = securityUtil;
        this.authorizedClientService = authorizedClientService;
        this.googleUserInfoService = googleUserInfoService;

    }

    @GetMapping("/user/get-user")
    @ApiMessage("Get user successfully")
    public ResponseEntity<UserResponse> getUserLogin(
            HttpServletRequest request)
            throws InvalidException {

        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Long userId = this.securityUtil.getUserFromToken(token).getId();
        User user = this.userService.findById(userId);
        if (user == null) {
            throw new InvalidException("You are not authorized");
        }
        UserResponse userResponse = new UserResponse(user.getId(), user.getFullName(), user.getEmail(),
                user.getPhoneNumber(), user.getAddress(), user.getDateOfBirth(), user.getRole());
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @PutMapping("/user/profile")
    public ResponseEntity<UserResponse> update(HttpServletRequest request,
            @RequestBody User user)
            throws InvalidException {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Long userId = this.securityUtil.getUserFromToken(token).getId();
        User currentUser = this.userService.findById(userId);
        if (currentUser == null) {
            throw new InvalidException("User not found");
        }
        if (user.getFullName() == null || user.getPhoneNumber() == null
                || user.getAddress() == null) {
            throw new InvalidException("Please fill all fields");
        }
        currentUser.setFullName(user.getFullName());
        currentUser.setPhoneNumber(user.getPhoneNumber());
        currentUser.setAddress(user.getAddress());
        currentUser.setDateOfBirth(user.getDateOfBirth());
        User updatedUser = this.userService.handleUpdateUser(currentUser);
        UserResponse userResponse = new UserResponse(updatedUser.getId(), updatedUser.getFullName(),
                updatedUser.getEmail(),
                updatedUser.getPhoneNumber(), updatedUser.getAddress(), updatedUser.getDateOfBirth(),
                updatedUser.getRole());
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @GetMapping("/user/profile")
    @ApiMessage("Get information successfully")
    public ResponseEntity<UserResponse> getProfileUser(HttpServletRequest request)
            throws InvalidException {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Long userId = this.securityUtil.getUserFromToken(token).getId();
        User user = this.userService.findById(userId);
        UserResponse userResponse = new UserResponse(user.getId(), user.getFullName(), user.getEmail(),
                user.getPhoneNumber(), user.getAddress(), user.getDateOfBirth(), user.getRole());
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @GetMapping("/user/{id}")
    @ApiMessage("Get user successfully")
    public ResponseEntity<UserResponse> getUserById(HttpServletRequest request,
            @PathVariable Long id)
            throws InvalidException {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String role = this.securityUtil.getUserFromToken(token).getRole();
        if (!role.equals("ADMIN")) {
            throw new InvalidException("You are not authorized");
        }
        User user = this.userService.findById(id);
        if (user == null) {
            throw new InvalidException("User not found");
        }
        UserResponse userResponse = new UserResponse(user.getId(), user.getFullName(), user.getEmail(),
                user.getPhoneNumber(), user.getAddress(), user.getDateOfBirth(), user.getRole());
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @GetMapping("/user")
    @ApiMessage("Get all users successfully")
    public ResponseEntity<List<UserResponse>> getAllUsers(HttpServletRequest request)
            throws InvalidException {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String role = this.securityUtil.getUserFromToken(token).getRole();
        if (!role.equals("ADMIN")) {
            throw new InvalidException("You are not authorized");
        }
        List<User> users = this.userService.findAllUsers();
        if (users == null) {
            throw new InvalidException("Users not found");
        }
        List<UserResponse> userResponses = new ArrayList();
        for (User user : users) {
            UserResponse userResponse = new UserResponse(user.getId(), user.getFullName(), user.getEmail(),
                    user.getPhoneNumber(), user.getAddress(), user.getDateOfBirth(), user.getRole());
            userResponses.add(userResponse);
        }

        return ResponseEntity.status(HttpStatus.OK).body(userResponses);
    }

    @PutMapping("/user/change-password")
    @ApiMessage("Change password successfully")
    public ResponseEntity<Void> changePassword(
            @Valid HttpServletRequest request,
            @RequestBody ChangePasswordDTO changePassword)
            throws InvalidException {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Long userId = this.securityUtil.getUserFromToken(token).getId();
        User currentUser = this.userService.findById(userId);
        if (currentUser == null) {
            throw new InvalidException("User not found");
        }
        if (!this.passwordEncoder.matches(changePassword.getOldPassword(), currentUser.getPassword())) {
            throw new InvalidException("Old password is incorrect");
        }
        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            throw new InvalidException("New password and confirm password are not the same");
        }
        String hashPassword = this.passwordEncoder.encode(changePassword.getNewPassword());
        currentUser.setPassword(hashPassword);
        this.userService.handleUpdateUser(currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PutMapping("/user/authorize")
    @ApiMessage("Authorize user successfully")
    public ResponseEntity<UserResponse> authorizeUser(HttpServletRequest request,
            @RequestBody AuthorizeDTO authorizeDTO)
            throws InvalidException {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String role = this.securityUtil.getUserFromToken(token).getRole();
        if (!role.equals("ADMIN")) {
            throw new InvalidException("You are not authorized");
        }
        User currentUser = this.userService.getUserByEmail(authorizeDTO.getEmail());
        if (currentUser == null) {
            throw new InvalidException("User not found");
        }
        if (currentUser.getEmail().equals("beerushtech@gmail.com")) {
            throw new InvalidException("You can not change role of this user");
        }
        currentUser.setRole(authorizeDTO.getRole());
        User updatedUser = this.userService.handleUpdateUser(currentUser);
        UserResponse userResponse = new UserResponse(updatedUser.getId(), updatedUser.getFullName(),
                updatedUser.getEmail(),
                updatedUser.getPhoneNumber(), updatedUser.getAddress(), updatedUser.getDateOfBirth(),
                updatedUser.getRole());
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

}
