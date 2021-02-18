package com.example.demo.controller;

import com.example.demo.internal.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String getHomePage(Model model) {
        model.addAttribute(Constants.TITLE, "Home");
        return Constants.HOME;
    }
}
