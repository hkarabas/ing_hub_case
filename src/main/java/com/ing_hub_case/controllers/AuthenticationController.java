package com.ing_hub_case.controllers;



import com.ing_hub_case.entities.User;
import com.ing_hub_case.models.LoginResponse;
import com.ing_hub_case.models.UserDto;
import com.ing_hub_case.services.JwtService;
import com.ing_hub_case.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    private final JwtService jwtService;
    private final UserService userService;

    public AuthenticationController(JwtService jwtService, UserService authenticationService) {
        this.jwtService = jwtService;
        this.userService = authenticationService;
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<UserDto>  signupUser(@Valid @RequestBody UserDto userDto) {
      var signUser =  userService.signUp(userDto);
      userService.saveUserLogs(signUser);
      return new ResponseEntity<UserDto>(signUser,HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginResponseResponse(@RequestBody UserDto userDto) {
        User authenticatedUser = userService.login(userDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken,jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

}
