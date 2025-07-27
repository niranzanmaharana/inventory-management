package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.dto.ProductDto;
import com.niranzan.inventory.management.entity.AttributeType;
import com.niranzan.inventory.management.entity.ProductAttribute;
import com.niranzan.inventory.management.entity.ProductCategory;
import com.niranzan.inventory.management.entity.ProductItem;
import com.niranzan.inventory.management.enums.ImageFileType;
import com.niranzan.inventory.management.exceptions.ResourceNotFoundException;
import com.niranzan.inventory.management.exceptions.UnableToSaveFileException;
import com.niranzan.inventory.management.mapper.ProductMapper;
import com.niranzan.inventory.management.repository.AttributeTypeRepository;
import com.niranzan.inventory.management.repository.CategoryRepository;
import com.niranzan.inventory.management.repository.ProductRepository;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import com.niranzan.inventory.management.view.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private static final String MSG_PRODUCT_NOT_FOUND_WITH_ID = "Product not found with id: {}";
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final AttributeTypeRepository attributeTypeRepository;

    @Value("${upload-path.invoices}")
    private String invoicePath;
    @Value("${upload-path.images}")
    private String productImagePath;

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream().map(product -> {
            ProductResponse response = new ProductResponse();
            response.setId(product.getId());
            response.setProductName(product.getProductName());
            response.setDescription(product.getDescription());
            response.setAvailableQty(product.getQuantity());
            response.setPrice(product.getPricePerUnit());
            response.setInvoiceImagePath(product.getInvoiceImagePath());
            response.setProductImagePath(product.getProductImagePath());
            response.setEnabled(product.isEnabled());
            response.setCategoryName(product.getCategory().getCategoryName());
            ProductCategory parentCategory = product.getCategory().getParent();
            response.setSubCategoryName(parentCategory != null ? parentCategory.getCategoryName() : StringUtils.EMPTY);
            return response;
        }).toList();
    }

    public List<ProductDto> findByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(productMapper::toDto).toList();
    }

    public ProductDto saveProduct(ProductDto productDto, MultipartFile productImage) {
        ProductItem product = Objects.isNull(productDto.getId()) ? new ProductItem()
                : productRepository.findById(productDto.getId()).orElseThrow(() -> new RuntimeException(MessageFormatUtil.format(MSG_PRODUCT_NOT_FOUND_WITH_ID, String.valueOf(productDto.getId()))));

        product.setProductName(productDto.getProductName());
        product.setDescription(productDto.getDescription());
        product.setQuantity(productDto.getQuantity());
        product.setPricePerUnit(productDto.getPricePerUnit());
        product.setExpiryDate(productDto.getExpiryDate());

        // Load category
        ProductCategory subCategory = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("SubCategory not found"));
        product.setCategory(subCategory);

        // Save image if uploaded
        uploadAndSetImageUrl(product, productImage, ImageFileType.PRODUCT_PHOTO);

        // Prepare ProductAttributes
        setupProductAttributes(productDto, product);

        ProductItem savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    private void uploadAndSetImageUrl(ProductItem product, MultipartFile file, ImageFileType imageFileType) {
        if (file != null && !file.isEmpty()) {
            try {
                // Determine subdirectory based on image type
                String subFolder = imageFileType == ImageFileType.RECEIPT_PHOTO ? "invoices" : "images";

                // Base upload folder = ./uploads/
                Path baseUploadDir = Paths.get("uploads", subFolder); // resolves to "uploads/invoices" or "uploads/images"
                Files.createDirectories(baseUploadDir); // ensure directory exists

                // Generate unique filename
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = baseUploadDir.resolve(fileName);

                // Save the file
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Print absolute and relative paths
                System.out.println("Absolute Path: " + filePath.toAbsolutePath());
                System.out.println("Relative Web Path: /uploads/" + subFolder + "/" + fileName);

                // Set relative path in entity
                String relativePath = "/" + subFolder + "/" + fileName;
                if (imageFileType == ImageFileType.RECEIPT_PHOTO) {
                    product.setInvoiceImagePath(relativePath);
                } else if (imageFileType == ImageFileType.PRODUCT_PHOTO) {
                    product.setProductImagePath(relativePath);
                }
            } catch (IOException e) {
                throw new UnableToSaveFileException("Failed to save image: " + e.getMessage());
            }
        }
    }

    private void setupProductAttributes(ProductDto productDto, ProductItem product) {
        if (productDto.getProductAttributes() == null) {
            product.getAttributes().clear(); // clear all if no attributes passed
            return;
        }

        // Get existing attributes (same instance)
        Set<ProductAttribute> existingAttributes = product.getAttributes();
        existingAttributes.clear(); // clear existing entries

        for (Map.Entry<Long, String> entry : productDto.getProductAttributes().entrySet()) {
            Long attrTypeId = entry.getKey();
            String value = entry.getValue();

            AttributeType type = attributeTypeRepository.findById(attrTypeId)
                    .orElseThrow(() -> new RuntimeException("AttributeType not found: " + attrTypeId));

            ProductAttribute attribute = new ProductAttribute();
            attribute.setAttributeType(type);
            attribute.setAttributeValue(value);
            attribute.setProduct(product);

            existingAttributes.add(attribute);
        }
    }

    public ProductDto prepareDataForEdit(Long productId) {
        ProductItem productItem = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format("Product not found with id: {}", String.valueOf(productId))));
        ProductDto productDto = productMapper.toDto(productItem);
        productDto.setCategoryId(productItem.getCategory().getId());
        Map<Long, String> attributeValues = productItem.getAttributes().stream()
                .collect(Collectors.toMap(
                        attr -> attr.getAttributeType().getId(),
                        ProductAttribute::getAttributeValue
                ));
        productDto.setProductAttributes(attributeValues);
        return productDto;
    }

    public ProductDto findById(Long productId) {
        return productRepository.findById(productId)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format("Product not found with id: {}", String.valueOf(productId))));
    }

    public boolean productExistsById(Long productId) {
        return productRepository.existsById(productId);
    }

    public ProductDto changeStatus(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setEnabled(!product.isEnabled());
                    product = productRepository.save(product);
                    return productMapper.toDto(product);
                })
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_PRODUCT_NOT_FOUND_WITH_ID, id.toString())));
    }
}
