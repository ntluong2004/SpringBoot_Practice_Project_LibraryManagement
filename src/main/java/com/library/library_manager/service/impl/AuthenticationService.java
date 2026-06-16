package com.library.library_manager.service.impl;

import com.library.library_manager.exception.ErrorCode;
import com.library.library_manager.repository.IUserRepository;
import com.library.library_manager.service.IAuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.library.library_manager.dto.authenticate.AuthenticationRequestDTO;
import com.library.library_manager.dto.authenticate.AuthenticationResponseDTO;
import com.library.library_manager.entity.User;
import com.library.library_manager.exception.AppException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService implements IAuthenticationService {

    IUserRepository userRepository;
    @Value("${jwt.signerKey}")
    @NonFinal
    String SIGNER_KEY;

    @Override
    public AuthenticationResponseDTO login(AuthenticationRequestDTO authenticationRequest) {
        // Bước 1: lấy user thông qua username
        User user = userRepository.findByUserName(authenticationRequest.getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        // Bước 2: check password coi đúng hay sai
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Bước 3: gen token
        return AuthenticationResponseDTO.builder()
                .token(generateToken(user))
                .build();
    }

    private String generateToken(User user) {
        // Tạo phần header cho JWT, sử dụng thuật toán ký là HS512 (HMAC SHA-512)
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        // Tạo phần claims (payload) cho JWT, chứa các thông tin về người dùng
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().subject(user.getUserName()) // Đặt chủ thể (subject) của JWT là tên đăng nhập của người dùng
                .issuer("tolu.com") // Đặt người phát hành JWT là "sqc.com"
                .issueTime(new Date()) // Đặt thời gian phát hành JWT là thời điểm hiện tại
                .expirationTime(new Date( // Đặt thời gian hết hạn cho JWT là 1 giờ kể từ lúc phát hành
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                // Thêm một custom claim (thông tin tùy chỉnh) vào JWT, chứa thông tin về đối tượng Student
                .claim("scope", getRole(user)).build(); // Xây dựng đối tượng JWTClaimsSet

        // Tạo payload từ claims đã tạo, chuyển đối tượng claims thành định dạng JSON
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        // Tạo JWSObject từ header và payload, kết hợp chúng lại thành đối tượng JWS
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            // Ký JWT bằng thuật toán HMAC SHA-512, sử dụng khóa bí mật (SIGNER_KEY)
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));

            // Chuyển đối tượng JWS thành chuỗi JWT hoàn chỉnh (header.payload.signature) và trả về
            return jwsObject.serialize();
        } catch (JOSEException e) {
            // Nếu có lỗi xảy ra trong quá trình ký JWT, ném ra ngoại lệ RuntimeException
            throw new RuntimeException(e);
        }
    }

    private String getRole(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

//        user.getRoles().forEach(role -> stringJoiner.add(role.getName()));

        user.getRoles().forEach(role -> {
            stringJoiner.add("ROLE_" + role.getRoleName());

            role.getPermissions()
                    .forEach(permission -> stringJoiner.add(permission.getName()));
        });

        return stringJoiner.toString();
    }
}
