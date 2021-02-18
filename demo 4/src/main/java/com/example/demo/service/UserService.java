package com.example.demo.service;

import com.example.demo.internal.Constants;
import com.example.demo.internal.HelperMethods;
import com.example.demo.internal.Validator;
import com.example.demo.model.ValidationResult;
import com.example.demo.model.user.Role;
import com.example.demo.model.user.User;
import com.example.demo.model.user.request.UserChangePasswordModel;
import com.example.demo.model.user.request.UserEditModel;
import com.example.demo.model.user.request.UserRegisterModel;
import com.example.demo.respository.UserRepository;
import com.example.demo.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Validator validator;
    @Autowired
    private CommonUtils commonUtils;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // register
    public String register(UserRegisterModel requestModel, Model model) {
        ValidationResult<String> validation = validator.validateUserRegistration(requestModel);
        if (!validation.isSuccess()) {
            model.addAttribute(Constants.ERROR, validation.getResult());
            return Constants.TMPL_REGISTER;
        }

        User user = User.fromUserRegisterModel(requestModel);
        user.setPassword(encryptedPassword(user.getPassword()));

        if (!HelperMethods.isPictureUploaded(requestModel.getPicture())) {
            requestModel.setPicture(getDefaultPicture());
        }

        String picture = HelperMethods.getMultiPartFileAs64BaseString(requestModel.getPicture());
        if (picture == null) {
            model.addAttribute(Constants.ERROR, "System error, please contact your administrator");
            return Constants.TMPL_REGISTER;
        }

        user.setPicture(picture);

        userRepository.save(user);
        return Constants.REDIRECT + Constants.RECIPES;
    }

    //Change password
    public String changePassword(UserChangePasswordModel requestModel, Model model) {
        model.addAttribute(Constants.TITLE, "Change password");
        User loggedUser = userRepository.findById(HelperMethods.getLoggedInUserId(model)).orElse(null);

        ValidationResult<String> validation = validator.validatePasswordChange(requestModel, loggedUser);
        if (!validation.isSuccess()) {
            model.addAttribute(Constants.ERROR, validation.getResult());
            return Constants.TMPL_CHANGEPASSWORD;
        }

        loggedUser.setPassword(bCryptPasswordEncoder.encode(requestModel.getNewPassword()));
        userRepository.save(loggedUser);
        return Constants.REDIRECT + Constants.LOGOUT;
    }

    // users
    public String editUser(UserEditModel requestModel, Model model) {
        model.addAttribute(Constants.TITLE, "Edit");
        User user = userRepository.findById(requestModel.getId()).orElse(null);

        ValidationResult<String> validation = validator.validateEditUser(requestModel, user);
        if (!validation.isSuccess()) {
            model.addAttribute(Constants.ERROR, validation.getResult());
            return Constants.TMPL_USER_EDIT;
        }

        user.setUserRole(requestModel.getUserRole() == null ? Role.USER : requestModel.getUserRole());
        user.setGender(requestModel.getGender());

        if (HelperMethods.isPictureUploaded(requestModel.getPicture())) {
            String picture = HelperMethods.getMultiPartFileAs64BaseString(requestModel.getPicture());
            if (picture == null) {
                model.addAttribute(Constants.ERROR, "System error, please contact your administrator");
                return Constants.TMPL_USER_EDIT;
            }

            user.setPicture(picture);
        }

        userRepository.save(user);
        return Constants.REDIRECT + Constants.RECIPES;
    }

    // helper methods
    private MultipartFile getDefaultPicture() {
        try {
            File pic = new File("src/main/resources/default-avatar.jpg");
            FileInputStream fileInputStream = new FileInputStream(pic);
            return new MockMultipartFile(pic.getName(), pic.getName(), MediaType.TEXT_HTML_VALUE, fileInputStream);
        }
        catch (Exception exception) { return null; }
    }

    public String encryptedPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }
}
