package com.library.library_manager.controller;

import com.library.library_manager.dto.ApiResponse;
import com.library.library_manager.dto.authenticate.AuthenticationRequestDTO;
import com.library.library_manager.dto.authenticate.AuthenticationResponseDTO;
import com.library.library_manager.service.IAuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/auth")
public class AuthenticationController {

    IAuthenticationService authenticationService;

    @PostMapping("login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationRequestDTO authenticationRequest) {

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<AuthenticationResponseDTO>builder()
                        .data(authenticationService.login(authenticationRequest))
                        .build()
        );
    }
}
