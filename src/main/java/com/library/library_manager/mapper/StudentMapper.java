package com.library.library_manager.mapper;

import com.library.library_manager.dto.student.StudentProfileResponseDTO;
import com.library.library_manager.dto.student.StudentResponseDTO;
import com.library.library_manager.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "fullName",     expression = "java(student.getUser().getFullName())")
    @Mapping(target = "email",        expression = "java(student.getUser().getEmail())")
    @Mapping(target = "phoneNumber",  expression = "java(student.getUser().getPhoneNumber())")
    @Mapping(target = "balance",      source = "totalDebt")
    StudentResponseDTO studentToStudentResponseDTO(Student student);

    List<StudentResponseDTO> toStudentResponseList(List<Student> students);

    @Mapping(target = "fullName",    expression = "java(student.getUser().getFullName())")
    @Mapping(target = "email",       expression = "java(student.getUser().getEmail())")
    @Mapping(target = "phoneNumber", expression = "java(student.getUser().getPhoneNumber())")
    StudentProfileResponseDTO studentToStudentProfileResponseDTO(Student student);
}
