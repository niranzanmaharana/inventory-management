package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.dto.UserProfileDto;
import com.niranzan.inventory.management.dto.UserRoleDto;
import com.niranzan.inventory.management.entity.UserProfile;
import com.niranzan.inventory.management.enums.AppErrorCode;
import com.niranzan.inventory.management.enums.AppMessageParameter;
import com.niranzan.inventory.management.enums.AppRole;
import com.niranzan.inventory.management.service.UserService;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.niranzan.inventory.management.enums.AppPages.REDIRECT_URL;
import static com.niranzan.inventory.management.enums.AppPages.REGISTRATION_PATH;

@Controller
@RequiredArgsConstructor
public class AuthController extends BaseController {
    public static final String MODEL_ATTR_PLACEHOLDER_FOR_USER_PROFILE = "userProfile";

    private final UserService UserService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registration(Model model) {
        model.addAttribute(MODEL_ATTR_PLACEHOLDER_FOR_USER_PROFILE, new UserProfileDto());
        return REGISTRATION_PATH.getPath();
    }

    @PostMapping("/register")
    public String registration(@Valid @ModelAttribute("userProfile") UserProfileDto userProfileDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute(MODEL_ATTR_PLACEHOLDER_FOR_USER_PROFILE, userProfileDto);
            return REGISTRATION_PATH.getPath();
        }

        try {
            UserRoleDto userRoleDto = new UserRoleDto();
            userRoleDto.setRoleName(AppRole.ROLE_STAFF.getRoleName());
            userProfileDto.setUserRole(userRoleDto);
            UserProfile userProfile = UserService.saveUser(userProfileDto);
            attributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("{} registered successful", userProfile.getFirstName()));
        } catch (DataIntegrityViolationException ex) {
            return extractException(userProfileDto, result, model, ex);
        } catch (Exception ex) {
            model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), "An unexpected error occurred: " + ex.getMessage());
            model.addAttribute(MODEL_ATTR_PLACEHOLDER_FOR_USER_PROFILE, userProfileDto);
            return REGISTRATION_PATH.getPath();
        }

        return REDIRECT_URL.getPath() + REGISTRATION_PATH.getPath();
    }

    private static String extractException(UserProfileDto userProfileDto, BindingResult result, Model model, DataIntegrityViolationException ex) {
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
                model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), "Duplicate entry found: " + ex.getMessage());
            }
        } else {
            model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), "A data integrity violation occurred.: " + ex.getMessage());
        }

        model.addAttribute(MODEL_ATTR_PLACEHOLDER_FOR_USER_PROFILE, userProfileDto);
        return REGISTRATION_PATH.getPath();
    }
}
