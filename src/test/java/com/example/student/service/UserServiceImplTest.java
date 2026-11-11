package com.example.student.service;

import com.example.student.mapper.UserMapperImpl;
import com.example.student.repository.UserRepository;
import com.example.student.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder encoder;

    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl(repository, new UserMapperImpl(), encoder);
    }

    @Test
    @DisplayName("Проверка, занят ли email")
    void existsByEmail() {
        when(repository.existsByEmail("a@mail.ru")).thenReturn(true);
        when(repository.existsByEmail("b@mail.ru")).thenReturn(false);

        assertTrue(service.existsByEmail("a@mail.ru"));
        assertFalse(service.existsByEmail("b@mail.ru"));
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteById() {
        service.deleteById(3L);

        verify(repository).deleteById(3L);
    }
}
