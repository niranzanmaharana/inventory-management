package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.dto.SupplierDto;
import com.niranzan.inventory.management.entity.Supplier;
import com.niranzan.inventory.management.enums.AppMessageParameter;
import com.niranzan.inventory.management.service.SupplierService;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

import static com.niranzan.inventory.management.enums.AppPages.*;

@Controller
@RequestMapping("/supplier")
@RequiredArgsConstructor
public class SupplierController extends BaseController {
    private final SupplierService supplierService;

    @GetMapping("/supplier-list")
    public String listSuppliers(Model model) {
        model.addAttribute("suppliers", supplierService.findAll());
        return SUPPLIER_LIST_PATH.getPath();
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("supplier", new SupplierDto());
        return SUPPLIER_FORM_PATH.getPath();
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        SupplierDto supplier = supplierService.getSupplierById(id);
        model.addAttribute("supplier", supplier);
        return SUPPLIER_FORM_PATH.getPath();
    }

    @PostMapping("/save")
    public String saveSupplier(@Valid @ModelAttribute("supplier") SupplierDto dto, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return SUPPLIER_FORM_PATH.getPath();
        }

        SupplierDto savedDto = Objects.isNull(dto.getId())
                ? supplierService.saveSupplier(dto)
                : supplierService.updateSupplier(dto);

        attributes.addFlashAttribute("success", "Supplier " + savedDto.getSupplierName() + " saved successfully");

        return REDIRECT_URL.getPath() + SUPPLIER_LIST_PATH.getPath();
    }

    @PostMapping("/toggle-status")
    public String toggleStatus(@RequestParam long id, RedirectAttributes redirectAttributes) {
        Supplier supplier = supplierService.toggleStatus(id);
        redirectAttributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("Supplier status updated for {}", supplier.getSupplierName()));
        return REDIRECT_URL.getPath() + SUPPLIER_LIST_PATH.getPath();
    }
}
