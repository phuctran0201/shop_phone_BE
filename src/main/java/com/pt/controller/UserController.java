package com.pt.controller;

import com.pt.DTO.TokenRefreshDTO;
import com.pt.DTO.ViewUserDTO;
import com.pt.configSecurity.jwt.ConfigJwtUtils;
import com.pt.entity.RefreshToken;
import com.pt.req.*;
import com.pt.service.RefreshTokenService;
import com.pt.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CrossOrigin(value = "*")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final ConfigJwtUtils JwtUtils;

    private final UserService userService;

    private final RefreshTokenService refreshTokenService;

    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public List<ViewUserDTO> getUsers() throws Exception {
        List<ViewUserDTO> viewUserDTOS=this.userService.listUserData();
        return viewUserDTOS;
    }
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest userRequest) throws Exception {

        return ResponseEntity.ok(this.userService.createUser(userRequest));
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> SignUp(@RequestBody SignUp signUp) throws Exception {
        return ResponseEntity.ok( this.userService.signUp(signUp));
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> SignIn(@RequestBody SignIn signIn, HttpServletResponse response) throws Exception {

        return ResponseEntity.ok( this.userService.signIn(signIn,response));
    }
    @PutMapping("/users")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest updateUserRequest) throws Exception {

        UpdateUserRequest updateUser = new UpdateUserRequest();
        LocalDateTime current = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String formatted = current.format(formatter);
        updateUser.setEmail(updateUserRequest.getEmail());
        updateUser.setId(updateUserRequest.getId());
        updateUser.setName(updateUserRequest.getName());
        updateUser.setAddress(updateUserRequest.getAddress());
        updateUser.setPassword(updateUserRequest.getPassword());
        updateUser.setAvatar(updateUserRequest.getAvatar());
        updateUser.setUserAuth(updateUserRequest.getUserAuth());
        updateUser.setPhone(updateUserRequest.getPhone());
        updateUser.setCity(updateUserRequest.getCity());
        updateUser.setUpdatedAt(formatted);
        return ResponseEntity.ok(this.userService.updateUser(updateUser));
    }
    @DeleteMapping(path = "/users/{id}")
    public ResponseEntity<?> deleteUser( @PathVariable String id) throws Exception {
        return ResponseEntity.ok(this.userService.deleteUser(id));
    }
    @GetMapping(path = "/users/detail/{id}")
    public ResponseEntity<?> getDetailUser( @PathVariable String id) throws Exception {
        return ResponseEntity.ok(this.userService.userDetail(id));
    }
    @PostMapping("/refreshToken")
    public TokenRefreshDTO refreshToken(HttpServletRequest request, @RequestBody(required = false) TokenRefreshRequest tokenRefreshRequest ) {
        // Get the token from the cookie

        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("token")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            throw new RuntimeException("Refresh token is not found in the cookie!");
        }



        String finalRefreshToken = refreshToken;
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(userInfo -> {
                    String accessToken = JwtUtils.generateToken(userInfo.getEmail(), userInfo.getId());
                    return TokenRefreshDTO.builder()
                            .accessToken(accessToken)
                            .token(finalRefreshToken)
                            .build();
                }).orElseThrow(() -> new RuntimeException(
                        "Refresh token is not in the database!"));
    }

    @PostMapping("/signOut")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) throws Exception {
        return this.userService.logoutUser(response);
    }

    @PostMapping("/users/deleteMany")
    public ResponseEntity<?> deleteManyProduct( @RequestBody IdsRequest ids) throws Exception {
        return ResponseEntity.ok( this.userService.deleteMany(ids));
    }
}
