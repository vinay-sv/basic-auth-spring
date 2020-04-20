package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/")
public class TemplateController {

    @GetMapping("login")
    public String getLoginView() {
        // This redirects to the "login.html" file mentioned in templates folder. This is carried out by the thymeleaf dependency in the pom.xml file
        return "login";
    }

    @GetMapping("courses")
    public String getCourses() {
        return "courses";
    }
}
