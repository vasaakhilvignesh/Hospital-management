package com.hospital.billingservice.repository;

import com.hospital.billingservice.entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillItemRepository extends JpaRepository<BillItem, Long> {
    List<BillItem> findByBillId(Long billId);
}
