package com.example.demo.internal;

import com.example.demo.model.cookeryBook.request.BookCreateModel;
import com.example.demo.model.recipe.request.RecipeCreateModel;
import com.example.demo.model.user.Gender;
import com.example.demo.model.user.User;
import com.example.demo.model.user.request.UserChangePasswordModel;
import com.example.demo.model.user.request.UserEditModel;
import com.example.demo.respository.UserRepository;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.model.ValidationResult;
import com.example.demo.model.user.request.UserRegisterModel;

@Component
public class Validator {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String USER_DOES_NOT_EXIST_ERROR = "User with such id does not exist.";
    private static final String USERNAME_IS_TAKEN_ERROR = "Username is already taken!";
    private static final String USERNAME_LENGTH_ERROR = "Username should be between 4 and 16 characters!";
    private static final String EMAIL_IS_TAKEN_ERROR = "Email is already taken!";
    private static final String EMAIL_LENGTH_ERROR = "Email should be between 4 and 32 characters and contain @!";
    private static final String GENDER_IS_REQUIRED_ERROR = "Gender must be filled!";
    private static final String PASSWORD_IS_INVALID_ERROR = "Password length must be over 8 characters, " +
            "must contain at least one digit and at least one special character";
    private static final String PASSWORDS_SHOULD_NOT_MATCH_ERROR = "New password can not be the same as the old password!";
    private static final String WRONG_OLD_PASSWORD_ERROR = "Wrong old password!";
    private static final String RECIPE_NAME_LENGTH_ERROR = "Recipe name must be between 4 and 80 characters!";
    private static final String RECIPE_TIME_NOT_SET_ERROR = "You must add time to cook for this recipe!";
    private static final String RECIPE_SHORT_DESC_ERROR = "Recipe short description must be between 4 and 256 characters!";
    private static final String RECIPE_LONG_DESC_ERROR = "Recipe long description must be between 4 and 2048 characters!";
    private static final String RECIPE_PRODUCTS_ERROR = "You must add products to this recipe!";
    private static final String RECIPE_TAGS_ERROR = "You must add tags to this recipe!";
    private static final String RECIPE_NO_PICTURE_ERROR = "Recipe must have picture!";
    private static final String RECIPE_NO_BOOK_ERROR = "Recipe must have cookery book assigned to it!";
    private static final String RECIPE_NO_ID_ERROR = "There is no such recipe!";

    private static final Integer STRING_MIN_LENGTH = 4;
    private static final Integer RECIPE_NAME_MAX_LENGTH = 80;
    private static final Integer RECIPE_SHORT_DESC_MAX_LENGTH = 256;
    private static final Integer RECIPE_LONG_DESC_MAX_LENGTH = 2048;
    private static final Integer USERNAME_MAX_LENGTH = 16;
    private static final Integer EMAIL_MAX_LENGTH = 32;
    private static final Integer PASSWORD_MIN_LENGTH = 8;
    private static final Integer PASSWORD_MAX_LENGTH = 42;
    private static final Integer PASSWORD_NUMBER_SPECIAL_CHARACTERS = 1;

    private static final PasswordValidator PASSWORD_VALIDATOR = new PasswordValidator(
            new LengthRule(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH),
            new CharacterRule(EnglishCharacterData.Digit, PASSWORD_NUMBER_SPECIAL_CHARACTERS),
            new CharacterRule(EnglishCharacterData.Special, PASSWORD_NUMBER_SPECIAL_CHARACTERS)
    );

    public ValidationResult<String> validateUserRegistration(UserRegisterModel userRegisterModel) {
        if (userRepository.findByUsername(userRegisterModel.getUsername()) != null) {
            return new ValidationResult<>(false, USERNAME_IS_TAKEN_ERROR);
        }

        if (userRepository.findByEmail(userRegisterModel.getEmail()) != null) {
            return new ValidationResult<>(false, EMAIL_IS_TAKEN_ERROR);
        }

        if (!(userRegisterModel.getUsername().length() > STRING_MIN_LENGTH && userRegisterModel.getUsername().length() < USERNAME_MAX_LENGTH)) {
            return new ValidationResult<>(false, USERNAME_LENGTH_ERROR);
        }

        if (!(userRegisterModel.getEmail().length() > STRING_MIN_LENGTH && userRegisterModel.getEmail().length() < EMAIL_MAX_LENGTH)) {
            return new ValidationResult<>(false, EMAIL_LENGTH_ERROR);
        }

        if (!validatePassword(userRegisterModel.getPassword())) {
            return new ValidationResult<>(false, PASSWORD_IS_INVALID_ERROR);
        }

        if (!validateGender(userRegisterModel.getGender())) {
            return new ValidationResult<>(false, GENDER_IS_REQUIRED_ERROR);
        }

        return new ValidationResult<>(true);
    }

    public ValidationResult<String> validateEditUser(UserEditModel userEditModel, User user) {
        if (user == null) {
            return new ValidationResult<>(false, USER_DOES_NOT_EXIST_ERROR);
        }

        if (!validateGender(userEditModel.getGender())) {
            return new ValidationResult<>(false, GENDER_IS_REQUIRED_ERROR);
        }

        return new ValidationResult<>(true);
    }

    private boolean validateGender(Gender gender) {
        return gender != null;
    }

    private boolean validatePassword(String password) {
        return PASSWORD_VALIDATOR.validate(new PasswordData(password)).isValid();
    }

    public ValidationResult<String> validatePasswordChange(UserChangePasswordModel userChangePasswordModel, User loggedUser) {
        if (!bCryptPasswordEncoder.matches(userChangePasswordModel.getOldPassword(), loggedUser.getPassword())) {
            return new ValidationResult<>(false, WRONG_OLD_PASSWORD_ERROR);
        }

        if (userChangePasswordModel.getOldPassword().equals(userChangePasswordModel.getNewPassword())) {
            return new ValidationResult<>(false, PASSWORDS_SHOULD_NOT_MATCH_ERROR);
        }

        if (!validatePassword(userChangePasswordModel.getNewPassword())) {
            return new ValidationResult<>(false, PASSWORD_IS_INVALID_ERROR);
        }

        return new ValidationResult<>(true);
    }

    public ValidationResult<String> validateRecipeCreation(RecipeCreateModel recipe) {
        if (recipe.getCookeryBookName() == null || recipe.getCookeryBookName().trim().isEmpty()) {
            return new ValidationResult<>(false, RECIPE_NO_BOOK_ERROR);
        }

        if (!HelperMethods.isPictureUploaded(recipe.getPicture())) {
            return new ValidationResult<>(false, RECIPE_NO_PICTURE_ERROR);
        }

        return validateRecipe(recipe);
    }

    public ValidationResult<String> validateRecipe(RecipeCreateModel recipe) {
        if (!(recipe.getName().length() > STRING_MIN_LENGTH && recipe.getName().length() < RECIPE_NAME_MAX_LENGTH)) {
            return new ValidationResult<>(false, RECIPE_NAME_LENGTH_ERROR);
        }

        if (recipe.getTimeToCook() <= 0) {
            return new ValidationResult<>(false, RECIPE_TIME_NOT_SET_ERROR);
        }

        if (!(recipe.getShortDescription().length() > STRING_MIN_LENGTH && recipe.getShortDescription().length() < RECIPE_SHORT_DESC_MAX_LENGTH)) {
            return new ValidationResult<>(false, RECIPE_SHORT_DESC_ERROR);
        }

        if (!(recipe.getLongDescription().length() > STRING_MIN_LENGTH && recipe.getLongDescription().length() < RECIPE_LONG_DESC_MAX_LENGTH)) {
            return new ValidationResult<>(false, RECIPE_LONG_DESC_ERROR);
        }

        if (recipe.getProducts().trim().isEmpty()) {
            return new ValidationResult<>(false, RECIPE_PRODUCTS_ERROR);
        }

        if (recipe.getTags().trim().isEmpty()) {
            return new ValidationResult<>(false, RECIPE_TAGS_ERROR);
        }

        return new ValidationResult<>(true);
    }

    public ValidationResult<String> validateBook(BookCreateModel book) {
        if (book.getName() == null || book.getName().isEmpty()) {
            return new ValidationResult<>(false, "Please name your cookery book!");
        }

        return new ValidationResult<>(true);
    }
}