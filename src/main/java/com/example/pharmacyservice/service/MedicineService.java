package com.example.pharmacyservice.service;

import com.example.pharmacyservice.model.dto.MedicineDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class MedicineService {

    // Giả lập bảng thuốc trong DB (thay bằng JpaRepository khi có entity thật).
    private final Map<String, MedicineDto> database = new ConcurrentHashMap<>();

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
        // Đọc từ DB; nếu chưa có thì tạo bản ghi mặc định (như SELECT ... hoặc INSERT seed)
        return database.computeIfAbsent(id, key -> MedicineDto.builder()
                .id(key)
                .name("Thuốc " + key)
                .price(50000)
                .stock(100)
                .build());
    }

    @CacheEvict(value = "medicines", key = "#medicine.id")
    public MedicineDto updateMedicine(MedicineDto medicine) {
        // Ghi giá mới xuống DB rồi xóa key medicines::<id> trong Redis ngay lập tức,
        // lần GET sau sẽ cache MISS và nạp lại giá mới nhất.
        log.info("Update DB + evict cache medicines::{}", medicine.getId());
        database.put(medicine.getId(), medicine);
        return medicine;
    }
}
