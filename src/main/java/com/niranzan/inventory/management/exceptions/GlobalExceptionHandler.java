package com.niranzan.inventory.management.exceptions;

import com.niranzan.inventory.management.dto.CustomUserDetail;
import com.niranzan.inventory.management.enums.AppMessagePlaceholder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Collections;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handle404(NoHandlerFoundException ex) {
        ModelAndView mav = new ModelAndView("error/page-not-found");
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap(AppMessagePlaceholder.ERROR_MSG_PLACEHOLDER.getPlaceholderName(), "Invalid username or password"));
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
