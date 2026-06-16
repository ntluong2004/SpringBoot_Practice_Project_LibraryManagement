package com.library.library_manager.service.impl;

import com.library.library_manager.dto.role.RoleRequestDTO;
import com.library.library_manager.dto.role.RoleResponseDTO;
import com.library.library_manager.entity.Role;
import com.library.library_manager.mapper.RoleMapper;
import com.library.library_manager.repository.IPermissionRepository;
import com.library.library_manager.repository.IRoleRepository;
import com.library.library_manager.service.IRoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService implements IRoleService {

    IPermissionRepository permissionRepository;
    IRoleRepository roleRepository;
    RoleMapper roleMapper;


    @Override
    public List<RoleResponseDTO> getAll() {
        // 1. Gọi repository lấy hết sạch Role trong DB lên
        List<Role> roles = roleRepository.findAll();

        // 2. Đi qua bộ chuyển đổi MapStruct để trả về dữ liệu chuẩn
        return roleMapper.toRoleResponseList(roles);
    }

    @Override
    public RoleResponseDTO create(RoleRequestDTO roleRequest) {
        // 1. Tạo đối tượng Role từ Request
        Role role = Role.builder()
                .roleName(roleRequest.getRoleName())
                .build();

        // 2. Tìm các Permission trong DB dựa trên danh sách ID gửi lên
        var permissions = permissionRepository.findAllById(roleRequest.getPermission_id());

        // 3. Gán danh sách quyền vào Role
        role.setPermissions(new  HashSet<>(permissions));

        // 4. Lưu vào Database
        role = roleRepository.save(role);

        // 5. Trả về Response (Bạn có thể viết hàm map riêng để tái sử dụng)
        return roleMapper.roleToRoleResponseDTO(role);
}
    @Override
    public RoleResponseDTO update(Long roleId, RoleRequestDTO request) {
        // 1. Tìm Role cũ trong DB, không thấy thì báo lỗi
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò này"));

        // 2. Cập nhật tên vai trò mới
        role.setRoleName(request.getRoleName());

        // 3. Lấy và cập nhật lại danh sách quyền mới (SỬA LẠI ĐOẠN NÀY)
        // Giả sử DTO của bạn dùng getPermission_id() như code bạn gửi:
        var permissions = permissionRepository.findAllById(request.getPermission_id());

        // Đổ trực tiếp List permissions vào HashSet một cách tự nhiên
        role.setPermissions(new HashSet<>(permissions));

        // 4. Lưu và trả về kết quả qua Mapper
        return roleMapper.roleToRoleResponseDTO(roleRepository.save(role));
    }

    @Override
    public void delete(Long roleId) {
        // Kiểm tra xem Role có tồn tại không trước khi xóa
        if (!roleRepository.existsById(roleId)) {
            throw new RuntimeException("Vai trò không tồn tại để xóa");
        }
        roleRepository.deleteById(roleId);
    }
}
