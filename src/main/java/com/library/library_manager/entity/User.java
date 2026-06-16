package com.library.library_manager.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    Long userId;

    @Column(name = "full_name", nullable = false)
    String fullName;

    @Column(name = "email", unique = true, nullable = false)
    String email;

    @Column(name = "phone_number")
    String phoneNumber;

    @Column(name = "user_name", unique = true, nullable = false, length = 50)
    String userName;

    @Column(name = "password", nullable = false)
    String password;

    // Quan hệ n-n với Roles
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role", // Tên bảng trung gian tự sinh
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    List<Incident> reportedIncidents;

    @OneToOne(mappedBy = "user") // "user" là tên biến User trong class Student
    private Student student;
}