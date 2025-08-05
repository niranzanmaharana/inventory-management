package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.dto.AttributeTypeDto;
import com.niranzan.inventory.management.enums.AppMessageParameter;
import com.niranzan.inventory.management.service.AttributeTypeService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.niranzan.inventory.management.enums.AppPages.ATTRIBUTE_TYPE_FORM_PATH;
import static com.niranzan.inventory.management.enums.AppPages.ATTRIBUTE_TYPE_LIST_PATH;
import static com.niranzan.inventory.management.enums.AppPages.REDIRECT_URL;

@Controller
@RequestMapping("/attribute-type")
@RequiredArgsConstructor
public class AttributeTypeController {
    public static final String MSG_ATTRIBUTE_TYPE_STATUS_UPDATED_SUCCESSFULLY = "Attribute type {} status updated successfully.";
    private final AttributeTypeService attributeTypeService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @GetMapping("/attribute-type-list")
    public String listAttributeTypes(Model model) {
        List<AttributeTypeDto> attributeTypes = attributeTypeService.findAllAttributes();
        model.addAttribute("attributeTypes", attributeTypes);
        return ATTRIBUTE_TYPE_LIST_PATH.getPath();
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/add")
    public String addAttributeForm(Model model) {
        model.addAttribute("attributeType", new AttributeTypeDto());
        return ATTRIBUTE_TYPE_FORM_PATH.getPath();
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping("/save")
    public String saveAttributeType(@Valid @ModelAttribute("attributeType") AttributeTypeDto attributeTypeDto, BindingResult result, Model model, RedirectAttributes attributes) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("attributeType", attributeTypeDto);
                return ATTRIBUTE_TYPE_FORM_PATH.getPath();
            }

            AttributeTypeDto savedAttributeType = attributeTypeService.save(attributeTypeDto);
            attributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("Attribute type {} saved", savedAttributeType.getAttributeName()));
            return REDIRECT_URL.getPath() + ATTRIBUTE_TYPE_LIST_PATH.getPath();
        } catch (DataIntegrityViolationException exception) {
            if (exception.getMessage().contains("UK_AttributeType_AttributeName")) {
                model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), MessageFormatUtil.format("Duplicate name: {}", attributeTypeDto.getAttributeName()));
            } else {
                model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), MessageFormatUtil.format("Unknown exception: {}", exception.getMessage()));
            }

            return ATTRIBUTE_TYPE_FORM_PATH.getPath();
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping("/update")
    public String updateAttributeType(@Valid @ModelAttribute("attributeType") AttributeTypeDto attributeTypeDto, BindingResult result, Model model, RedirectAttributes attributes) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("attributeType", attributeTypeDto);
                return ATTRIBUTE_TYPE_FORM_PATH.getPath();
            }

            AttributeTypeDto savedAttributeType = attributeTypeService.update(attributeTypeDto);
            attributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format("Attribute type {} updated", savedAttributeType.getAttributeName()));
            return REDIRECT_URL.getPath() + ATTRIBUTE_TYPE_LIST_PATH.getPath();
        } catch (DataIntegrityViolationException exception) {
            if (exception.getMessage().contains("UK_AttributeType_AttributeName")) {
                model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), MessageFormatUtil.format("Duplicate name: {}", attributeTypeDto.getAttributeName()));
            } else {
                model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), MessageFormatUtil.format("Unknown exception: {}", exception.getMessage()));
            }

            return ATTRIBUTE_TYPE_FORM_PATH.getPath();
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/edit/{id}")
    public String editAttributeType(@PathVariable Long id, Model model) {
        AttributeTypeDto attributeType = attributeTypeService.findById(id);
        model.addAttribute("attributeType", attributeType);
        return ATTRIBUTE_TYPE_FORM_PATH.getPath();
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping("/disable/{id}")
    public String disableAttributeType(@PathVariable Long id, RedirectAttributes attributes) {
        AttributeTypeDto dto = attributeTypeService.updateStatus(id, Boolean.FALSE);
        attributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format(MSG_ATTRIBUTE_TYPE_STATUS_UPDATED_SUCCESSFULLY, dto.getAttributeName()));
        return REDIRECT_URL.getPath() + ATTRIBUTE_TYPE_LIST_PATH.getPath();
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping("/enable/{id}")
    public String enableAttributeType(@PathVariable Long id, RedirectAttributes attributes) {
        AttributeTypeDto dto = attributeTypeService.updateStatus(id, Boolean.TRUE);
        attributes.addFlashAttribute(AppMessageParameter.SUCCESS_PARAM_NM.getName(), MessageFormatUtil.format(MSG_ATTRIBUTE_TYPE_STATUS_UPDATED_SUCCESSFULLY, dto.getAttributeName()));
        return REDIRECT_URL.getPath() + ATTRIBUTE_TYPE_LIST_PATH.getPath();
    }
}
