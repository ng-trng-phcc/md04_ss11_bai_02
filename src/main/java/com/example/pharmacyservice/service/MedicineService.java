package com.example.pharmacyservice.service;

import com.example.pharmacyservice.model.dto.MedicineDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MedicineService {

    @Cacheable(value = "medicines", key = "#id")
    public MedicineDto getMedicineById(String id) {
        // Chỉ chạy khi cache MISS (Redis chưa có medicines::<id>).
        // Cache HIT thì Spring trả về luôn, không vào thân hàm -> không truy vấn DB.
        log.info("Cache miss medicines::{} -> truy vấn DB/API", id);
        try {
            // Giả lập DB tốn thời gian (~300ms) để thấy rõ lần 2 nhanh hơn (<10ms)
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return MedicineDto.builder()
                .id(id)
                .name("Thuốc " + id)
                .price(50000)
                .stock(100)
                .build();
    }
}
