package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.dto.SalesTransactionDto;
import com.niranzan.inventory.management.entity.SalesTransaction;
import com.niranzan.inventory.management.enums.AppMessageParameter;
import com.niranzan.inventory.management.service.InvoiceService;
import com.niranzan.inventory.management.service.SalesService;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import com.niranzan.inventory.management.view.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.niranzan.inventory.management.enums.AppPages.REDIRECT_URL;
import static com.niranzan.inventory.management.enums.AppPages.SALES_DASHBOARD_PATH;

@Controller
@RequestMapping("/sales")
@RequiredArgsConstructor
@Slf4j
public class SalesController extends BaseController {
    private final SalesService salesService;
    private final InvoiceService invoiceService;

    @GetMapping("/sales-dashboard")
    public String newSale(Model model) {
        model.addAttribute("sale", new SalesTransactionDto());
        return SALES_DASHBOARD_PATH.getPath();
    }

    @GetMapping("/confirmation/{invoiceNumber}")
    public String confirmOrder(@PathVariable String invoiceNumber, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("sale", new SalesTransactionDto());
        redirectAttributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format(
                "Order successfully placed. Invoice No: {}.", invoiceNumber
        ));
        redirectAttributes.addFlashAttribute("invoiceNumber", invoiceNumber);
        return REDIRECT_URL.getPath() + SALES_DASHBOARD_PATH.getPath();
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<ApiResponse<String>> saveSale(@Valid @RequestBody SalesTransactionDto sale, BindingResult result) {
        if (result.hasErrors()) {
            String errorMsg = result.getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                    .orElse("Validation failed");

            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, errorMsg, null));
        }

        try {
            SalesTransaction salesTransaction = salesService.saveSale(sale);
            log.info("Transaction saved with id: {}, invoice number: {}", salesTransaction.getId(), salesTransaction.getInvoiceNumber());
            String message = MessageFormatUtil.format("Order placed successfully with invoice number: {}", salesTransaction.getInvoiceNumber());
            return ResponseEntity.ok(new ApiResponse<>(true, message, salesTransaction.getInvoiceNumber()));
        } catch (Exception exception) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Error while placing the order: " + exception.getMessage(), null));
        }
    }
}
