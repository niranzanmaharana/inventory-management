package com.niranzan.inventory.management.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niranzan.inventory.management.dto.ProductDto;
import com.niranzan.inventory.management.enums.AppMessageParameter;
import com.niranzan.inventory.management.enums.ImageFileType;
import com.niranzan.inventory.management.service.AttributeTypeService;
import com.niranzan.inventory.management.service.CategoryService;
import com.niranzan.inventory.management.service.ProductService;
import com.niranzan.inventory.management.service.SupplierService;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import com.niranzan.inventory.management.view.response.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.niranzan.inventory.management.enums.AppPages.PRODUCT_FORM_PATH;
import static com.niranzan.inventory.management.enums.AppPages.PRODUCT_LIST_PATH;
import static com.niranzan.inventory.management.enums.AppPages.REDIRECT_URL;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController extends BaseController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final AttributeTypeService attributeTypeService;
    private final SupplierService supplierService;
    private final ObjectMapper objectMapper;
    @Value("${default.image-path}")
    private String defaultImagePath;

    @GetMapping("/product-list")
    public String listProducts(Model model) {
        List<ProductResponse> products = productService.findAll();
        log.info("Retrieved {} products", products.size());
        model.addAttribute("products", products);
        return PRODUCT_LIST_PATH.getPath();
    }

    @GetMapping("/by-category/{categoryId}")
    @ResponseBody
    public ResponseEntity<List<ProductDto>> findByCategory(@PathVariable Long categoryId) {
        List<ProductDto> products = productService.findByCategory(categoryId);
        log.info("Found {} products by {} category", products.size(), categoryId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/add")
    public String addProductForm(Model model) {
        ProductDto productDto = new ProductDto();
        productDto.setProductAttributes(new HashMap<>());
        log.info("New product form request received");
        addModelAttributes(model, productDto);
        return PRODUCT_FORM_PATH.getPath();
    }

    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") ProductDto productDto,
                              BindingResult result,
                              @RequestParam("attributeJson") String attributeJson,
                              Model model,
                              RedirectAttributes attributes) {
        log.info("Adding new product with product name: {}", productDto.getProductName());
        if (result.hasErrors()) {
            handleValidationErrors(result);
            restoreAttributesIfPresent(attributeJson, productDto);
            addModelAttributes(model, productDto);
            return PRODUCT_FORM_PATH.getPath();
        }
        try {
            ProductDto savedProduct = productService.saveProduct(productDto, attributeJson);
            attributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(),
                    MessageFormatUtil.format("Product {} saved successfully", savedProduct.getProductName()));
            log.info("Product {} added successfully", productDto.getProductName());
            return REDIRECT_URL.getPath() + PRODUCT_LIST_PATH.getPath();
        } catch (Exception e) {
            log.error("Failed adding product: {}", e.getMessage());
            model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), e.getMessage());
            addModelAttributes(model, productDto);
            return PRODUCT_FORM_PATH.getPath();
        }
    }

    @GetMapping("/edit/{productId}")
    public String editProductForm(@PathVariable Long productId, Model model) {
        log.info("Received request to open edit product form with id: {}", productId);
        ProductDto productDto = productService.prepareDataForEdit(productId);
        log.info("Sending product form for product: {}", productDto.getProductName());
        addModelAttributes(model, productDto);
        return PRODUCT_FORM_PATH.getPath();
    }

    @PostMapping("/upload-image")
    @ResponseBody
    public ResponseEntity<String> uploadProductImage(
            @RequestParam(name = "productId") Long productId,
            @RequestParam(name = "imageType") String imageType,
            @RequestParam("file") MultipartFile file) {
        log.info("Received request to upload image with type: {}, for product with id: {}", imageType, productId);
        return ResponseEntity.ok(productService.uploadImage(productId, imageType, file));
    }

    @PostMapping("/image-path")
    @ResponseBody
    public String getImagePath(@RequestParam Long id, @RequestParam String imageType) {
        log.info("Received request to find image path with image type: {} for product: {}", imageType, id);
        String imagePath = defaultImagePath;
        ProductDto product = productService.findById(id);
        if (product == null || StringUtils.isBlank(imageType)) {
            return imagePath;
        }

        String normalizedType = imageType.trim().toUpperCase();

        if (ImageFileType.PRODUCT_PHOTO.name().equals(normalizedType)) {
            imagePath = product.getProductImagePath();
        } else if (ImageFileType.RECEIPT_PHOTO.name().equals(normalizedType)) {
            imagePath = product.getInvoiceImagePath();
        }

        return StringUtils.defaultString(imagePath);
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes attributes) {
        log.info("Received request to toggle the status of product with id: {}", id);
        boolean exists = productService.productExistsById(id);
        if (exists) {
            ProductDto productDto = productService.changeStatus(id);
            attributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("{} status updated", productDto.getProductName()));
            log.info("Toggled product status with id: {}", id);
        } else {
            log.error("Cannot find product with id: {}", id);
            attributes.addFlashAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), MessageFormatUtil.format("Cannot find product with id: {}", String.valueOf(id)));
        }
        return REDIRECT_URL.getPath() + PRODUCT_LIST_PATH.getPath();
    }

    private void addModelAttributes(Model model, ProductDto productDto) {
        model.addAttribute("product", productDto);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("attributeTypes", attributeTypeService.findAllAttributes());
        model.addAttribute("productAttributes", convertAttributes(productDto.getProductAttributes()));
    }

    private Map<String, String> convertAttributes(Map<Long, String> original) {
        return original.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        Map.Entry::getValue
                ));
    }

    private void handleValidationErrors(BindingResult result) {
        Map<String, String> errorMap = result.getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> (error instanceof FieldError) ? ((FieldError) error).getField() : error.getObjectName(),
                        ObjectError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));
        log.error("Form validation failed: {}", errorMap);
    }

    private void restoreAttributesIfPresent(String attributeJson, ProductDto productDto) {
        if (StringUtils.isNotBlank(attributeJson)) {
            try {
                Map<Long, String> restoredAttributes = objectMapper.readValue(
                        attributeJson, new TypeReference<>() {
                        });
                productDto.setProductAttributes(restoredAttributes);
            } catch (Exception e) {
                log.error("Failed to parse attributeJson", e);
            }
        }
    }
}
