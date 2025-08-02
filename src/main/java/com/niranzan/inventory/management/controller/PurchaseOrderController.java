package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.dto.PurchaseOrderDto;
import com.niranzan.inventory.management.service.CategoryService;
import com.niranzan.inventory.management.service.ProductService;
import com.niranzan.inventory.management.service.PurchaseOrderService;
import com.niranzan.inventory.management.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.niranzan.inventory.management.enums.AppPages.*;

@Controller
@RequestMapping("/purchase-order")
@RequiredArgsConstructor
public class PurchaseOrderController extends BaseController {
    private final PurchaseOrderService purchaseOrderService;
    private final SupplierService supplierService;
    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/purchase-order-form")
    public String newPurchaseOrderForm(Model model) {
        model.addAttribute("purchaseOrder", new PurchaseOrderDto());
        model.addAttribute("products", productService.findAll());
        model.addAttribute("parentCategories", categoryService.findParentCategories());
        model.addAttribute("suppliers", supplierService.findAll());

        return PURCHASE_ORDER_FORM_PATH.getPath();
    }

    @PostMapping("/save")
    public String savePurchaseOrder(@ModelAttribute @Valid PurchaseOrderDto purchaseOrderDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("products", productService.findAll());
            model.addAttribute("suppliers", supplierService.findAll());
            return PURCHASE_ORDER_FORM_PATH.getPath();
        }
        purchaseOrderService.createPurchaseOrder(purchaseOrderDto);
        return REDIRECT_URL.getPath() + PURCHASE_ORDER_LIST_PATH.getPath();
    }
}
