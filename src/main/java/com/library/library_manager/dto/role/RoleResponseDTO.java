package com.library.library_manager.dto.role;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponseDTO {
    Long id;
    String roleName;
}