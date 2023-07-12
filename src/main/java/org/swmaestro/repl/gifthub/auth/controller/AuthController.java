package org.swmaestro.repl.gifthub.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.swmaestro.repl.gifthub.auth.dto.SignInDto;
import org.swmaestro.repl.gifthub.auth.dto.SignUpDto;
import org.swmaestro.repl.gifthub.auth.dto.TokenDto;
import org.swmaestro.repl.gifthub.auth.service.AuthService;
import org.swmaestro.repl.gifthub.auth.service.MemberService;
import org.swmaestro.repl.gifthub.auth.service.RefreshTokenService;
import org.swmaestro.repl.gifthub.util.JwtProvider;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "사용자 인증 관련 API")
public class AuthController {
	private final MemberService memberService;
	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;
	private final JwtProvider jwtProvider;

	@PostMapping("/sign-up")
	@Operation(summary = "회원가입 메서드", description = "사용자가 회원가입을 하기 위한 메서드입니다.")
	public TokenDto signUp(@RequestBody SignUpDto signUpDto) {
		TokenDto tokenDto = memberService.create(signUpDto);
		return tokenDto;
	}

	@PostMapping("/sign-in")
	@Operation(summary = "로그인 메서드", description = "사용자가 로그인을 하기 위한 메서드입니다.")
	public TokenDto signIn(@RequestBody SignInDto loginDto) {
		TokenDto tokenDto = authService.signIn(loginDto);
		return tokenDto;
	}

	@PostMapping("/refresh")
	@Operation(summary = "Refresh Token 재발급 메서드", description = "Refresh Token을 재발급 받기 위한 메서드입니다.")
	public TokenDto validateRefreshToken(@RequestHeader("Authorization") String refreshToken) {
		String newAccessToken = refreshTokenService.createNewAccessTokenByValidateRefreshToken(refreshToken);
		String newRefreshToken = refreshTokenService.createNewRefreshTokenByValidateRefreshToken(refreshToken);

		TokenDto tokenDto = TokenDto.builder()
			.accessToken(newAccessToken)
			.refreshToken(newRefreshToken)
			.build();

		refreshToken = refreshToken.substring(7);
		refreshTokenService.storeRefreshToken(tokenDto, jwtProvider.getUsername(refreshToken));
		return tokenDto;
	}
}
