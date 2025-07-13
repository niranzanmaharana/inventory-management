package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.dto.UserDto;
import com.niranzan.inventory.management.entity.User;
import com.niranzan.inventory.management.enums.AppErrorCode;
import com.niranzan.inventory.management.enums.AppMessagePlaceholder;
import com.niranzan.inventory.management.mapper.RoleMapper;
import com.niranzan.inventory.management.mapper.UserMapper;
import com.niranzan.inventory.management.service.UserService;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.niranzan.inventory.management.enums.AppPages.REDIRECT_URL;
import static com.niranzan.inventory.management.enums.AppPages.USER_FORM;
import static com.niranzan.inventory.management.enums.AppPages.USER_LIST;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;

    @GetMapping("/users")
    public String users(Model model) {
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return USER_LIST.getPageName();
    }

    @GetMapping("/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new UserDto());
        return USER_FORM.getPageName();
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(Model model, @PathVariable long id) {
        User user = userService.findById(id);
        UserDto userDto = userMapper.toDto(user);
        model.addAttribute("user", userDto);
        return USER_FORM.getPageName();
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return USER_FORM.getPageName();
        }

        try {
            User savedUser = (userDto.getId() == null) ? userService.saveUser(userDto) : userService.updateUser(userDto);

            userDto.setId(savedUser.getId());
            redirectAttributes.addFlashAttribute(AppMessagePlaceholder.SUCCESS_MSG_PLACEHOLDER.getPlaceholderName(), MessageFormatUtil.format("User ({}) saved successfully", userDto.getUsername()));
            return REDIRECT_URL.getPageName() + USER_LIST.getPageName();
        } catch (DataIntegrityViolationException ex) {
            return extractException(userDto, result, model, ex);
        } catch (Exception ex) {
            model.addAttribute(AppMessagePlaceholder.ERROR_MSG_PLACEHOLDER.getPlaceholderName(), "An error occurred while saving user: " + ex.getMessage());
            model.addAttribute("user", userDto);
            return USER_FORM.getPageName();
        }
    }

    @PostMapping("/toggle-status")
    public String toggleUserStatus(@RequestParam long id, RedirectAttributes redirectAttributes) {
        User updatedUser = userService.toggleUserStatus(id);
        redirectAttributes.addFlashAttribute("success", "User status updated for (" + updatedUser.getFirstName() + ")");
        return REDIRECT_URL.getPageName() + USER_LIST.getPageName();
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
            } else {
                model.addAttribute(AppMessagePlaceholder.ERROR_MSG_PLACEHOLDER.getPlaceholderName(), "Duplicate entry found: " + ex.getMessage());
            }
        } else {
            model.addAttribute(AppMessagePlaceholder.ERROR_MSG_PLACEHOLDER.getPlaceholderName(), "A data integrity violation occurred.: " + ex.getMessage());
        }

        model.addAttribute("user", userDto);
        return USER_FORM.getPageName();
    }
}
