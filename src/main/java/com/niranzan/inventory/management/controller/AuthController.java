package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.dto.UserDto;
import com.niranzan.inventory.management.enums.AppErrorCode;
import com.niranzan.inventory.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    public static final String REGISTRATION_PAGE = "register";
    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registration(Model model) {
        model.addAttribute("user", new UserDto());
        return REGISTRATION_PAGE;
    }

    @PostMapping("/register")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return REGISTRATION_PAGE;
        }

        try {
            userService.saveUser(userDto, "ROLE_ADMIN");
        } catch (DataIntegrityViolationException ex) {
            return extractException(userDto, result, model, ex);
        } catch (Exception ex) {
            model.addAttribute("error", "An unexpected error occurred: " + ex.getMessage());
            model.addAttribute("user", userDto);
            return REGISTRATION_PAGE;
        }

        return "redirect:/register?success";
    }

    private static String extractException(UserDto userDto, BindingResult result, Model model, DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        if (message.contains("Duplicate entry")) {
            if (message.contains("UK_user_mobile")) {
                result.rejectValue("mobile", AppErrorCode.DUPLICATE_ELEMENT.getErrorCode(), "Mobile number already exists.");
            }
            if (message.contains("UK_user_email")) {
                result.rejectValue("email", AppErrorCode.DUPLICATE_ELEMENT.getErrorCode(), "Email already exists.");
            }
            if (message.contains("UK_user_username")) {
                result.rejectValue("username", AppErrorCode.DUPLICATE_ELEMENT.getErrorCode(), "Username already exists.");
            }
        } else {
            model.addAttribute("error", "A data integrity violation occurred.: " + ex.getMessage());
        }

        model.addAttribute("user", userDto);
        return REGISTRATION_PAGE;
    }
}
