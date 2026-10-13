package com.hospital.billingservice.dto;

import com.hospital.billingservice.entity.ItemType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BillItemRequest {

    @NotNull(message = "Item type is required")
    private ItemType itemType;

    @NotBlank(message = "Description is required")
    private String description;

    private String referenceId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Unit price cannot be negative")
    private BigDecimal unitPrice;

    public BillItemRequest() {}

    public BillItemRequest(ItemType itemType, String description, String referenceId, Integer quantity, BigDecimal unitPrice) {
        this.itemType = itemType;
        this.description = description;
        this.referenceId = referenceId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

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
}
