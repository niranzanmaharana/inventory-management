package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.dto.AttributeTypeDto;
import com.niranzan.inventory.management.entity.AttributeType;
import com.niranzan.inventory.management.exceptions.ResourceNotFoundException;
import com.niranzan.inventory.management.repository.AttributeTypeRepository;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttributeTypeService {
    private static final String MSG_ATTRIBUTE_TYPE_NOT_FOUND_WITH_ID = "Attribute not found with id: {}";
    private final AttributeTypeRepository attributeTypeRepository;

    public List<AttributeTypeDto> findAllAttributes() {
        return attributeTypeRepository.findAll().stream().map(this::convert).toList();
    }

    public boolean existsByAttributeNameIgnoreCase(String attributeName) {
        return attributeTypeRepository.existsByAttributeNameIgnoreCase(attributeName);
    }

    public AttributeTypeDto save(AttributeTypeDto attributeTypeDto) {
        AttributeType savedAttributeType = attributeTypeRepository.save(convert(attributeTypeDto));
        return convert(savedAttributeType);
    }

    public AttributeTypeDto update(AttributeTypeDto attributeTypeDto) {
        AttributeType attributeType = attributeTypeRepository.findById(attributeTypeDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_ATTRIBUTE_TYPE_NOT_FOUND_WITH_ID, attributeTypeDto.getId().toString())));
        attributeType.setAttributeName(attributeTypeDto.getAttributeName());
        attributeType.setDataType(attributeTypeDto.getDataType());
        AttributeType savedAttributeType = attributeTypeRepository.save(attributeType);
        return convert(savedAttributeType);
    }

    public List<AttributeType> findAll() {
        return attributeTypeRepository.findAll();
    }

    public AttributeTypeDto findById(Long id) {
        return attributeTypeRepository.findById(id)
                .map(this::convert)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_ATTRIBUTE_TYPE_NOT_FOUND_WITH_ID, id.toString())));
    }

    public AttributeTypeDto updateStatus(Long id, boolean shouldEnable) {
        AttributeType attributeType = attributeTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_ATTRIBUTE_TYPE_NOT_FOUND_WITH_ID, id.toString())));
        attributeType.setEnabled(shouldEnable);
        AttributeType saved = attributeTypeRepository.save(attributeType);
        return convert(saved);
    }

    private AttributeType convert(AttributeTypeDto attributeTypeDto) {
        AttributeType attributeType = new AttributeType();
        attributeType.setId(attributeTypeDto.getId());
        attributeType.setAttributeName(attributeTypeDto.getAttributeName());
        attributeType.setDataType(attributeTypeDto.getDataType());
        return attributeType;
    }

    private AttributeTypeDto convert(AttributeType attr) {
        AttributeTypeDto dto = new AttributeTypeDto();
        dto.setId(attr.getId());
        dto.setAttributeName(attr.getAttributeName());
        dto.setEnabled(attr.isEnabled());
        dto.setDataType(attr.getDataType());
        return dto;
    }
}
