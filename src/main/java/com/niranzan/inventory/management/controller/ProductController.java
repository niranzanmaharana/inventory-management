package com.niranzan.inventory.management.controller;

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
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

import static com.niranzan.inventory.management.enums.AppPages.PRODUCT_FORM_PATH;
import static com.niranzan.inventory.management.enums.AppPages.PRODUCT_LIST_PATH;
import static com.niranzan.inventory.management.enums.AppPages.REDIRECT_URL;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController extends BaseController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final AttributeTypeService attributeTypeService;
    private final SupplierService supplierService;
    private final String DEFAULT_IMAGE_PATH = "/images/product-default-image.png";

    @GetMapping("/product-list")
    public String listProducts(Model model) {
        List<ProductResponse> products = productService.findAll();
        model.addAttribute("products", products);
        return PRODUCT_LIST_PATH.getPath();
    }

    @GetMapping("/by-category/{categoryId}")
    @ResponseBody
    public ResponseEntity<List<ProductDto>> findByCategory(@PathVariable Long categoryId) {
        List<ProductDto> products = productService.findByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/add")
    public String addProductForm(Model model) {
        ProductDto productDto = new ProductDto();
        productDto.setProductAttributes(new HashMap<>());
        addModelAttributes(model, productDto);
        return PRODUCT_FORM_PATH.getPath();
    }

    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") ProductDto productDto,
                              BindingResult result,
                              @RequestParam("attributeJson") String attributeJson,
                              Model model,
                              RedirectAttributes attributes) {
        if (result.hasErrors()) {
            addModelAttributes(model, productDto);
            return PRODUCT_FORM_PATH.getPath();
        }
        try {
            ProductDto savedProduct = productService.saveProduct(productDto, attributeJson);
            attributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(),
                    MessageFormatUtil.format("Product {} saved successfully", savedProduct.getProductName()));
            return REDIRECT_URL.getPath() + PRODUCT_LIST_PATH.getPath();
        } catch (Exception e) {
            model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), e.getMessage());
            addModelAttributes(model, productDto);
            return PRODUCT_FORM_PATH.getPath();
        }
    }

    @GetMapping("/edit/{productId}")
    public String editProductForm(@PathVariable Long productId, Model model) {
        ProductDto productDto = productService.prepareDataForEdit(productId);
        addModelAttributes(model, productDto);
        return PRODUCT_FORM_PATH.getPath();
    }

    @PostMapping("/upload-image")
    @ResponseBody
    public ResponseEntity<String> uploadProductImage(
            @RequestParam(name = "productId") Long productId,
            @RequestParam(name = "imageType") String imageType,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(productService.uploadImage(productId, imageType, file));
    }

    @PostMapping("/image-path")
    @ResponseBody
    public String getImagePath(@RequestParam Long id, @RequestParam String imageType) {
        String imagePath = DEFAULT_IMAGE_PATH;
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
        boolean exists = productService.productExistsById(id);
        if (exists) {
            ProductDto productDto = productService.changeStatus(id);
            attributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("{} status updated", productDto.getProductName()));
        } else {
            attributes.addFlashAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), MessageFormatUtil.format("Cannot find product with id: {}", String.valueOf(id)));
        }
        return REDIRECT_URL.getPath() + PRODUCT_LIST_PATH.getPath();
    }

    private void addModelAttributes(Model model, ProductDto productDto) {
        model.addAttribute("product", productDto);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("attributeTypes", attributeTypeService.findAllAttributes());
        Map<String, String> attrMap = new HashMap<>();
        for (Map.Entry<Long, String> entry : productDto.getProductAttributes().entrySet()) {
            attrMap.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        model.addAttribute("productAttributes", attrMap);
    }
}
