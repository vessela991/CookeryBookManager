package com.example.demo.model.user;

import com.example.demo.model.user.request.UserRegisterModel;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @NonNull
    @Column(nullable = false, unique=true)
    @Email
    private String email;

    @NonNull
    @Column(nullable = false, unique=true)
    @Size(min = 4, max = 16)
    private String username;

    @NonNull
    @Size(min = 8, max = 30)
    private String password;

    @NonNull
    private Gender gender;

    @NonNull
    private Role userRole = Role.USER;

    @NonNull
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Column(length = 10485760)
    private String picture;

    @ElementCollection(targetClass = Long.class)
    private List<Long> cookeryBookIds;
    private Long refrigeratorId;

    public static User fromUserRegisterModel(UserRegisterModel userRegisterModel) {
        User user = new User();
        user.setEmail(userRegisterModel.getEmail());
        user.setUsername(userRegisterModel.getUsername());
        user.setPassword(userRegisterModel.getPassword());
        user.setGender(userRegisterModel.getGender());
        user.setUserRole(userRegisterModel.getUserRole());
        return user;
    }
}
