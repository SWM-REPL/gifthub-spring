package org.swmaestro.repl.gifthub.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.swmaestro.repl.gifthub.auth.dto.SignUpDto;
import org.swmaestro.repl.gifthub.auth.entity.Member;
import org.swmaestro.repl.gifthub.auth.repository.SpringDataJpaMemberRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class AuthServiceTest {
	@Mock
	private SpringDataJpaMemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private AuthServiceImpl authService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		authService = new AuthServiceImpl(memberRepository, passwordEncoder);
	}

	/*
	 * 비밀번호 검증 로직 성공 테스트
	 */
	@Test
	void verifyPasswordSuccess() {
		//given
		SignUpDto signUpDto = SignUpDto.builder()
			.username("jinlee1703")
			.password("abc123##")
			.nickname("이진우")
			.build();
		Member member = Member.builder()
			.username("jinlee1703")
			.password("abc123##")
			.nickname("이진우")
			.build();

		when(memberRepository.findByUsername(signUpDto.getUsername())).thenReturn(member);
		when(passwordEncoder.matches(signUpDto.getPassword(), member.getPassword())).thenReturn(true);

		// When
		SignUpDto result = authService.verifyPassword(signUpDto);

		// Then
		assertNotNull(result);
		assertEquals(signUpDto.getUsername(), result.getUsername());
	}

	/*
	 * 비밀번호 검증 로직 실패 테스트
	 */
	@Test
	void verifyPasswordFail() {
		//given
		SignUpDto signUpDto = SignUpDto.builder()
			.username("jinlee1703")
			.password("abc123##")
			.nickname("이진우")
			.build();
		Member member = Member.builder()
			.username("jinlee1703")
			.password("abc123##XX")
			.nickname("이진우")
			.build();

		when(memberRepository.findByUsername(signUpDto.getUsername())).thenReturn(member);
		when(passwordEncoder.matches(signUpDto.getPassword(), member.getPassword())).thenReturn(false);

		// When
		SignUpDto result = authService.verifyPassword(signUpDto);

		// Then
		assertEquals(null, result);
	}
}
