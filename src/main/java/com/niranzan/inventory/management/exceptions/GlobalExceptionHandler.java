package com.niranzan.inventory.management.exceptions;

import com.niranzan.inventory.management.dto.CustomUserDetail;
import com.niranzan.inventory.management.enums.AppMessageParameter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handle404(NoHandlerFoundException ex) {
        ModelAndView mav = new ModelAndView("error/page-not-found");
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap(AppMessageParameter.ERROR_PARAM_NM.getName(), "Invalid username or password"));
    }

    @ModelAttribute
    public void addUserInfoToModel(HttpServletRequest request, Model model, @AuthenticationPrincipal CustomUserDetail userDetails) {
        model.addAttribute("requestURI", request.getRequestURI());
        if (userDetails != null) {
            model.addAttribute("firstName", userDetails.getFirstName());
            model.addAttribute("roles", userDetails.getAuthorities());
        }
    }
}
