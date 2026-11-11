package com.example.student.service;

import com.example.student.dto.StudentFullResponse;
import com.example.student.dto.StudentRequest;
import com.example.student.dto.StudentResponse;
import com.example.student.dto.StudentUpdateRequest;
import com.example.student.exception.EntityNotFoundException;
import com.example.student.mapper.StudentMapperImpl;
import com.example.student.model.Student;
import com.example.student.repository.StudentRepository;
import com.example.student.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository repository;

    private StudentServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new StudentServiceImpl(repository, new StudentMapperImpl());
    }

    private Student student(long id) {
        Student s = new Student();
        s.setId(id);
        s.setName("Ivan");
        s.setSurname("Petrov");
        s.setPhoneNumber("+7000");
        s.setEmail("old@mail.ru");
        return s;
    }

    @Test
    @DisplayName("Сохранение студента")
    void save() {
        when(repository.save(any(Student.class))).thenAnswer(inv -> {
            Student s = inv.getArgument(0);
            s.setId(10L);
            return s;
        });

        StudentResponse response = service.save(new StudentRequest("Ivan", "Petrov", "+7000", "ivan@mail.ru"));

        assertEquals(10L, response.getId());
        assertEquals("Ivan", response.getName());
        verify(repository).save(any(Student.class));
    }

    @Test
    @DisplayName("Поиск по id")
    void findById() {
        when(repository.findById(1L)).thenReturn(Optional.of(student(1)));

        StudentFullResponse response = service.findById(1L);

        assertEquals("Petrov", response.getSurname());
        assertEquals("old@mail.ru", response.getEmail());
    }

    @Test
    @DisplayName("Студент не найден — исключение")
    void findByIdNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> service.findById(99L));
        assertEquals("Student with ID 99 not found!", e.getMessage());
    }

    @Test
    @DisplayName("Список всех студентов")
    void findAll() {
        when(repository.findAll()).thenReturn(List.of(student(1), student(2)));

        assertEquals(2, service.findAll().getTotalCount());
    }

    @Test
    @DisplayName("Обновление телефона и email")
    void updateBothFields() {
        Student existing = student(1);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        service.update(1L, new StudentUpdateRequest("+7999", "new@mail.ru"));

        assertEquals("+7999", existing.getPhoneNumber());
        assertEquals("new@mail.ru", existing.getEmail());
    }

    @Test
    @DisplayName("Пустые поля при обновлении не меняются")
    void updateWithNulls() {
        Student existing = student(1);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        service.update(1L, new StudentUpdateRequest(null, null));

        assertEquals("+7000", existing.getPhoneNumber());
        assertEquals("old@mail.ru", existing.getEmail());
    }

    @Test
    @DisplayName("Удаление по id")
    void deleteById() {
        service.deleteById(5L);

        verify(repository).deleteById(5L);
    }
}
