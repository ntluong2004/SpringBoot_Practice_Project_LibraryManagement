package com.library.library_manager.controller;

import com.library.library_manager.dto.ApiResponse;
import com.library.library_manager.dto.role.RoleRequestDTO;
import com.library.library_manager.dto.role.RoleResponseDTO;
import com.library.library_manager.entity.Role;
import com.library.library_manager.service.IRoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import com.library.library_manager.dto.staff.StaffResponseDTO;
import com.library.library_manager.entity.Staff;
import com.library.library_manager.service.impl.StaffService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@RestController
@RequestMapping("/api/role")
public class RoleController {

    IRoleService roleService;

    @GetMapping
     @PreAuthorize("hasAnyRole('ADMIN')") // CHỐT CHẶN: Chỉ Token có ROLE_ADMIN mới lấy được list này
    public ApiResponse<List<RoleResponseDTO>> getAll() {
        return ApiResponse.<List<RoleResponseDTO>>builder()
                .data(roleService.getAll())
                .build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<RoleResponseDTO> create(@RequestBody RoleRequestDTO role) {
        return ResponseEntity.ok(roleService.create(role));
    }

    @PutMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin mới được sửa quyền
    public ApiResponse<RoleResponseDTO> update(@PathVariable Long roleId, @RequestBody RoleRequestDTO request) {
        return ApiResponse.<RoleResponseDTO>builder()
                .data(roleService.update(roleId, request))
                .build();
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin mới có quyền xóa sinh tử này
    public ApiResponse<String> delete(@PathVariable Long roleId) {
        roleService.delete(roleId);
        return ApiResponse.<String>builder()
                .data("Xóa vai trò thành công!")
                .build();
    }
}
