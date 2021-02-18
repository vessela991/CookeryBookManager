package com.example.demo.util;

import com.example.demo.model.cookeryBook.CookeryBook;
import com.example.demo.model.recipe.Recipe;
import com.example.demo.model.user.Gender;
import com.example.demo.model.user.Role;
import com.example.demo.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class CommonUtils {
    public Boolean isRoleUser(String role) {
        return Role.USER.toString().equals(role);
    }

    public Boolean isRoleAdmin(String role) {
        return Role.ADMIN.toString().equals(role);
    }

    public Boolean isGenderMale(String gender) {
        return Gender.MALE.toString().equals(gender);
    }

    public Boolean isGenderFemale(String gender) {
        return Gender.FEMALE.toString().equals(gender);
    }

    public Boolean isGenderOther(String gender) {
        return Gender.OTHER.toString().equals(gender);
    }

    public Boolean isUserAdmin(User user) {
        return user != null && Role.ADMIN.equals(user.getUserRole());
    }

    public Boolean isUserLoggedIn(User user) {
        return user == null;
    }

    public Boolean isOwnerOrAdmin(User user, Recipe recipe) {
        return user != null && (Role.ADMIN.equals(user.getUserRole()) || recipe.getUserId().equals(user.getId()));
    }

    public Boolean isOwnerOrAdmin(User user, CookeryBook cookeryBook) {
        return user != null && (Role.ADMIN.equals(user.getUserRole()) || cookeryBook.getUserId().equals(user.getId()));
    }

    public int isBookPublic(CookeryBook cookeryBook) {
        return cookeryBook.isPublic() ? 1 : 0;
    }

    public String removeBrackets(String str) {
        return str == null ? "" : str.replace("[", "").replace("]", "");
    }
}
