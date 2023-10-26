package com.cydeo.controller;

import com.cydeo.dto.UserDTO;
import com.cydeo.entity.ResponseWrapper;
import com.cydeo.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> getUsers(){
        List<UserDTO> userDTOList = userService.listAllUsers();
        //I was giving this list of userDTOs to html. Now I'm gonna give it to ResponseEntity (http).
        return ResponseEntity.ok(new ResponseWrapper("Users are successfully retrieved", userDTOList, HttpStatus.OK));
        //.ok(body): ok means 200. body object is new ResponseWrapper(String message, Object data, HttpStatus httpStatus)
        //first .ok is in Postman status, second .OK is in the response body.
    }

    @GetMapping("/{username}")
    @RolesAllowed("Admin")
    public ResponseEntity<ResponseWrapper> getUserByName(@PathVariable("username") String username){
        //@PathVariable = "catch from the uri
        UserDTO user = userService.findByUserName(username);
        return ResponseEntity.ok(new ResponseWrapper("User is successfully retrieved",user,HttpStatus.OK));
    }

    @PostMapping
    @RolesAllowed("Admin")
    public ResponseEntity<ResponseWrapper> createUser(@RequestBody UserDTO user){
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper("User is created successfully",HttpStatus.CREATED));
    }

    @PutMapping
    @RolesAllowed("Admin")
    public ResponseEntity<ResponseWrapper> updateUser(@RequestBody UserDTO user){
        userService.update(user);
        return ResponseEntity.ok(new ResponseWrapper("User is successfully updated",user,HttpStatus.OK));
    }

    @DeleteMapping("/{userName}")
    @RolesAllowed("Admin")
    public ResponseEntity<ResponseWrapper> deleteUser(@PathVariable String userName){
        userService.deleteByUserName(userName);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT)
//                .body(new ResponseWrapper("User is successfully created",HttpStatus.NO_CONTENT)); //--> 204. this doesn't show the body.
        return ResponseEntity.ok(new ResponseWrapper("User is successfully deleted",HttpStatus.OK));
    }



}
