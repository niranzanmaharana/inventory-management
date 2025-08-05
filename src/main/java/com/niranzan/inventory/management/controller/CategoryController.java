package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.dto.AttributeTypeDto;
import com.niranzan.inventory.management.dto.CategoryDto;
import com.niranzan.inventory.management.entity.ProductCategory;
import com.niranzan.inventory.management.enums.AppMessageParameter;
import com.niranzan.inventory.management.exceptions.ResourceNotFoundException;
import com.niranzan.inventory.management.mapper.ProductCategoryMapper;
import com.niranzan.inventory.management.service.CategoryService;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.niranzan.inventory.management.enums.AppPages.PRODUCT_CATEGORY_FORM_PATH;
import static com.niranzan.inventory.management.enums.AppPages.PRODUCT_CATEGORY_LIST_PATH;
import static com.niranzan.inventory.management.enums.AppPages.REDIRECT_URL;

@Controller
@RequestMapping("/product-category")
@RequiredArgsConstructor
public class CategoryController extends BaseController {
    public static final String MODEL_ATTR_PLACEHOLDER_FOR_CATEGORY = "category";
    public static final String MODEL_ATTR_PLACEHOLDER_FOR_CATEGORIES = "categories";
    public static final String MODEL_ATTR_PLACEHOLDER_FOR_HAS_SUB_CATEGORIES = "hasSubCategories";
    private final CategoryService categoryService;
    private final ProductCategoryMapper productCategoryMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @GetMapping("/category-list")
    public String listCategories(Model model) {
        List<CategoryDto> categoryTree = categoryService.getCategoryTree();
        model.addAttribute("categoryTree", categoryTree);
        return PRODUCT_CATEGORY_LIST_PATH.getPath();
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/add")
    public String showAddForm(Model model, @RequestParam(name = "parentId", required = false) Long parentId) {
        CategoryDto categoryDto = new CategoryDto();
        if (parentId != null) {
            categoryDto.setParentId(parentId);
        }
        addModelAttributes(Boolean.FALSE, categoryDto, model);
        return PRODUCT_CATEGORY_FORM_PATH.getPath();
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping("/save")
    public String saveCategory(@Valid @ModelAttribute("category") CategoryDto categoryDto, BindingResult result, Model model, RedirectAttributes attributes) {
        try {
            if (result.hasErrors()) {
                addModelAttributes(Boolean.FALSE, categoryDto, model);
                return PRODUCT_CATEGORY_FORM_PATH.getPath();
            }

            ProductCategory productCategory = categoryService.saveCategory(categoryDto);
            attributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("Product category {} added", productCategory.getCategoryName()));
            return REDIRECT_URL.getPath() + PRODUCT_CATEGORY_LIST_PATH.getPath();
        } catch (DataIntegrityViolationException exception) {
            if (exception.getMessage().contains("UK_ProductCategory_CategoryName")) {
                model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), MessageFormatUtil.format("Duplicate name: {}", categoryDto.getCategoryName()));
            } else {
                model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), MessageFormatUtil.format("Unknown exception: {}", exception.getMessage()));
            }
            addModelAttributes(Boolean.FALSE, categoryDto, model);
            return PRODUCT_CATEGORY_FORM_PATH.getPath();
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        ProductCategory category = categoryService.getById(id);
        CategoryDto categoryDto = productCategoryMapper.toDto(category);

        addModelAttributes(!category.getSubCategories().isEmpty(), categoryDto, model);
        return PRODUCT_CATEGORY_FORM_PATH.getPath();
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute CategoryDto categoryDto,
                                 Model model,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            ProductCategory category = categoryService.getById(id);
            addModelAttributes(!category.getSubCategories().isEmpty(), categoryDto, model);
            return PRODUCT_CATEGORY_FORM_PATH.getPath();
        }
        ProductCategory update = categoryService.update(id, categoryDto);
        redirectAttributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("Category {} updated successfully.", update.getCategoryName()));
        return REDIRECT_URL.getPath() + PRODUCT_CATEGORY_LIST_PATH.getPath();
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            CategoryDto category = categoryService.toggleStatus(id);
            attributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("{} status updated successfully.", category.getCategoryName()));
        } catch (ResourceNotFoundException exception) {
            attributes.addFlashAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), exception.getMessage());
        }
        return REDIRECT_URL.getPath() + PRODUCT_CATEGORY_LIST_PATH.getPath();
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/{categoryId}/subcategories")
    @ResponseBody
    public List<CategoryDto> findSubCategories(@PathVariable Long categoryId) {
        return categoryService.findSubCategories(categoryId);
    }

    @PreAuthorize("hasAnyRole('MANAGER'")
    @GetMapping("/attributes/{subCategoryId}")
    @ResponseBody
    public List<AttributeTypeDto> getAttributesForSubCategory(@PathVariable Long subCategoryId) {
        return categoryService.getAttributesForSubCategory(subCategoryId);
    }

    private void addModelAttributes(boolean hasSubcategories, CategoryDto categoryDto, Model model) {
        Long currentCategoryId = categoryDto.getId();
        List<CategoryDto> categories = (currentCategoryId != null)
                ? categoryService.findAllExcept(List.of(currentCategoryId))
                : categoryService.findAll();

        model.addAttribute(MODEL_ATTR_PLACEHOLDER_FOR_CATEGORIES, categories);
        model.addAttribute(MODEL_ATTR_PLACEHOLDER_FOR_CATEGORY, categoryDto);
        model.addAttribute(MODEL_ATTR_PLACEHOLDER_FOR_HAS_SUB_CATEGORIES, hasSubcategories);
    }
}
