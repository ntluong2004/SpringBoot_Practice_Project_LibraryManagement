package com.library.library_manager.repository;

import com.library.library_manager.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface IPermissionRepository extends JpaRepository<Permission,Long> {
}
