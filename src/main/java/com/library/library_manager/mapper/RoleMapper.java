package com.library.library_manager.mapper;

import com.library.library_manager.dto.role.RoleRequestDTO;
import com.library.library_manager.dto.role.RoleResponseDTO;
import com.library.library_manager.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role roleRequestDTOtoRole(RoleRequestDTO roleRequestDTO);
    RoleResponseDTO roleToRoleResponseDTO(Role role);

    // MapStruct tự hiểu cách biến 1 List Entity thành 1 List DTO
    List<RoleResponseDTO> toRoleResponseList(List<Role> roles);
}
