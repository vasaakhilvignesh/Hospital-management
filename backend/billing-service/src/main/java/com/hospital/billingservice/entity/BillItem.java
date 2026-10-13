package com.hospital.billingservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bill_items")
public class BillItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    @JsonBackReference
    private Bill bill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ItemType itemType;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(length = 100)
    private String referenceId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    public BillItem() {}

    public BillItem(Bill bill, ItemType itemType, String description, String referenceId,
                    Integer quantity, BigDecimal unitPrice, BigDecimal amount) {
        this.bill = bill;
        this.itemType = itemType;
        this.description = description;
        this.referenceId = referenceId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Bill getBill() { return bill; }
    public void setBill(Bill bill) { this.bill = bill; }

    public ItemType getItemType() { return itemType; }
    public void setItemType(ItemType itemType) { this.itemType = itemType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
