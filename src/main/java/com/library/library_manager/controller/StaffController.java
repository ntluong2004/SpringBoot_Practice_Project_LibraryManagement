package com.library.library_manager.controller;

import com.library.library_manager.dto.ApiResponse;
import com.library.library_manager.dto.staff.StaffRequestDTO;
import com.library.library_manager.dto.staff.StaffResponseDTO;
import com.library.library_manager.entity.Staff;
import com.library.library_manager.service.impl.StaffService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@RestController
@RequestMapping("/api/staff")
public class StaffController {

    StaffService staffService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin mới có quyền xóa sinh tử này
    public List<StaffResponseDTO> getStaffList() {
        return staffService.findAll();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StaffResponseDTO>> createStaff(@RequestBody @Valid StaffRequestDTO requestDTO) {
        return ResponseEntity.ok(
                ApiResponse.<StaffResponseDTO>builder()
                        .data(staffService.createStaff(requestDTO)) // <-- Gọi hàm TẠO MỚI (create), không truyền id
                        .build()
        );
    }

    // PUT: Cập nhật theo staffId
    @PutMapping("/{staffId}")
    public ResponseEntity<StaffResponseDTO> updateStaff(@PathVariable Long staffId, @RequestBody @Valid StaffRequestDTO requestDTO) {
        return ResponseEntity.ok(staffService.updateStaff(staffId, requestDTO));
    }

    // DELETE: Xóa theo staffId
    @DeleteMapping("/{staffId}")
    public ResponseEntity<String> deleteStaff(@PathVariable Long staffId) {
        staffService.deleteStaff(staffId);
        return ResponseEntity.ok("Deleted staff with id: " + staffId); //khng cần trả về
    }
}