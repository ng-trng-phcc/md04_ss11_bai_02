package com.example.pharmacyservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Chi tiết thuốc. Dùng class cụ thể (thay vì Map) để Redis JSON serializer
 * typed (JacksonJsonRedisSerializer) đọc/ghi đối xứng, không cần @class type info.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineDto implements Serializable {
    private String id;
    private String name;
    private int price;
    private int stock;
}
