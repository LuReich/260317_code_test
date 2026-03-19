package backend.users.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.users.dto.UsersDTO;
import backend.users.entity.UsersEntity;
import backend.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 ID로 사용자 조회 (없으면 예외)
     */
    @Transactional(readOnly = true)
    public UsersEntity getByLoginIdOrThrow(String loginId) {
        return usersRepository.findByLoginId(loginId)
                .orElseThrow(() -> new NoSuchElementException("USER_NOT_FOUND"));
    }

    /**
     * 로그인 ID로 사용자 DTO 반환
     */
    @Transactional(readOnly = true)
    public UsersDTO getUserDto(String loginId) {
        UsersEntity user = getByLoginIdOrThrow(loginId);
        UsersDTO dto = new UsersDTO();
        dto.setUid(user.getUid());
        dto.setId(user.getLoginId());
        dto.setNickname(user.getNickname());
        dto.setRole(user.getRole());
        dto.setCreateAt(user.getCreateAt());
        dto.setUpdateAt(user.getUpdateAt());
        return dto;
    }

    /**
     * 비밀번호 일치 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean matchesPassword(UsersEntity user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    /**
     * 전체 사용자 목록 반환
     */
    @Transactional(readOnly = true)
    public List<UsersDTO> listAll() {
        return usersRepository.findAll().stream()
                .map(user -> {
                    UsersDTO dto = new UsersDTO();
                    dto.setUid(user.getUid());
                    dto.setId(user.getLoginId());
                    dto.setNickname(user.getNickname());
                    dto.setRole(user.getRole());
                    dto.setCreateAt(user.getCreateAt());
                    dto.setUpdateAt(user.getUpdateAt());
                    return dto;
                })
                .toList();
    }
}
