package com.pt.controller;

import com.pt.DTO.TokenRefreshDTO;
import com.pt.DTO.ViewUserDTO;
import com.pt.configSecurity.jwt.ConfigJwtUtils;
import com.pt.entity.RefreshToken;
import com.pt.req.*;
import com.pt.service.RefreshTokenService;
import com.pt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final ConfigJwtUtils JwtUtils;

    private final UserService userService;

    private final RefreshTokenService refreshTokenService;

    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/users/")
    public List<ViewUserDTO> getUsers() throws Exception {
        List<ViewUserDTO> viewUserDTOS=this.userService.listUserData();
        return viewUserDTOS;
    }
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest userRequest) throws Exception {
        this.userService.createUser(userRequest);
        return new ResponseEntity<>("add user successfully", HttpStatus.OK);
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> SignUp(@RequestBody SignUp signUp) throws Exception {
        return this.userService.signUp(signUp);
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> SignIn(@RequestBody SignIn signIn) throws Exception {
        return this.userService.signIn(signIn);
    }
    @PutMapping("/users")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest updateUserRequest) throws Exception {

        UpdateUserRequest updateUser = new UpdateUserRequest();
        LocalDateTime current = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String formatted = current.format(formatter);
        String encodedPassword = passwordEncoder.encode(updateUserRequest.getPassword());

        updateUser.setEmail(updateUserRequest.getEmail());
        updateUser.setId(updateUserRequest.getId());
        updateUser.setName(updateUserRequest.getName());
        updateUser.setPassword(encodedPassword);
        updateUser.setUserAuth(updateUserRequest.getUserAuth());
        updateUser.setPhone(updateUserRequest.getPhone());
        updateUser.setUpdatedAt(formatted);
        return ResponseEntity.ok(this.userService.updateUser(updateUser));
    }
    @DeleteMapping(path = "/users/{id}")
    public ResponseEntity<?> deleteUser( @PathVariable String id) throws Exception {
        return this.userService.deleteUser(id);
    }
    @GetMapping(path = "/users/detail/{id}")
    public ResponseEntity<?> getDetailUser( @PathVariable String id) throws Exception {
        return this.userService.userDetail(id);
    }
    @PostMapping("/refreshToken")
    public TokenRefreshDTO refreshToken(@RequestBody TokenRefreshRequest tokenRefreshRequest) {
        return refreshTokenService.findByToken(tokenRefreshRequest.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(userInfo -> {
                    String accessToken = JwtUtils.generateToken(userInfo.getEmail(), userInfo.getId());
                    return TokenRefreshDTO.builder()
                            .accessToken(accessToken)
                            .token(tokenRefreshRequest.getToken())
                            .build();
                }).orElseThrow(() -> new RuntimeException(
                        "Refresh token is not in database!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() throws Exception {
        return this.userService.logoutUser();
    }
}
