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
public class UserEditModel {
    private Long id;
    private Gender gender;
    private Role userRole = Role.USER;
    private MultipartFile picture;
}
