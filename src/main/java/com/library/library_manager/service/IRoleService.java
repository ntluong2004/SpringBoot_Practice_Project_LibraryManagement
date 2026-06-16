package com.library.library_manager.service;

import com.library.library_manager.dto.role.RoleRequestDTO;
import com.library.library_manager.dto.role.RoleResponseDTO;
import com.library.library_manager.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRoleService{
    List<RoleResponseDTO> getAll();
    RoleResponseDTO create(RoleRequestDTO roleRequest);
    RoleResponseDTO update(Long roleId, RoleRequestDTO request);
    void delete(Long roleId);
}
