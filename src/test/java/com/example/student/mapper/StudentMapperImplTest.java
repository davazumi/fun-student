package com.example.student.mapper;

import com.example.student.dto.StudentFullResponse;
import com.example.student.dto.StudentRequest;
import com.example.student.dto.StudentResponse;
import com.example.student.dto.StudentResponseList;
import com.example.student.model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StudentMapperImplTest {

    private final StudentMapper mapper = new StudentMapperImpl();

    private Student student(long id, String name, String surname) {
        Student s = new Student();
        s.setId(id);
        s.setName(name);
        s.setSurname(surname);
        s.setPhoneNumber("+79990000000");
        s.setEmail(name.toLowerCase() + "@mail.ru");
        return s;
    }

    @Test
    @DisplayName("Запрос превращается в студента")
    void requestToStudent() {
        StudentRequest request = new StudentRequest("Ivan", "Petrov", "+79991112233", "ivan@mail.ru");

        Student student = mapper.requestToStudent(request);

        assertNull(student.getId());
        assertEquals("Ivan", student.getName());
        assertEquals("Petrov", student.getSurname());
        assertEquals("+79991112233", student.getPhoneNumber());
        assertEquals("ivan@mail.ru", student.getEmail());
    }

    @Test
    @DisplayName("Студент превращается в полный ответ")
    void studentToFullResponse() {
        StudentFullResponse response = mapper.studentToFullResponse(student(1, "Ivan", "Petrov"));

        assertEquals(1, response.getId());
        assertEquals("Ivan", response.getName());
        assertEquals("+79990000000", response.getPhoneNumber());
        assertEquals("ivan@mail.ru", response.getEmail());
    }

    @Test
    @DisplayName("Студент превращается в краткий ответ")
    void studentToResponse() {
        StudentResponse response = mapper.studentToResponse(student(2, "Petr", "Semenov"));

        assertEquals(2, response.getId());
        assertEquals("Petr", response.getName());
        assertEquals("Semenov", response.getSurname());
    }

    @Test
    @DisplayName("Список студентов превращается в ответ со счётчиком")
    void studentListToStudentResponseList() {
        List<Student> students = List.of(student(1, "Ivan", "Petrov"), student(2, "Petr", "Semenov"));

        StudentResponseList list = mapper.studentListToStudentResponseList(students);

        assertEquals(2, list.getTotalCount());
        assertEquals("Petr", list.getResponse().get(1).getName());
    }

    @Test
    @DisplayName("Пустой список")
    void emptyList() {
        StudentResponseList list = mapper.studentListToStudentResponseList(List.of());

        assertEquals(0, list.getTotalCount());
        assertEquals(0, list.getResponse().size());
    }
}
