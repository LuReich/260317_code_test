package backend.contents.service;

import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.contents.dto.ContentCreateRequestDTO;
import backend.contents.dto.ContentListResponseDTO;
import backend.contents.dto.ContentResponseDTO;
import backend.contents.dto.ContentUpdateRequestDTO;
import backend.contents.entity.ContentsEntity;
import backend.contents.repository.ContentsRepository;
import backend.users.entity.UsersEntity;
import backend.users.service.UsersService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentsService {

    private final ContentsRepository contentsRepository;
    private final UsersService usersService;

    @Transactional
    /**
     * 콘텐츠 생성 (작성자 정보 포함)
     */
    public ContentResponseDTO write(ContentCreateRequestDTO request, Authentication authentication) {
        String loginId = requireLoginId(authentication);

        ContentsEntity entity = new ContentsEntity();
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setViewCount(0L);
        entity.setCreatedBy(loginId);
        entity.setLastModifiedBy(loginId);

        ContentsEntity saved = contentsRepository.save(entity);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    /**
     * 콘텐츠 목록 조회 (페이징)
     */
    public Page<ContentListResponseDTO> list(Pageable pageable) {
        return contentsRepository.findAll(pageable).map(this::toListItem);
    }

    @Transactional
    /**
     * 콘텐츠 상세 조회 및 조회수 증가
     */
    public ContentResponseDTO getDetailAndIncreaseView(Long id) {
        ContentsEntity entity = getEntityOrThrow(id);
        entity.setViewCount(entity.getViewCount() + 1);
        return toResponse(entity);
    }

    @Transactional
    /**
     * 콘텐츠 수정 (본인/ADMIN만 가능)
     */
    public ContentResponseDTO update(Long id, ContentUpdateRequestDTO request, Authentication authentication) {
        String loginId = requireLoginId(authentication);

        ContentsEntity entity = getEntityOrThrow(id);
        checkPermission(entity, loginId);

        boolean changed = false;
        if (request.getTitle() != null) {
            entity.setTitle(request.getTitle());
            changed = true;
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
            changed = true;
        }
        if (!changed) {
            throw new IllegalArgumentException("NO_FIELDS_TO_UPDATE");
        }
        entity.setLastModifiedBy(loginId);

        return toResponse(entity);
    }

    @Transactional
    /**
     * 콘텐츠 삭제 (본인/ADMIN만 가능)
     */
    public void delete(Long id, Authentication authentication) {
        String loginId = requireLoginId(authentication);
        ContentsEntity entity = getEntityOrThrow(id);
        checkPermission(entity, loginId);
        contentsRepository.delete(entity);
    }

    private ContentsEntity getEntityOrThrow(Long id) {
        return contentsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("CONTENT_NOT_FOUND"));
    }

    private String requireLoginId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }
        return String.valueOf(authentication.getPrincipal());
    }

    private void checkPermission(ContentsEntity entity, String loginId) {
        // ADMIN은 전체 수정/삭제 가능
        UsersEntity user = usersService.getByLoginIdOrThrow(loginId);
        if ("ADMIN".equals(user.getRole())) {
            return;
        }

        if (!loginId.equals(entity.getCreatedBy())) {
            throw new IllegalArgumentException("FORBIDDEN");
        }
    }

    private ContentResponseDTO toResponse(ContentsEntity e) {
        ContentResponseDTO dto = new ContentResponseDTO();
        dto.setId(e.getId());
        dto.setTitle(e.getTitle());
        dto.setDescription(e.getDescription());
        dto.setViewCount(e.getViewCount());
        dto.setCreatedDate(e.getCreatedDate());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setLastModifiedDate(e.getLastModifiedDate());
        dto.setLastModifiedBy(e.getLastModifiedBy());
        return dto;
    }

    private ContentListResponseDTO toListItem(ContentsEntity e) {
        ContentListResponseDTO dto = new ContentListResponseDTO();
        dto.setId(e.getId());
        dto.setTitle(e.getTitle());
        dto.setViewCount(e.getViewCount());
        dto.setCreatedDate(e.getCreatedDate());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setLastModifiedDate(e.getLastModifiedDate());
        dto.setLastModifiedBy(e.getLastModifiedBy());
        return dto;
    }
}
