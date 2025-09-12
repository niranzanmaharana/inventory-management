package com.niranzan.inventory.management.entity;

import com.niranzan.inventory.management.enums.PaymentMethod;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_transaction", uniqueConstraints = {
        @UniqueConstraint(columnNames = "invoice_number", name = "UK_INVOICE_NUMBER"),
        @UniqueConstraint(columnNames = "order_number", name = "UK_ORDER_NUMBER")
})
@Getter
@Setter
public class SalesTransaction extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_SALES_TX_X_CUSTOMER")
    )
    private Customer customer;

    private LocalDateTime transactionDateTime;

    private BigDecimal totalAmount;

    private BigDecimal totalTax;

    private BigDecimal totalDiscount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private boolean paid;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @Column(unique = true, nullable = false)
    private String invoiceNumber;

    @OneToMany(mappedBy = "salesTransaction", cascade = CascadeType.ALL)
    private List<SalesTransactionItem> items = new ArrayList<>();
}
