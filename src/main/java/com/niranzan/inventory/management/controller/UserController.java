package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.dto.UserProfileDto;
import com.niranzan.inventory.management.entity.UserProfile;
import com.niranzan.inventory.management.enums.AppErrorCode;
import com.niranzan.inventory.management.enums.AppMessageParameter;
import com.niranzan.inventory.management.mapper.UserRoleMapper;
import com.niranzan.inventory.management.mapper.UserMapper;
import com.niranzan.inventory.management.service.UserService;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
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
    private UserRoleMapper userRoleMapper;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/user-list")
    public String users(Model model) {
        List<UserProfileDto> users = userService.findAllUsers();
        model.addAttribute("userProfiles", users);
        return USER_LIST.getPageName();
    }

    @GetMapping("/add")
    public String addUserForm(Model model) {
        model.addAttribute("userProfile", new UserProfileDto());
        return USER_FORM.getPageName();
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(Model model, @PathVariable long id) {
        UserProfile userProfile = userService.findById(id);
        UserProfileDto userProfileDto = userMapper.toDto(userProfile);
        model.addAttribute("userProfile", userProfileDto);
        return USER_FORM.getPageName();
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("userProfile") UserProfileDto userProfileDto, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("userProfile", userProfileDto);
            return USER_FORM.getPageName();
        }

        try {
            UserProfile savedUserProfile = (userProfileDto.getId() == null) ? userService.saveUser(userProfileDto) : userService.updateUser(userProfileDto);

            userProfileDto.setId(savedUserProfile.getId());
            redirectAttributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("UserProfile ({}) saved successfully", userProfileDto.getUsername()));
            return REDIRECT_URL.getPageName() + USER_LIST.getPageName();
        } catch (DataIntegrityViolationException ex) {
            return extractException(userProfileDto, result, model, ex);
        } catch (Exception ex) {
            model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), "An error occurred while saving user: " + ex.getMessage());
            model.addAttribute("userProfile", userProfileDto);
            return USER_FORM.getPageName();
        }
    }

    @PostMapping("/toggle-status")
    public String toggleUserStatus(@RequestParam long id, RedirectAttributes redirectAttributes) {
        UserProfile updatedUserProfile = userService.toggleUserStatus(id);
        redirectAttributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("UserProfile status updated for {}", updatedUserProfile.getFirstName()));
        return REDIRECT_URL.getPageName() + USER_LIST.getPageName();
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

        model.addAttribute("userProfile", userProfileDto);
        return USER_FORM.getPageName();
    }
}
