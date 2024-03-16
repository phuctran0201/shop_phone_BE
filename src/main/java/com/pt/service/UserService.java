package com.pt.service;

import com.pt.DTO.ViewUserDTO;

import com.pt.req.SignIn;
import com.pt.req.SignUp;
import com.pt.req.CreateUserRequest;
import com.pt.req.UpdateUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {

    public List<ViewUserDTO> listUserData() throws Exception;

    public void createUser(CreateUserRequest userRequest) throws Exception;

    public ResponseEntity<?> signUp(SignUp signUp) throws Exception;

    public ResponseEntity<?> signIn(SignIn signIn) throws Exception;

    public ResponseEntity<?> updateUser(UpdateUserRequest updateUserRequest) throws Exception;

    public ResponseEntity<?> deleteUser(String id) throws Exception;

    public ResponseEntity<?> userDetail(String id) throws Exception;

    public ResponseEntity<?> logoutUser() throws Exception;
}
