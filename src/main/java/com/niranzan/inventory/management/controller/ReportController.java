package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.service.CategoryService;
import com.niranzan.inventory.management.service.PurchaseOrderService;
import com.niranzan.inventory.management.service.ReportService;
import com.niranzan.inventory.management.service.SupplierService;
import com.niranzan.inventory.management.view.response.PurchaseOrderItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

import static com.niranzan.inventory.management.enums.AppPages.PURCHASE_REPORT_PATH;

@Controller
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final PurchaseOrderService purchaseOrderService;
    private final SupplierService supplierService;
    private final CategoryService categoryService;

    @GetMapping("/purchase-report")
    public String purchaseReport(Model model) {
        model.addAttribute("purchases", List.of());
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        return PURCHASE_REPORT_PATH.getPath();
    }

    @GetMapping("/purchase-report/filter")
    @ResponseBody
    public List<PurchaseOrderItemResponse> filterPurchaseOrderItems(
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Long categoryId) {
        return purchaseOrderService.filterPurchaseOrderItems(startDate, endDate, supplierId, categoryId);
    }
}
