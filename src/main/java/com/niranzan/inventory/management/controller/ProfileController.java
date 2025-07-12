package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.dto.UserDto;
import com.niranzan.inventory.management.entity.User;
import com.niranzan.inventory.management.enums.AppMessagePlaceholder;
import com.niranzan.inventory.management.exceptions.PasswordMismatchException;
import com.niranzan.inventory.management.mapper.RoleMapper;
import com.niranzan.inventory.management.mapper.UserMapper;
import com.niranzan.inventory.management.service.UserService;
import com.niranzan.inventory.management.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import static com.niranzan.inventory.management.enums.AppPages.PROFILE_CHANGE_PASSWORD_PAGE;
import static com.niranzan.inventory.management.enums.AppPages.PROFILE_VIEW_PAGE;
import static com.niranzan.inventory.management.enums.AppPages.REDIRECT_URL;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;

    @GetMapping("view-profile")
    public String profile(Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        UserDto userDto = userMapper.toDto(user);
        model.addAttribute("user", userDto);
        return PROFILE_VIEW_PAGE.getPageName();
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model, Principal principal, RedirectAttributes attributes) {
        User currentUser = userService.findUserByUsername(principal.getName());

        // Restore immutable fields not submitted in form
        userDto.setId(currentUser.getId());
        userDto.setUsername(currentUser.getUsername());
        userDto.setRole(roleMapper.toDto(currentUser.getRole()));

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return PROFILE_VIEW_PAGE.getPageName();
        }

        try {
            User updatedUser = userService.updateProfile(userDto);
            attributes.addFlashAttribute("user", updatedUser);
            attributes.addFlashAttribute("success", "Profile info updated successfully");
            return REDIRECT_URL.getPageName() + PROFILE_VIEW_PAGE.getPageName();
        } catch (Exception e) {
            model.addAttribute("user", userDto);
            model.addAttribute(AppMessagePlaceholder.ERROR_MSG_PLACEHOLDER.getPlaceholderName(), e.getMessage());
            return PROFILE_VIEW_PAGE.getPageName();
        }
    }

    @GetMapping("/change-password")
    public String changePassword(Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        UserDto userDto = userMapper.toDto(user);
        model.addAttribute("user", userDto);
        return PROFILE_CHANGE_PASSWORD_PAGE.getPageName();
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 RedirectAttributes redirectAttributes) {
        try {
            long currentUserId = SecurityUtil.getCurrentUserId();
            userService.changePassword(currentUserId, currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully.");
        } catch (PasswordMismatchException e) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong: " + e.getMessage());
        }

        return REDIRECT_URL.getPageName() + PROFILE_CHANGE_PASSWORD_PAGE.getPageName();
    }
}
