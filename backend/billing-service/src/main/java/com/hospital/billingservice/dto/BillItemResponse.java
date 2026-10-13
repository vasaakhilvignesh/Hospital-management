package com.hospital.billingservice.dto;

import com.hospital.billingservice.entity.BillItem;
import com.hospital.billingservice.entity.ItemType;
import java.math.BigDecimal;

public class BillItemResponse {

    private Long id;
    private ItemType itemType;
    private String description;
    private String referenceId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;

    public BillItemResponse() {}

    public static BillItemResponse fromEntity(BillItem item) {
        BillItemResponse res = new BillItemResponse();
        res.setId(item.getId());
        res.setItemType(item.getItemType());
        res.setDescription(item.getDescription());
        res.setReferenceId(item.getReferenceId());
        res.setQuantity(item.getQuantity());
        res.setUnitPrice(item.getUnitPrice());
        res.setAmount(item.getAmount());
        return res;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
