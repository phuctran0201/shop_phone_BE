package com.pt.service.impl;

import com.pt.DTO.TokenRefreshDTO;
import com.pt.DTO.ViewUserDTO;
import com.pt.configSecurity.UserDetailsImpl;
import com.pt.configSecurity.jwt.ConfigJwtUtils;
import com.pt.entity.RefreshToken;
import com.pt.entity.User;
import com.pt.exceptionMessage.MessageResponse;
import com.pt.repository.UserRepository;
import com.pt.req.SignIn;
import com.pt.req.SignUp;
import com.pt.req.CreateUserRequest;
import com.pt.req.UpdateUserRequest;
import com.pt.service.RefreshTokenService;
import com.pt.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final AuthenticationManager authenticationManager;


    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ConfigJwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;


    @Autowired
    public UserServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder, ConfigJwtUtils jwtUtils, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public List<ViewUserDTO> listUserData() throws Exception {
        String role = jwtUtils.getRoleFromJWT();
        if (role != null && role.equals("ADMIN")) {
            List<User> users = userRepository.findAllByOrderByCreatedAtDescUpdatedAtDesc();
            List<ViewUserDTO> viewUserDTOs = users.stream()
                    .map(user -> modelMapper.map(user, ViewUserDTO.class))
                    .collect(Collectors.toList());
            return viewUserDTOs;
        } else {
            throw new Exception("Access denied");
        }
    }

    @Override
    public void createUser(CreateUserRequest userRequest) throws Exception {
        User user=modelMapper.map(userRequest,User.class);
        userRepository.save(user);
    }

    @Override
    public ResponseEntity<?> signUp(SignUp signUp) {
        try {
            String name = signUp.getName();
            String email = signUp.getEmail();
            String password = signUp.getPassword();
            Integer phone = signUp.getPhone();
            String userAuth = signUp.getUserAuth();

            Optional<User> checkUser=userRepository.findByEmail(email);

            Pattern pattern = Pattern.compile("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
            Matcher matcher = pattern.matcher(email);
            boolean isCheckEmail = matcher.matches();
            MessageResponse errorResponse = new MessageResponse();
            if (name == null || email == null || password == null || phone == null || userAuth==null) {
                errorResponse.setMessage("The input is required");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }else if (!isCheckEmail){
                errorResponse.setMessage("The input is email");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }else if (checkUser.isPresent()){
                errorResponse.setMessage("email is already");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            User user = modelMapper.map(signUp, User.class);
            LocalDateTime current = LocalDateTime.now();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

            String formatted = current.format(formatter);
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
            user.setCreatedAt(formatted);
            user.setUpdatedAt(formatted);

            userRepository.save(user);
            return ResponseEntity.ok("User signed up successfully");
        } catch (Exception e) {

            return ResponseEntity.status(500).body(
                    new ErrorMessage( "An error occurred during sign up")
            );
        }
    }


    @Override
    public ResponseEntity<?> signIn(SignIn signIn) {
        try {
            String email = signIn.getEmail();
            String password = signIn.getPassword();

            Optional<User> user = userRepository.findByEmail(email);
            if (email == null || email==null ) {
                MessageResponse errorResponse = new MessageResponse();
                errorResponse.setMessage("Email and password are required");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            if (user.isEmpty()) {
                MessageResponse errorResponse = new MessageResponse();
                errorResponse.setMessage("Email is not registered");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            User foundUser = user.get();
            boolean passwordMatch = passwordEncoder.matches(password, foundUser.getPassword());

            if (!passwordMatch) {
                MessageResponse errorResponse = new MessageResponse();
                errorResponse.setMessage("Incorrect password");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(foundUser.getEmail());
            TokenRefreshDTO token =new TokenRefreshDTO();
            token.setAccessToken(jwtUtils.generateToken(foundUser.getEmail(), foundUser.getId()));
            token.setToken(refreshToken.getToken());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorMessage( "An error occurred during sign in")
            );
        }
    }

    @Override
    public ResponseEntity<?> updateUser(UpdateUserRequest updateUserRequest) throws Exception {
        try {
            Optional<User> checkUser=userRepository.findById(updateUserRequest.getId());
            if (checkUser.isEmpty()) {
                MessageResponse errorResponse = new MessageResponse();
                errorResponse.setMessage("Invalid id");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            User user = modelMapper.map(updateUserRequest, User.class);
            user.setCreatedAt(checkUser.get().getCreatedAt());
            userRepository.save(user);
            MessageResponse successResponse = new MessageResponse();
            successResponse.setMessage("User updated successfully");
            successResponse.setStatus("OK");
            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorMessage( "An error occurred during update User")
            );
        }

    }

    @Override
    public ResponseEntity<?> deleteUser(String id) throws Exception {

        try {
            Optional<User> checkUser=userRepository.findById(id);
            if (checkUser.isEmpty()) {
                MessageResponse errorResponse = new MessageResponse();
                errorResponse.setMessage("Invalid id");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

                userRepository.deleteById(id);
                MessageResponse successResponse = new MessageResponse();
                successResponse.setMessage("delete user successfully");
                successResponse.setStatus("OK");
                return ResponseEntity.ok(successResponse);




        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorMessage( "An error occurred during delete User")
            );
        }
    }

    @Override
    public ResponseEntity<?> userDetail(String id) throws Exception {
        try {
            Optional<User> checkUser=userRepository.findById(id);
            if (checkUser.isEmpty()) {
                MessageResponse errorResponse = new MessageResponse();
                errorResponse.setMessage("Invalid id");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            User user=checkUser.get();
            ViewUserDTO viewUserDTO=modelMapper.map(user,ViewUserDTO.class);

            return ResponseEntity.status(HttpStatus.OK).body(viewUserDTO);


        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorMessage( "An error occurred during get detail User")
            );
        }
    }

    @Override
    public ResponseEntity<?> logoutUser() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOptional = userRepository.findByEmail(authentication.getName());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String userId = user.getId();
            refreshTokenService.deleteByUserId(userId);
            MessageResponse successResponse = new MessageResponse();
            successResponse.setMessage("LOG OUT SUCCESSFULLY");
            successResponse.setStatus("OK");
            return ResponseEntity.ok(successResponse);
        } else {
            MessageResponse errorResponse = new MessageResponse();
            errorResponse.setMessage("User not found");
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }


}
