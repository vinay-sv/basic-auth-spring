package com.example.demo.student;

import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("management/api/v1/students")
public class StudentManagementController {
    private static final List<Student> STUDENTS = Arrays.asList(
            new Student(1, "student1"),
            new Student(2, "student2"),
            new Student(3, "student3")
    );

    @GetMapping()
    public List<Student> getAllStudents() {
        return STUDENTS;
    }

    @PostMapping
    public void registerStudent(@RequestBody Student student) {
        System.out.println("register student");
    }

    @DeleteMapping(path = "{studentId}")
    public void deleteStudent(@PathVariable("studentId") Integer studentId) {
        System.out.println("delete student " + studentId);
    }

    @PutMapping(path = "{studentId}")
    public void updateStudent(@PathVariable("studentId") Integer studentId, @RequestBody Student student) {
        System.out.println("update student" + studentId);
    }

}
