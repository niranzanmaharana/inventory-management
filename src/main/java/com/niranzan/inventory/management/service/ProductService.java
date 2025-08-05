package com.niranzan.inventory.management.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niranzan.inventory.management.dto.ProductDto;
import com.niranzan.inventory.management.entity.AttributeType;
import com.niranzan.inventory.management.entity.ProductAttribute;
import com.niranzan.inventory.management.entity.ProductCategory;
import com.niranzan.inventory.management.entity.ProductItem;
import com.niranzan.inventory.management.enums.ImageFileType;
import com.niranzan.inventory.management.exceptions.GenericException;
import com.niranzan.inventory.management.exceptions.InvalidFormDataException;
import com.niranzan.inventory.management.exceptions.ResourceNotFoundException;
import com.niranzan.inventory.management.exceptions.UnableToSaveFileException;
import com.niranzan.inventory.management.mapper.ProductMapper;
import com.niranzan.inventory.management.repository.AttributeTypeRepository;
import com.niranzan.inventory.management.repository.CategoryRepository;
import com.niranzan.inventory.management.repository.ProductRepository;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import com.niranzan.inventory.management.view.response.ProductResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintDeclarationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.niranzan.inventory.management.config.AppMessage.MSG_CATEGORY_NOT_FOUND_WITH_ID;
import static com.niranzan.inventory.management.config.AppMessage.MSG_INVALID_ATTRIBUTES_EXCEPTION;
import static com.niranzan.inventory.management.config.AppMessage.MSG_PRODUCT_NOT_FOUND_WITH_ID;
import static com.niranzan.inventory.management.config.AppMessage.MSG_UNKNOWN_EXCEPTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final AttributeTypeRepository attributeTypeRepository;
    private final ObjectMapper objectMapper;

    @Value("${upload-path.base}")
    private String uploadFolderPath;
    @Value("${upload-path.temp}")
    private String temporaryFolderPath;
    @Value("${upload-path.invoices}")
    private String invoiceFolderPath;
    @Value("${upload-path.images}")
    private String imagesFolderPath;
    private final String FOLDER_SEPARATOR = "/";

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream().map(product -> {
            ProductResponse response = new ProductResponse();
            response.setId(product.getId());
            response.setProductName(product.getProductName());
            response.setDescription(product.getDescription());
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

    @Transactional
    public ProductDto saveProduct(ProductDto productDto, String attributeJson) {
        ProductItem product = Objects.isNull(productDto.getId()) ? new ProductItem()
                : productRepository.findById(productDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_PRODUCT_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(productDto.getId()))));
        ProductCategory subCategory = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_CATEGORY_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(productDto.getCategoryId()))));

        try {
            Map<Long, String> productAttributes = objectMapper.readValue(attributeJson, new TypeReference<>() {
            });
            productDto.setProductAttributes(productAttributes);
            product.setProductName(productDto.getProductName());
            product.setDescription(productDto.getDescription());
            product.setCategory(subCategory);
            if (Objects.isNull(product.getProductCode())) {
                product.setProductCode(generateProductCode(product.getProductName()));
            }
            setupProductAttributes(productDto, product);
            ProductItem savedProduct = productRepository.save(product);

            return productMapper.toDto(savedProduct);
        } catch (JsonProcessingException exception) {
            throw new InvalidFormDataException(MessageFormatUtil.format(MSG_INVALID_ATTRIBUTES_EXCEPTION.getMessage(), exception.getMessage()));
        } catch (DataIntegrityViolationException | ConstraintDeclarationException exception) {
            throw new InvalidFormDataException(extractException(exception));
        } catch (Exception e) {
            throw new GenericException(MessageFormatUtil.format(MSG_UNKNOWN_EXCEPTION.getMessage(), e.getMessage()));
        }
    }

    private void uploadAndSetImageUrl(ProductItem product, MultipartFile file, ImageFileType imageFileType) {
        if (file != null && !file.isEmpty()) {
            try {
                // Determine subdirectory based on image type
                String subFolder = imageFileType == ImageFileType.RECEIPT_PHOTO ? invoiceFolderPath : imagesFolderPath;

                Path baseUploadDir = Paths.get(uploadFolderPath, subFolder);
                Files.createDirectories(baseUploadDir);

                // Generate unique filename
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = baseUploadDir.resolve(fileName);

                // Save the file
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Set relative path in entity
                String relativePath = FOLDER_SEPARATOR + subFolder + FOLDER_SEPARATOR + fileName;
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

    private String uploadTemporaryImage(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                String subFolder = temporaryFolderPath;

                Path baseUploadDir = Paths.get(uploadFolderPath, subFolder);
                Files.createDirectories(baseUploadDir);

                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = baseUploadDir.resolve(fileName);

                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                return FOLDER_SEPARATOR + subFolder + FOLDER_SEPARATOR + fileName;
            } catch (IOException e) {
                throw new UnableToSaveFileException("Failed to save image: " + e.getMessage());
            }
        }
        return StringUtils.EMPTY;
    }

    private void setupProductAttributes(ProductDto productDto, ProductItem product) {
        if (productDto.getProductAttributes() == null) {
            product.getAttributes().clear();
            return;
        }

        // Get existing attributes (same instance)
        List<ProductAttribute> existingAttributes = product.getAttributes();
        existingAttributes.clear();

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
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_PRODUCT_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(productId))));
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
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_PRODUCT_NOT_FOUND_WITH_ID.getMessage(), id.toString())));
    }

    public String uploadImage(Long productId, String imageType, MultipartFile file) {
        if (Objects.isNull(productId)) {
            return uploadTemporaryImage(file);
        }
        ImageFileType imageFileType = ImageFileType.valueOf(imageType);

        ProductItem product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_PRODUCT_NOT_FOUND_WITH_ID.getMessage(), productId.toString())));

        // Delete existing image if present
        deleteIfImageExist(product, imageFileType);

        uploadAndSetImageUrl(product, file, ImageFileType.valueOf(imageType));
        product = productRepository.save(product);
        return product.getProductImagePath();
    }

    private void deleteIfImageExist(ProductItem product, ImageFileType imageFileType) {
        String relativePath = imageFileType == ImageFileType.RECEIPT_PHOTO ? product.getInvoiceImagePath() : product.getProductImagePath();
        try {
            if (!StringUtils.isBlank(relativePath)) {
                Path filePath = Paths.get(uploadFolderPath, relativePath.replaceFirst("^/", ""));
                File imageFile = filePath.toFile();

                if (imageFile.exists() && imageFile.isFile()) {
                    boolean deleted = imageFile.delete();
                    if (!deleted) {
                        log.warn("Failed to delete image file: {}", imageFile.getAbsolutePath());
                    } else {
                        log.info("Deleted existing image: {}", imageFile.getAbsolutePath());
                    }
                } else {
                    log.warn("Image file not found: {}", imageFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            log.error("Error while deleting image file for path: {}", relativePath, e);
        }
    }

    private String extractException(Exception ex) {
        String message = ex.getMessage();
        if (message.contains("Duplicate entry")) {
            if (message.contains("UK_ProductItem_ProductName")) {
                return "Product name already exists.";
            }
        }
        return "A data integrity violation occurred.: " + ex.getMessage();
    }

    public String generateProductCode(String productName) {
        String cleanedProductName = productName != null
                ? productName.replaceAll("\\s+", "").toUpperCase()
                : "PROD";

        String prefix = cleanedProductName.length() >= 4
                ? cleanedProductName.substring(0, 4)
                : cleanedProductName;

        Long maxId = productRepository.findMaxId();
        long nextId = (maxId != null ? maxId + 1 : 1);

        return String.format("%s-%04d", prefix, nextId);
    }
}
