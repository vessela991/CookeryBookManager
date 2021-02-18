package com.example.demo.controller;

import com.example.demo.internal.Constants;
import com.example.demo.security.UserPrincipal;
import com.example.demo.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;

@ControllerAdvice
public class BaseController {
    @Autowired
    private CommonUtils commonUtils;

    @ModelAttribute()
    public void addAttributes(Model model, HttpSession session) {
        model.addAttribute(Constants.TITLE, "IF YOU SEE THIS SET TITLE");
        model.addAttribute(Constants.COMMON_UTILS, commonUtils);
        model.addAttribute(Constants.LOGGED_USER,
                session.getAttribute(Constants.SPRING_SECURITY_CONTEXT) == null ? null :
                // get security context, get authentication, get principal, cast it to our custom principal and get user
                ((UserPrincipal) (((SecurityContextImpl) session.getAttribute(Constants.SPRING_SECURITY_CONTEXT))
                .getAuthentication().getPrincipal())).getUser());
    }
}
