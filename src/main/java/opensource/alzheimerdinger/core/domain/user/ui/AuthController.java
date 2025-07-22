package opensource.alzheimerdinger.core.domain.user.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.LoginRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.SignUpToGuardianRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.SignUpRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.LoginResponse;
import opensource.alzheimerdinger.core.domain.user.application.usecase.UserAuthUseCase;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "회원가입·로그인·로그아웃 인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AuthController {

    private final UserAuthUseCase userAuthUseCase;

    /**
     *  회원가입 (보호자는 ROLE_GUARDIAN, 피보호자는 ROLE_PATIENT)
     */
    @Operation(
            summary     = "회원가입",
            description = "보호자는 ROLE_GUARDIAN, 피보호자는 ROLE_PATIENT 권한으로 사용자 등록",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "가입할 사용자 정보",
                    required    = true,
                    content     = @Content(schema = @Schema(implementation = SignUpRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "가입 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자", content = @Content)
            }
    )
    @PostMapping("/sign-up")
    public BaseResponse<Void> signUp(
            @Valid
            @org.springframework.web.bind.annotation.RequestBody
            SignUpRequest request) {
        userAuthUseCase.signUp(request);
        return BaseResponse.onSuccess();
    }

    @Operation(
            summary = "로그인",
            description = "아이디/비밀번호로 인증 후 JWT 토큰 발급",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그인 요청 정보",
                    required    = true,
                    content     = @Content(schema = @Schema(implementation = LoginRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
            }
    )
    @PostMapping("/login")
    public BaseResponse<LoginResponse> login(
            @Valid
            @RequestBody
            LoginRequest request) {
        return BaseResponse.onSuccess(userAuthUseCase.login(request));
    }

    @Operation(
            summary = "로그아웃",
            description = "헤더의 JWT 토큰을 무효화하여 로그아웃 처리",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
                    @ApiResponse(responseCode = "401", description = "잘못된 토큰",
                            content = @Content)
            }
    )
    @DeleteMapping("/logout")
    public BaseResponse<Void> logout(
            @Parameter(description = "HttpServletRequest 에서 JWT 추출", hidden = true)
            HttpServletRequest request) {
        userAuthUseCase.logout(request);
        return BaseResponse.onSuccess();
    }

    )
}
