package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.dto.ProfileDto;
import com.niranzan.inventory.management.dto.UserProfileDto;
import com.niranzan.inventory.management.entity.UserProfile;
import com.niranzan.inventory.management.enums.AppMessageParameter;
import com.niranzan.inventory.management.exceptions.PasswordMismatchException;
import com.niranzan.inventory.management.mapper.UserMapper;
import com.niranzan.inventory.management.service.UserService;
import com.niranzan.inventory.management.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import static com.niranzan.inventory.management.enums.AppPages.*;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController extends BaseController {
    private final UserService UserService;
    private final UserMapper userMapper;

    @GetMapping("view-profile")
    public String profile(Model model, Principal principal) {
        log.info("Viewing profile");
        UserProfile userProfile = UserService.findUserByUsername(principal.getName());
        ProfileDto profile = userMapper.toProfile(userProfile);
        model.addAttribute("userProfile", profile);
        return PROFILE_VIEW_PATH.getPath();
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("userProfile") ProfileDto profileDto, BindingResult result, Model model, Principal principal, RedirectAttributes attributes) {
        UserProfile currentUserProfile = UserService.findUserByUsername(principal.getName());

        // Restore immutable fields not submitted in form
        profileDto.setId(currentUserProfile.getId());
        profileDto.setUsername(currentUserProfile.getUsername());

        if (result.hasErrors()) {
            model.addAttribute("userProfile", profileDto);
            return PROFILE_VIEW_PATH.getPath();
        }

        try {
            UserProfile updatedUserProfile = UserService.updateProfile(profileDto);
            attributes.addFlashAttribute("userProfile", updatedUserProfile);
            attributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), "Profile info updated successfully");
            return REDIRECT_URL.getPath() + PROFILE_VIEW_PATH.getPath();
        } catch (Exception e) {
            model.addAttribute("userProfile", profileDto);
            model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), e.getMessage());
            return PROFILE_VIEW_PATH.getPath();
        }
    }

    @GetMapping("/change-password")
    public String changePassword(Model model, Principal principal) {
        UserProfile userProfile = UserService.findUserByUsername(principal.getName());
        UserProfileDto userProfileDto = userMapper.toDto(userProfile);
        model.addAttribute("userProfile", userProfileDto);
        return PROFILE_CHANGE_PASSWORD_PATH.getPath();
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 RedirectAttributes redirectAttributes) {
        try {
            long currentUserId = SecurityUtil.getCurrentUserId();
            UserService.changePassword(currentUserId, currentPassword, newPassword);
            redirectAttributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), "Password changed successfully. Please log in again.");
            return "redirect:/login?passwordChanged";
        } catch (PasswordMismatchException e) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong: " + e.getMessage());
        }

        return REDIRECT_URL.getPath() + PROFILE_CHANGE_PASSWORD_PATH.getPath();
    }
}
