package com.library.library_manager.entity;

import jakarta.persistence.*;

import lombok.*;

import lombok.experimental.FieldDefaults;


import java.time.LocalDate;

import java.util.HashSet;

import java.util.Set;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity

public class Role {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")

    Long id;
    @Column(name = "role_name", unique = true, nullable = false)

    String roleName;

    @ManyToMany(fetch = FetchType.EAGER) // EAGER để khi lấy Role là có luôn List quyền
    @JoinTable(name = "role_permission", // Tên bảng trung gian
            joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id")

    )
    Set<Permission> permissions = new HashSet<>();

    @ManyToMany(mappedBy = "roles") // Phải khớp với tên biến 'roles' trong class User

    Set<User> users = new HashSet<>();

}