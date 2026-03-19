package backend.users.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import backend.common.ApiResponse;
import backend.security.JwtTokenProvider;
import backend.users.dto.LoginRequestDTO;
import backend.users.dto.LoginResponseDTO;
import backend.users.dto.UsersDTO;
import backend.users.entity.UsersEntity;
import backend.users.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 로그인 (JWT 토큰 발급)
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        UsersEntity user = usersService.getByLoginIdOrThrow(request.getId());
        if (!usersService.matchesPassword(user, request.getPassword())) {
            throw new IllegalArgumentException("INVALID_CREDENTIALS");
        }

        String token = jwtTokenProvider.createAccessToken(user.getLoginId(), user.getRole());
        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setAccessToken(token);
        dto.setTokenType("Bearer");
        return ApiResponse.ok(dto);
    }

    /**
     * 전체 사용자 목록 조회 (ADMIN만 가능)
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UsersDTO>> listUsers() {
        return ApiResponse.ok(usersService.listAll());
    }
}
