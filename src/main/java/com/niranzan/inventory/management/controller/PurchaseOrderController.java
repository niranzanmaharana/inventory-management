package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.config.AppMessage;
import com.niranzan.inventory.management.dto.PurchaseOrderDto;
import com.niranzan.inventory.management.enums.AppMessageParameter;
import com.niranzan.inventory.management.enums.PurchaseOrderStatus;
import com.niranzan.inventory.management.service.ProductService;
import com.niranzan.inventory.management.service.PurchaseOrderItemService;
import com.niranzan.inventory.management.service.PurchaseOrderService;
import com.niranzan.inventory.management.service.SupplierService;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.niranzan.inventory.management.enums.AppPages.PURCHASE_ORDER_FORM_PATH;
import static com.niranzan.inventory.management.enums.AppPages.PURCHASE_ORDER_LIST_PATH;
import static com.niranzan.inventory.management.enums.AppPages.REDIRECT_URL;

@Controller
@RequestMapping("/purchase-order")
@RequiredArgsConstructor
public class PurchaseOrderController extends BaseController {
    private final PurchaseOrderService purchaseOrderService;
    private final PurchaseOrderItemService purchaseOrderItemService;
    private final SupplierService supplierService;
    private final ProductService productService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @GetMapping("/purchase-order-list")
    public String listPurchaseOrders(Model model) {
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        return PURCHASE_ORDER_LIST_PATH.getPath();
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/purchase-order-form")
    public String newPurchaseOrderForm(Model model) {
        addModelAttributes(model, new PurchaseOrderDto());
        return PURCHASE_ORDER_FORM_PATH.getPath();
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping("/save")
    public String savePurchaseOrder(
            @ModelAttribute @Valid PurchaseOrderDto purchaseOrderDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            addModelAttributes(model, purchaseOrderDto);
            return PURCHASE_ORDER_FORM_PATH.getPath();
        }
        try {
            purchaseOrderDto.setStatus(PurchaseOrderStatus.DRAFT.getStatus());
            String status = purchaseOrderService.draftPurchaseOrder(purchaseOrderDto);
            redirectAttributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("Purchase order processed with status: {}", status));
            return REDIRECT_URL.getPath() + PURCHASE_ORDER_LIST_PATH.getPath();
        } catch (Exception e) {
            model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), e.getMessage());
            addModelAttributes(model, purchaseOrderDto);
            return PURCHASE_ORDER_FORM_PATH.getPath();
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/edit/{id}")
    public String openPurchaseOrderFormForEditing(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        boolean isEditable = purchaseOrderService.findIfPurchaseOrderEditable(id);
        if (isEditable) {
            PurchaseOrderDto purchaseOrderDto = purchaseOrderService.prepareForEdit(id);
            addModelAttributes(model, purchaseOrderDto);
            return PURCHASE_ORDER_FORM_PATH.getPath();
        }
        redirectAttributes.addFlashAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), AppMessage.MSG_CANNOT_UPDATE_PURCHASE_ORDER.getMessage());
        return REDIRECT_URL.getPath() + PURCHASE_ORDER_LIST_PATH.getPath();
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/cancel/{id}")
    public String cancelPurchaseOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String status = purchaseOrderService.cancelPurchaseOrder(id);
            redirectAttributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("Purchase order cancelled and status updated: {}", status));
            return REDIRECT_URL.getPath() + PURCHASE_ORDER_LIST_PATH.getPath();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), e.getMessage());
            return REDIRECT_URL.getPath() + PURCHASE_ORDER_LIST_PATH.getPath();
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/place/{id}")
    public String placePurchaseOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String status = purchaseOrderService.placePurchaseOrder(id);
            redirectAttributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("Purchase order placed and status updated: {}", status));
            return REDIRECT_URL.getPath() + PURCHASE_ORDER_LIST_PATH.getPath();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), e.getMessage());
            return REDIRECT_URL.getPath() + PURCHASE_ORDER_LIST_PATH.getPath();
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/receive/{id}")
    public String receivePurchaseOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String status = purchaseOrderService.receivePurchaseOrder(id);
            redirectAttributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("Purchase order received and status updated: {}", status));
            return REDIRECT_URL.getPath() + PURCHASE_ORDER_LIST_PATH.getPath();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), e.getMessage());
            return REDIRECT_URL.getPath() + PURCHASE_ORDER_LIST_PATH.getPath();
        }
    }

    private void addModelAttributes(Model model, PurchaseOrderDto purchaseOrderDto) {
        model.addAttribute("purchaseOrder", purchaseOrderDto);
        model.addAttribute("products", productService.findAll());
        model.addAttribute("suppliers", supplierService.findAll());
    }
}
