package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.dto.CustomerDto;
import com.niranzan.inventory.management.service.CustomerService;
import com.niranzan.inventory.management.view.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/search")
    @ResponseBody
    public List<CustomerDto> search(@RequestParam String keyword) {
        return customerService.search(keyword);
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<ApiResponse<CustomerDto>> create(@Valid @RequestBody CustomerDto customer, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                    .orElse("Validation failed");

            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, errorMsg, null));
        }

        try {
            CustomerDto savedCustomer = customerService.save(customer);
            return ResponseEntity.ok(new ApiResponse<>(true, "Customer saved successfully", savedCustomer));

        } catch (Exception exception) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Error while saving customer: " + exception.getMessage(), null));
        }
    }
}
