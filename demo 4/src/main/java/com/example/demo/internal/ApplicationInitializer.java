package com.example.demo.internal;

import com.example.demo.model.cookeryBook.CookeryBook;
import com.example.demo.model.recipe.Recipe;
import com.example.demo.model.user.Gender;
import com.example.demo.model.user.Role;
import com.example.demo.model.user.User;
import com.example.demo.respository.CookeryBookRepository;
import com.example.demo.respository.RecipeRepository;
import com.example.demo.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Component
public class ApplicationInitializer implements ApplicationRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private CookeryBookRepository cookeryBookRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByUsername("admin") == null) {
            User admin = createNewUser("admin@admin.com", "admin", "admin1234!", Role.ADMIN);
            User admin2 = createNewUser("admin2@admin.com", "admin2", "admin1234!", Role.ADMIN);
            User user = createNewUser("user@user.com", "user", "user1234!", Role.USER);
            User user2 = createNewUser("user2@user.com", "user2", "user1234!", Role.USER);
            var cb1 = createNewCookeryBook("Kniga 1", admin.getUsername(), null,admin.getId(),false);
            var cb2 = createNewCookeryBook("Kniga 2", user.getUsername(), null,user.getId(),false);
            var cb3 = createNewCookeryBook("Kniga 3", user2.getUsername(), null,user2.getId(),true);
            var cb4 = createNewCookeryBook("Kniga 4", admin2.getUsername(), null,admin2.getId(),true);

            List<Recipe> cb1Recipes = new ArrayList<>();
            cb1Recipes.add(createNewRecipe(1, admin.getId(), cb1));
            cb1Recipes.add(createNewRecipe(2, admin.getId(), cb1));
            cb1Recipes.add(createNewRecipe(3, admin.getId(), cb1));
            cb1Recipes.add(createNewRecipe(4, admin.getId(), cb1));
            cb1Recipes.add(createNewRecipe(5, admin.getId(), cb1));

            List<Recipe> cb2Recipes = new ArrayList<>();
            cb2Recipes.add(createNewRecipe(11, user.getId(), cb2));
            cb2Recipes.add(createNewRecipe(22, user.getId(), cb2));
            cb2Recipes.add(createNewRecipe(33, user.getId(), cb2));
            cb2Recipes.add(createNewRecipe(44, user.getId(), cb2));
            cb2Recipes.add(createNewRecipe(55, user.getId(), cb2));

            List<Recipe> cb3Recipes = new ArrayList<>();
            cb3Recipes.add(createNewRecipe(13, user2.getId(), cb3));
            cb3Recipes.add(createNewRecipe(23, user2.getId(), cb3));
            cb3Recipes.add(createNewRecipe(33, user2.getId(), cb3));
            cb3Recipes.add(createNewRecipe(43, user2.getId(), cb3));
            cb3Recipes.add(createNewRecipe(53, user2.getId(), cb3));

            List<Recipe> cb4Recipes = new ArrayList<>();
            cb4Recipes.add(createNewRecipe(14, admin2.getId(), cb4));
            cb4Recipes.add(createNewRecipe(24, admin2.getId(), cb4));
            cb4Recipes.add(createNewRecipe(34, admin2.getId(), cb4));
            cb4Recipes.add(createNewRecipe(44, admin2.getId(), cb4));
            cb4Recipes.add(createNewRecipe(54, admin2.getId(), cb4));

            List<Long> recipeIdsCb1 = new ArrayList<>();
            List<Long> recipeIdsCb2 = new ArrayList<>();
            List<Long> recipeIdsCb3 = new ArrayList<>();
            List<Long> recipeIdsCb4 = new ArrayList<>();

            for(int i = 0; i< cb1Recipes.size(); i++) {
                recipeIdsCb1.add(cb1Recipes.get(i).getId());
                recipeIdsCb2.add(cb2Recipes.get(i).getId());
                recipeIdsCb3.add(cb3Recipes.get(i).getId());
                recipeIdsCb4.add(cb4Recipes.get(i).getId());
            }
            cb1.setRecipeIds(recipeIdsCb1);
            cb2.setRecipeIds(recipeIdsCb1);
            cb3.setRecipeIds(recipeIdsCb1);
            cb4.setRecipeIds(recipeIdsCb1);

            cookeryBookRepository.save(cb1);
            cookeryBookRepository.save(cb2);
            cookeryBookRepository.save(cb3);
            cookeryBookRepository.save(cb4);
        }
    }

    private Recipe createNewRecipe(Integer number, Long userId, CookeryBook cookeryBook) {
        Recipe recipe = new Recipe();
        recipe.setName("test"+number);
        recipe.setShortDescription("test short description"+number);
        recipe.setTimeToCook(10);
        recipe.setProducts(Arrays.asList("test products here".split(" ")));
        recipe.setLongDescription("test short description"+number);
        recipe.setTags(Arrays.asList("test short description".split(" ")));
        recipe.setPicture(getDefaultPicture("src/main/resources/default-recipe.jpg"));
        recipe.setUserId(userId);
        recipe.setCookeryBookId(cookeryBook.getId());
        recipe.setCookeryBookName(cookeryBook.getName());
        recipe.setCookeryBookId(cookeryBook.getId());
        recipe.setPublic(cookeryBook.isPublic());
        return recipeRepository.save(recipe);
    }

    private User createNewUser(String email, String username, String password, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder().encode(password));
        user.setGender(Gender.FEMALE);
        user.setUserRole(role);
        user.setPicture(getDefaultPicture("src/main/resources/default-avatar.jpg"));
        return userRepository.save(user);
    }

    private CookeryBook createNewCookeryBook(String name, String username, List<Long> recipeIds, Long userId, boolean isPublic) {
        CookeryBook cookeryBook = new CookeryBook();
        cookeryBook.setUsername(username);
        cookeryBook.setName(name);
        cookeryBook.setRecipeIds(recipeIds);
        cookeryBook.setUserId(userId);
        cookeryBook.setPublic(isPublic);
        return cookeryBookRepository.save(cookeryBook);
    }

    private static String getDefaultPicture(String path) {
        try {
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), MediaType.TEXT_HTML_VALUE, fileInputStream);
            Blob pictureBlob = new SerialBlob(multipartFile.getBytes());
            return Base64.getEncoder().encodeToString(pictureBlob.getBytes(1, (int) pictureBlob.length()));
        }
        catch (Exception ignored) {}
        return null;
    }
}
