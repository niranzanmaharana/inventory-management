package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.dto.UserDto;
import com.niranzan.inventory.management.entity.User;
import com.niranzan.inventory.management.mapper.UserMapper;
import com.niranzan.inventory.management.service.UserService;
import com.niranzan.inventory.management.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.niranzan.inventory.management.enums.AppPages.USER_HOME;

@Controller
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public String home(Model model) {
        User user = userService.findById(SecurityUtil.getCurrentUserId());
        UserDto userDto = userMapper.toDto(user);
        model.addAttribute("user", userDto);
        return USER_HOME.getPageName();
    }
}
