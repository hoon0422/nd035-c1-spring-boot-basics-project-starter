package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("signup")
public class SignupController {
    private final UserService userService;

    public SignupController(
            final UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String signupView(final @ModelAttribute("signup") User user) {
        return "signup";
    }

    @PostMapping()
    public String signup(final @ModelAttribute("signup") User user,
                         final RedirectAttributes redirectAttributes) {
        String signupError = null;
        if (!userService.isUsernameValid(user.getUsername())) {
            redirectAttributes.addFlashAttribute("signupError",
                    "Username already exists. Please enter another username");
        }
        int rows = userService.createUser(user);
        if (rows <= 0) {
            redirectAttributes.addFlashAttribute("signupError",
                    "There was an error registering a user. Please try again");
        } else {
            redirectAttributes.addFlashAttribute("signupError", null);
            redirectAttributes.addFlashAttribute("signupSuccess", true);
        }
        return "redirect:/signup";
    }
}
