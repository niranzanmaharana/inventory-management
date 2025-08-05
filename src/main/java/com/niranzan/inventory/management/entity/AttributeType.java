package com.niranzan.inventory.management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "attribute_type", uniqueConstraints = {
        @UniqueConstraint(columnNames = "attribute_name", name = "UK_ATTRIBUTE_NAME")
})
public class AttributeType extends BaseEntity {

    private String attributeName;
    private String dataType;

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
