package backend.contents.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import backend.common.ApiResponse;
import backend.common.PageResponseDTO;
import backend.contents.dto.ContentCreateRequestDTO;
import backend.contents.dto.ContentListResponseDTO;
import backend.contents.dto.ContentResponseDTO;
import backend.contents.dto.ContentUpdateRequestDTO;
import backend.contents.service.ContentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/contents")
@RequiredArgsConstructor
public class ContentsController {

    private final ContentsService contentsService;

    /**
     * 콘텐츠 생성 (로그인 필요)
     */
    @PostMapping("/write")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ContentResponseDTO> write(
            @Valid @RequestBody ContentCreateRequestDTO request,
            Authentication authentication
    ) {
        return ApiResponse.created(contentsService.write(request, authentication));
    }

    /**
     * 콘텐츠 목록 조회 (페이징)
     */
    @GetMapping("/list")
    public ApiResponse<PageResponseDTO<ContentListResponseDTO>> list(Pageable pageable) {
        Page<ContentListResponseDTO> page = contentsService.list(pageable);
        return ApiResponse.ok(new PageResponseDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        ));
    }

    /**
     * 콘텐츠 상세 조회 및 조회수 증가
     */
    @GetMapping("/{id}")
    public ApiResponse<ContentResponseDTO> detail(@PathVariable("id") Long id) {
        return ApiResponse.ok(contentsService.getDetailAndIncreaseView(id));
    }

    /**
     * 콘텐츠 수정 (본인/ADMIN만 가능)
     */
    @PatchMapping("/{id}")
    public ApiResponse<ContentResponseDTO> patch(
            @PathVariable("id") Long id,
            @Valid @RequestBody ContentUpdateRequestDTO request,
            Authentication authentication
    ) {
        return ApiResponse.ok(contentsService.update(id, request, authentication));
    }

    /**
     * 콘텐츠 삭제 (본인/ADMIN만 가능)
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id, Authentication authentication) {
        contentsService.delete(id, authentication);
    }
}
