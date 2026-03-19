package backend.users.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.users.entity.UsersEntity;
import backend.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;

/**
 * Spring Security에서 사용자 인증 시 DB에서 사용자 정보를 조회해주는 서비스 (UserDetailsService 구현체,
 * 로그인 시 반드시 필요)
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    /**
     * Spring Security가 로그인 시 호출하는 메서드 username(로그인 ID)로 DB에서 사용자 정보를 조회해
     * UserDetails로 반환
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersEntity user = usersRepository.findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("USER_NOT_FOUND"));

        return new User(
                user.getLoginId(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
