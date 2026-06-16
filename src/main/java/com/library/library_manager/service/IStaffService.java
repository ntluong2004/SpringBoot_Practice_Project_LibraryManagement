package com.library.library_manager.service;

import com.library.library_manager.dto.staff.StaffRequestDTO;
import com.library.library_manager.dto.staff.StaffResponseDTO;
import com.library.library_manager.entity.Staff;
import java.util.List;

public interface IStaffService {
    List<StaffResponseDTO> findAll();

    StaffResponseDTO createStaff(StaffRequestDTO request);
    StaffResponseDTO updateStaff(Long staffId, StaffRequestDTO request);

    void deleteStaff(Long staffId);
}