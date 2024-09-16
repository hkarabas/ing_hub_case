package com.ing_hub_case.services;

import com.google.gson.Gson;
import com.ing_hub_case.entities.User;
import com.ing_hub_case.entities.User_Log;
import com.ing_hub_case.exception.NoSuchCustomerExistsException;
import com.ing_hub_case.models.UserDto;
import com.ing_hub_case.repositories.UserLogRepository;
import com.ing_hub_case.repositories.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserLogRepository userLogRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, UserLogRepository userLogRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userLogRepository = userLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public List<com.ing_hub_case.entities.User> getUserList(String userType) {
        List<com.ing_hub_case.entities.User> list = new ArrayList<>();
        userRepository.findByUserType(userType).forEach(list::add);
        return  list;
    }


    public UserDto signUp(UserDto userDto) {
        User user = new User();
        user.setFullName(userDto.getName()+" "+userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setUserType(userDto.getUserType().toString());
        user.setDefaultCurrency(userDto.getDefaultCurrency().toString());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setIban(userDto.getIban());
        return   userRepository.save(user).convertToDto();
    }

    public User login(UserDto userDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getEmail(),userDto.getPassword())
        );
        return  userRepository.findByEmail(userDto.getEmail()).orElseThrow(() -> new  NoSuchCustomerExistsException("Customer Not Found"));
    }

    @Async
    public void saveUserLogs(UserDto user){
        Gson gson = new Gson();
        User_Log userLog = new User_Log();
        userLog.setCreated_date(Timestamp.valueOf(LocalDateTime.now()));
        userLog.setUser_id(user.getId());
        userLog.setJson(gson.toJson(user));
        userLogRepository.save(userLog);
    }


}
