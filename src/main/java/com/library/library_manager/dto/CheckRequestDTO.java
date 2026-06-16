package com.library.library_manager.dto;

import lombok.Data;

@Data
public class CheckRequestDTO {
    String studentCode;
    Long copyId;
    String barcode;
}
