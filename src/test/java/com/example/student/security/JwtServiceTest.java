package com.example.student.security;

import com.example.student.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    private UserDetails user(String email) {
        return User.withUsername(email).password("x").roles("ADMIN").build();
    }

    @Test
    @DisplayName("Из токена достаётся логин")
    void extractUsername() {
        String token = jwtService.generateToken("admin@example.com", Set.of(Role.ADMIN));

        assertEquals("admin@example.com", jwtService.extractUsername(token));
    }

    @Test
    @DisplayName("Из токена достаются роли")
    void extractRoles() {
        String token = jwtService.generateToken("a@mail.ru", Set.of(Role.ADMIN, Role.TEACHER));

        assertEquals(Set.of(Role.ADMIN, Role.TEACHER), jwtService.extractRoles(token));
    }

    @Test
    @DisplayName("Токен действителен для своего пользователя")
    void validTokenForSameUser() {
        String token = jwtService.generateToken("a@mail.ru", Set.of(Role.ADMIN));

        assertTrue(jwtService.validateToken(token, user("a@mail.ru")));
    }

    @Test
    @DisplayName("Токен недействителен для другого пользователя")
    void invalidTokenForOtherUser() {
        String token = jwtService.generateToken("a@mail.ru", Set.of(Role.ADMIN));

        assertFalse(jwtService.validateToken(token, user("b@mail.ru")));
    }

    @Test
    @DisplayName("Испорченный токен не разбирается")
    void brokenToken() {
        assertThrows(Exception.class, () -> jwtService.extractUsername("not.a.token"));
    }
}
