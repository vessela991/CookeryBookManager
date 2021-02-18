package com.example.demo.controller;

import com.example.demo.internal.Constants;
import com.example.demo.internal.HelperMethods;
import com.example.demo.model.user.User;
import com.example.demo.model.user.request.UserChangePasswordModel;
import com.example.demo.model.user.request.UserEditModel;
import com.example.demo.model.user.request.UserLoginModel;
import com.example.demo.model.user.request.UserRegisterModel;
import com.example.demo.respository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller()
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HelperMethods helperMethods;

    // login
    @GetMapping("/login")
    public String getLogin(@ModelAttribute("user") UserLoginModel requestModel, Model model) {
        model.addAttribute(Constants.TITLE, "Login");
        return Constants.TMPL_LOGIN;
    }

    @PostMapping("/login")
    public String failedLogin(@ModelAttribute("user") UserLoginModel requestModel, Model model) {
        // we come here when login fails
        model.addAttribute(Constants.TITLE, "Login");
        model.addAttribute(Constants.ERROR, "Failed Login");
        return Constants.TMPL_LOGIN;
    }

    // Register
    @GetMapping("/register")
    public String getRegister(@ModelAttribute("user") UserRegisterModel requestModel, Model model) {
        model.addAttribute(Constants.TITLE, "Register");
        return Constants.TMPL_REGISTER;
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserRegisterModel requestModel, Model model) {
        model.addAttribute(Constants.TITLE, "Register");
        return userService.register(requestModel, model);
    }

    // register user for admin, created so register can be allowed only for anonymous users
    @GetMapping("/users/create")
    public String getCreateUser(@ModelAttribute("user") UserRegisterModel requestModel, Model model) {
        model.addAttribute(Constants.TITLE, "Create User");
        return Constants.TMPL_REGISTER;
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute("user") UserRegisterModel requestModel, Model model) {
        model.addAttribute(Constants.TITLE, "Create User");
        return userService.register(requestModel, model);
    }

    // Change password
    @GetMapping("/changepassword")
    public String getChangePassword(@ModelAttribute("user") UserChangePasswordModel requestModel, Model model) {
        model.addAttribute(Constants.TITLE, "Change password");
        return Constants.TMPL_CHANGEPASSWORD;
    }

    @PostMapping("/changepassword")
    public String changePassword(@ModelAttribute("user") UserChangePasswordModel requestModel, Model model) {
        return userService.changePassword(requestModel, model);
    }

    // Users
    @GetMapping("/users")
    public String getUsers(Model model) {
        model.addAttribute(Constants.TITLE, "Users");
        model.addAttribute(Constants.USERS,helperMethods.filterCollection(userRepository.findAll(),
                user -> !user.getId().equals(HelperMethods.getLoggedInUserId(model))));
        return Constants.TMPL_USERS;
    }

    @GetMapping("/users/{id}")
    public String getEditUser(@PathVariable("id") Long userId, Model model) {
        model.addAttribute(Constants.TITLE, "Edit");
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Constants.REDIRECT + Constants.USERS;
        }
        model.addAttribute(Constants.USER, user);
        return Constants.TMPL_USER_EDIT;
    }

    @PostMapping("/users/edit")
    public String editUser(@ModelAttribute("user") UserEditModel requestModel, Model model) {
        return userService.editUser(requestModel, model);
    }

    @PostMapping(value = "/users/delete", params = "id")
    public String deleteUser(@RequestParam("id") Long userId) {
        try {
            // if id is not found in DB, exception is thrown, ignore it
            userRepository.deleteById(userId);
        } catch (Exception ignored) { }
        return Constants.REDIRECT + Constants.USERS;
    }
}
