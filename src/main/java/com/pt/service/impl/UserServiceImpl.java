package com.pt.service.impl;

import com.pt.Cokkie.CookieManager;
import com.pt.DTO.TokenRefreshDTO;
import com.pt.DTO.ViewUserDTO;
import com.pt.configSecurity.UserDetailsImpl;
import com.pt.configSecurity.jwt.ConfigJwtUtils;
import com.pt.entity.Product;
import com.pt.entity.RefreshToken;
import com.pt.entity.User;
import com.pt.enums.UserAuth;
import com.pt.exceptionMessage.MessageResponse;
import com.pt.repository.UserRepository;
import com.pt.req.*;
import com.pt.service.RefreshTokenService;
import com.pt.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
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
    public ResponseEntity<?> createUser(CreateUserRequest userRequest) throws Exception {
        try {
            Optional<User> checkUser = userRepository.findByEmail(userRequest.getEmail());

            Pattern pattern = Pattern.compile("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
            Matcher matcher = pattern.matcher(userRequest.getEmail());
            boolean isCheckEmail = matcher.matches();
            MessageResponse errorResponse = new MessageResponse();
            if (userRequest.getEmail() == null || userRequest.getName() == null || userRequest.getPassword() == null || userRequest.getUserAuth() == null) {
                errorResponse.setMessage("The input is required");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            } else if (!isCheckEmail) {
                errorResponse.setMessage("The input is email");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            } else if (checkUser.isPresent()) {
                errorResponse.setMessage("email is already");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }


            User user = modelMapper.map(userRequest, User.class);
            LocalDateTime current = LocalDateTime.now();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

            String formatted = current.format(formatter);
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setCreatedAt(formatted);
            user.setUpdatedAt(formatted);
            userRepository.save(user);
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.setMessage("Create user successfully");
            messageResponse.setStatus("OK");
            return ResponseEntity.ok(messageResponse);
        } catch (Exception e) {

            return ResponseEntity.status(500).body(
                    new ErrorMessage("An error occurred during create user")
            );
        }
    }



    @Override
    public ResponseEntity<?> signUp(SignUp signUp) {
        try {
            String name = signUp.getName();
            String email = signUp.getEmail();
            String password = signUp.getPassword();
            String confirmPassword = signUp.getConfirmPassword();
            UserAuth userAuth =UserAuth.USER;

            Optional<User> checkUser=userRepository.findByEmail(email);

            Pattern pattern = Pattern.compile("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
            Matcher matcher = pattern.matcher(email);
            boolean isCheckEmail = matcher.matches();
            MessageResponse errorResponse = new MessageResponse();
            if (name == null || email == null || password == null || userAuth==null) {
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
            else if (!password.equals(confirmPassword)){
                errorResponse.setMessage("confirm Password Not Correct");
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
            user.setUserAuth(userAuth);
            userRepository.save(user);
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.setMessage("User signed up successfully");
            messageResponse.setStatus("OK");
            return ResponseEntity.ok(messageResponse);
        } catch (Exception e) {

            return ResponseEntity.status(500).body(
                    new ErrorMessage( "An error occurred during sign up")
            );
        }
    }


    @Override
    public ResponseEntity<?> signIn(SignIn signIn,HttpServletResponse response) {
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

            CookieManager.addTokenCookie(response, token.getToken());

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
            if(updateUserRequest.getPassword()!=null){
                updateUserRequest.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
            }
            User existingUser = checkUser.get();
            modelMapper.getConfiguration().setSkipNullEnabled(true);
            modelMapper.map(updateUserRequest, existingUser);
            userRepository.save(existingUser);
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
    public ResponseEntity<?> logoutUser(HttpServletResponse response) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOptional = userRepository.findByEmail(authentication.getName());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String userId = user.getId();
            refreshTokenService.deleteByUserId(userId);
            CookieManager.deleteCookie(response);
            MessageResponse successResponse = new MessageResponse();
            successResponse.setMessage("LOG OUT SUCCESSFULLY");
            successResponse.setStatus("OK");
            return ResponseEntity.ok(successResponse);
        } else {
            MessageResponse errorResponse = new MessageResponse();
            errorResponse.setMessage("User not found");
            errorResponse.setStatus("ERR");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> deleteMany(IdsRequest ids) throws Exception {
        try {
            List<String> idList = ids.getIds();
            for (String id : idList) {
                if (!userRepository.existsById(id)) {
                    MessageResponse errorResponse = new MessageResponse();
                    errorResponse.setMessage("Invalid id " );
                    errorResponse.setStatus("ERR");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                }
            }

            userRepository.deleteAllById(idList);
            MessageResponse successResponse = new MessageResponse();
            successResponse.setMessage("Deleted many users successfully");
            successResponse.setStatus("OK");
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage("An error occurred during deleting many users"));
        }
    }




}
