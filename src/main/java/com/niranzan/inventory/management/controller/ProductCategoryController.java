package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.dto.ProductCategoryDto;
import com.niranzan.inventory.management.entity.ProductCategory;
import com.niranzan.inventory.management.enums.AppMessageParameter;
import com.niranzan.inventory.management.mapper.ProductCategoryMapper;
import com.niranzan.inventory.management.service.ProductCategoryService;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.niranzan.inventory.management.enums.AppPages.*;

@Controller
@RequestMapping("/product-category")
@RequiredArgsConstructor
public class ProductCategoryController extends BaseController {
    public static final String MODEL_ATTR_PLACEHOLDER_FOR_CATEGORY = "category";
    public static final String MODEL_ATTR_PLACEHOLDER_FOR_PARENT_CATEGORIES = "parentCategories";
    public static final String MODEL_ATTR_PLACEHOLDER_FOR_HAS_SUB_CATEGORIES = "hasSubCategories";
    private final ProductCategoryService categoryService;
    private final ProductCategoryMapper productCategoryMapper;

    @GetMapping("/category-list")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllTopCategoriesWithSubCategories());
        return PRODUCT_CATEGORY_LIST_PAGE.getPageName();
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        List<ProductCategoryDto> parentCategories = categoryService.getAllTopLevelCategories().stream().map(productCategoryMapper::toDto).toList();
        addModelAttributes(Boolean.FALSE, new ProductCategoryDto(), parentCategories, model);
        return PRODUCT_CATEGORY_FORM_PAGE.getPageName();
    }

    @PostMapping("/save")
    public String saveCategory(@Valid @ModelAttribute("category") ProductCategoryDto categoryDto, BindingResult result, Model model, RedirectAttributes attributes) {
        try {
            if (result.hasErrors()) {
                List<ProductCategoryDto> parentCategories = categoryService.getAllTopLevelCategories().stream().map(productCategoryMapper::toDto).toList();
                addModelAttributes(Boolean.FALSE, categoryDto, parentCategories, model);
                return PRODUCT_CATEGORY_FORM_PAGE.getPageName();
            }

            ProductCategory productCategory = categoryService.saveCategory(categoryDto);
            attributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("Product category {} added", productCategory.getCategoryName()));
            return REDIRECT_URL.getPageName() + PRODUCT_CATEGORY_LIST_PAGE.getPageName();
        } catch (DataIntegrityViolationException exception) {
            if (exception.getMessage().contains("UK_ProductCategory_CategoryName")) {
                model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), MessageFormatUtil.format("Duplicate name: {}", categoryDto.getCategoryName()));
            } else {
                model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), MessageFormatUtil.format("Unknown exception: {}", exception.getMessage()));
            }
            List<ProductCategoryDto> parentCategories = categoryService.getAllTopLevelCategories().stream().map(productCategoryMapper::toDto).toList();
            addModelAttributes(Boolean.FALSE, categoryDto, parentCategories, model);
            return PRODUCT_CATEGORY_FORM_PAGE.getPageName();
        }
    }

    @GetMapping("/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        ProductCategory category = categoryService.getById(id);
        ProductCategoryDto categoryDto = productCategoryMapper.toDto(category);

        List<ProductCategoryDto> parentCategories = categoryService.getAllParentExcept(id).stream().map(productCategoryMapper::toDto).toList();
        addModelAttributes(!category.getSubCategories().isEmpty(), categoryDto, parentCategories, model);
        return PRODUCT_CATEGORY_FORM_PAGE.getPageName();
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute ProductCategoryDto categoryDto,
                                 Model model,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            ProductCategory category = categoryService.getById(id);
            List<ProductCategoryDto> parentCategories = categoryService.getAllParentExcept(id).stream().map(productCategoryMapper::toDto).toList();
            addModelAttributes(!category.getSubCategories().isEmpty(), categoryDto, parentCategories, model);
            return PRODUCT_CATEGORY_FORM_PAGE.getPageName();
        }
        ProductCategory update = categoryService.update(id, categoryDto);
        redirectAttributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("Category {} updated successfully.", update.getCategoryName()));
        return REDIRECT_URL.getPageName() + PRODUCT_CATEGORY_LIST_PAGE.getPageName();
    }

    @PostMapping("/disable/{id}")
    public String disableCategory(@PathVariable Long id, RedirectAttributes attributes) {
        ProductCategory category = categoryService.getById(id);

        if (!category.getSubCategories().isEmpty()) {
            attributes.addFlashAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), "Cannot disable category with subcategories.");
        } else {
            ProductCategory productCategory = categoryService.disableCategory(id);
            attributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("Category {} disabled successfully.", productCategory.getCategoryName()));
        }
        return REDIRECT_URL.getPageName() + PRODUCT_CATEGORY_LIST_PAGE.getPageName();
    }

    @PostMapping("/enable/{id}")
    public String enableCategory(@PathVariable Long id, RedirectAttributes attributes) {
        ProductCategory productCategory = categoryService.enableCategory(id);
        attributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("Category {} enabled successfully.", productCategory.getCategoryName()));
        return REDIRECT_URL.getPageName() + PRODUCT_CATEGORY_LIST_PAGE.getPageName();
    }

    private void addModelAttributes(boolean hasSubcategories, ProductCategoryDto categoryDto, List<ProductCategoryDto> parentCategories, Model model) {
        model.addAttribute(MODEL_ATTR_PLACEHOLDER_FOR_CATEGORY, categoryDto);
        model.addAttribute(MODEL_ATTR_PLACEHOLDER_FOR_PARENT_CATEGORIES, parentCategories);
        model.addAttribute(MODEL_ATTR_PLACEHOLDER_FOR_HAS_SUB_CATEGORIES, hasSubcategories);
    }
}
