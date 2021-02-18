package com.example.demo.model.user.request;

import com.example.demo.model.user.Gender;
import com.example.demo.model.user.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterModel {
    private String email;
    private String username;
    private String password;
    private Gender gender;
    private Role userRole = Role.USER;
    private MultipartFile picture;
}
