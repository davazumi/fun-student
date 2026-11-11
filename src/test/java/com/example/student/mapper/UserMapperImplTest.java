package com.example.student.mapper;

import com.example.student.dto.user.UserResponse;
import com.example.student.model.Role;
import com.example.student.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperImplTest {

    private final UserMapper mapper = new UserMapperImpl();

    @Test
    @DisplayName("Пользователь превращается в ответ")
    void toResponse() {
        User user = User.builder().id(3L).email("a@mail.ru").password("x").roles(Set.of(Role.ADMIN)).build();

        UserResponse response = mapper.toResponse(user);

        assertEquals(3L, response.getId());
        assertEquals("a@mail.ru", response.getEmail());
        assertEquals(Set.of(Role.ADMIN), response.getRoles());
    }
}
