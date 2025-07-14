package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.dto.UserProfileDto;
import com.niranzan.inventory.management.entity.UserProfile;
import com.niranzan.inventory.management.enums.AppMessageParameter;
import com.niranzan.inventory.management.exceptions.PasswordMismatchException;
import com.niranzan.inventory.management.mapper.UserRoleMapper;
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
    private UserRoleMapper userRoleMapper;

    @GetMapping("view-profile")
    public String profile(Model model, Principal principal) {
        UserProfile userProfile = userService.findUserByUsername(principal.getName());
        UserProfileDto userProfileDto = userMapper.toDto(userProfile);
        model.addAttribute("userProfile", userProfileDto);
        return PROFILE_VIEW_PAGE.getPageName();
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("userProfile") UserProfileDto userProfileDto, BindingResult result, Model model, Principal principal, RedirectAttributes attributes) {
        UserProfile currentUserProfile = userService.findUserByUsername(principal.getName());

        // Restore immutable fields not submitted in form
        userProfileDto.setId(currentUserProfile.getId());
        userProfileDto.setUsername(currentUserProfile.getUsername());
        userProfileDto.setUserRole(userRoleMapper.toDto(currentUserProfile.getUserRole()));

        if (result.hasErrors()) {
            model.addAttribute("userProfile", userProfileDto);
            return PROFILE_VIEW_PAGE.getPageName();
        }

        try {
            UserProfile updatedUserProfile = userService.updateProfile(userProfileDto);
            attributes.addFlashAttribute("userProfile", updatedUserProfile);
            attributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), "Profile info updated successfully");
            return REDIRECT_URL.getPageName() + PROFILE_VIEW_PAGE.getPageName();
        } catch (Exception e) {
            model.addAttribute("userProfile", userProfileDto);
            model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), e.getMessage());
            return PROFILE_VIEW_PAGE.getPageName();
        }
    }

    @GetMapping("/change-password")
    public String changePassword(Model model, Principal principal) {
        UserProfile userProfile = userService.findUserByUsername(principal.getName());
        UserProfileDto userProfileDto = userMapper.toDto(userProfile);
        model.addAttribute("userProfile", userProfileDto);
        return PROFILE_CHANGE_PASSWORD_PAGE.getPageName();
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 RedirectAttributes redirectAttributes) {
        try {
            long currentUserId = SecurityUtil.getCurrentUserId();
            userService.changePassword(currentUserId, currentPassword, newPassword);
            redirectAttributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), "Password changed successfully. Please log in again.");
            return "redirect:/login?passwordChanged";
        } catch (PasswordMismatchException e) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong: " + e.getMessage());
        }

        return REDIRECT_URL.getPageName() + PROFILE_CHANGE_PASSWORD_PAGE.getPageName();
    }
}
