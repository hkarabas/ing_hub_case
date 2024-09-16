package com.ing_hub_case.controllers;

import com.google.gson.Gson;
import com.ing_hub_case.entities.User;
import com.ing_hub_case.models.UserDto;
import com.ing_hub_case.repositories.UserRepository;
import com.ing_hub_case.services.JwtService;
import com.ing_hub_case.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    UserService userService;

    @MockBean
    JwtService jwtService;

    private UserDto registerUserDto;

    private User user;

    private UserDto loginUserDto;

    private  String jwtToken;


    @BeforeEach
    void Init() {
        registerUserDto = new UserDto();
        registerUserDto.setName("hasan karabaş");
        registerUserDto.setEmail("hkarabas@gmail.com");
        registerUserDto.setPassword("qweqeqweqwewqas45345345345");

        user = new User();
        user.setEmail("hkarabas@gmail.com");
        user.setPassword("qweqeqweqwewqas45345345345");
        user.setFullName("hasan karabaş");

        loginUserDto = new UserDto();
        loginUserDto.setEmail("hkarabas@gmail.com");
        loginUserDto.setPassword("qweqeqweqwewqas45345345345");

        jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoa2FyYWJhc0BnbWFpbC5jb20iLCJpYXQiOjE3MjUwNDI1NjgsImV4cCI6MTcyNTA0NjE" +
                "2OH0.LCUnEmWE6jnuq0bMROgKZU_DAxPnD-zrXtKyB5ea3D8";
    }

    @Test
    void testUserSignUp() throws Exception {

        when(userService.signUp(any())).thenReturn(user.convertToDto());
        String json = new Gson().toJson(registerUserDto);
        mockMvc.perform( MockMvcRequestBuilders
                        .post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated())
               .andExpect(content().string(containsString("qweqeqweqwewqas45345345345")));
    }

    @Test
    void testUserLogin() throws Exception {
        when(userService.login(any())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(jwtToken);
        String json = new Gson().toJson(loginUserDto);
        mockMvc.perform( MockMvcRequestBuilders
                .post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(containsString(jwtToken)));
    }

}