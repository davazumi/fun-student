package com.example.student.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Полный запуск приложения с базой H2: вход по JWT и запросы к /api/v1/student.
 * Пользователи и студенты создаются при старте классом AppInitializer.
 */
@SpringBootTest
class StudentControllerIntegrationTest {

    private static final String URL = "/api/v1/student";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    private String login(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andReturn();
        Matcher m = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"")
                .matcher(result.getResponse().getContentAsString());
        m.find();
        return "Bearer " + m.group(1);
    }

    @Test
    @DisplayName("Админ входит и получает список студентов")
    void adminGetsAllStudents() throws Exception {
        String token = login("admin@example.com", "admin");

        mockMvc.perform(get(URL).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").isNumber());
    }

    @Test
    @DisplayName("Учитель видит студента по id")
    void teacherGetsStudentById() throws Exception {
        String token = login("teacher@example.com", "teacher");

        mockMvc.perform(get(URL + "/1").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Админ создаёт студента")
    void adminCreatesStudent() throws Exception {
        String token = login("admin@example.com", "admin");

        mockMvc.perform(post(URL + "/save").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Anna\",\"surname\":\"Orlova\",\"phone_number\":\"+7111\",\"email\":\"anna@mail.ru\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Anna"));
    }

    @Test
    @DisplayName("Несуществующий студент — 404")
    void studentNotFound() throws Exception {
        String token = login("admin@example.com", "admin");

        mockMvc.perform(get(URL + "/9999").header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("Без токена доступ запрещён")
    void noToken() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(result -> {
                    int code = result.getResponse().getStatus();
                    if (code != 401 && code != 403) {
                        throw new AssertionError("Ожидался 401 или 403, получен " + code);
                    }
                });
    }

    @Test
    @DisplayName("Испорченный токен — 401")
    void brokenToken() throws Exception {
        mockMvc.perform(get(URL).header("Authorization", "Bearer broken.token.value"))
                .andExpect(status().isUnauthorized());
    }
}
