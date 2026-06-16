package com.library.library_manager.service;

import com.library.library_manager.dto.authenticate.AuthenticationRequestDTO;
import com.library.library_manager.dto.authenticate.AuthenticationResponseDTO;

public interface IAuthenticationService {
    AuthenticationResponseDTO login(AuthenticationRequestDTO authenticationRequestDTO);

}
