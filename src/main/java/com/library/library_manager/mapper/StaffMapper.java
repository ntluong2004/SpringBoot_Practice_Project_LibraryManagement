package com.library.library_manager.mapper;

import com.library.library_manager.dto.staff.StaffResponseDTO;
import com.library.library_manager.entity.Staff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StaffMapper {

    @Mapping(target = "fullName",     expression = "java(staff.getUser().getFullName())")
    @Mapping(target = "username",     expression = "java(staff.getUser().getUserName())")
    @Mapping(target = "email",        expression = "java(staff.getUser().getEmail())")
    @Mapping(target = "phoneNumber",  expression = "java(staff.getUser().getPhoneNumber())")
    @Mapping(target = "positionName", expression = "java(staff.getPosition().getPositionName())")
    StaffResponseDTO staffToStaffResponseDTO(Staff staff);

    List<StaffResponseDTO> toStaffResponseList(List<Staff> staffList);
}
