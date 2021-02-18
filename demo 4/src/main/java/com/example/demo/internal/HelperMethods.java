package com.example.demo.internal;

import com.example.demo.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.util.Base64;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class HelperMethods {
    public <T> List<T> filterCollection(Iterable<T> collection, Predicate<? super T> filter) {
        return StreamSupport.stream(collection.spliterator(), false)
                .filter(filter).collect(Collectors.toList());
    }

    public <T> List<T> getAsCollection(Iterable<T> collection) {
        return StreamSupport.stream(collection.spliterator(), false).collect(Collectors.toList());
    }

    public static User getLoggedInUser(Model model) {
        return (User) model.getAttribute(Constants.LOGGED_USER);
    }

    public static Long getLoggedInUserId(Model model) {
        return getLoggedInUser(model).getId();
    }

    public static String getMultiPartFileAs64BaseString(MultipartFile picture) {
        try {
            Blob pictureBlob = new SerialBlob(picture.getBytes());
            return Base64.getEncoder().encodeToString(pictureBlob.getBytes(1, (int) pictureBlob.length()));
        }catch (Exception exception) {
            return null;
        }
    }

    public static boolean isPictureUploaded(MultipartFile picture) {
        return picture != null && !"".equals(picture.getResource().getFilename());
    }
}
