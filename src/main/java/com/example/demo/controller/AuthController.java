package com.example.demo.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.AuthUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private UserRepository userRepository; // ← 注入

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			HttpServletResponse response) throws Exception {
		try {
			String idToken = AuthUtil.extractIdToken(authorization, null);
			FirebaseToken decodedToken = AuthUtil.verifyIdToken(idToken);

			UserRecord userRecord = FirebaseAuth.getInstance().getUser(decodedToken.getUid());
			AuthUtil.addHttpOnlyCookie(response, "id_Token", idToken, TimeUnit.DAYS.toSeconds(5), "/");

			LoginResponseDTO dto = AuthUtil.buildLoginDto(decodedToken, userRecord);

			// ✅ 修正這段邏輯
			User userEntity = userRepository.findById(dto.getUid()).orElse(null);
			if (userEntity == null) {
				userEntity = new User();
				userEntity.setUid(dto.getUid());
				System.out.println("🔧 建立新使用者：" + dto.getUid());
			} else {
				System.out.println("🔄 更新已存在使用者：" + dto.getUid());
			}
			userEntity.setName(dto.getName());
			userEntity.setHead(dto.getPictureUrl());
			userEntity.setEmail(dto.getEmail());

			userRepository.save(userEntity); // ← 寫入或更新

			System.out.println("登入成功！\tUID：" + dto.getUid() + "\tName：" + dto.getName() + "\tEmail：" + dto.getEmail());

			return ResponseEntity.ok(dto);
		} catch (IllegalArgumentException e) {
			// 缺少或格式不對的 token
			return ResponseEntity.badRequest().body(null);
		} catch (FirebaseAuthException e) {
			// 驗證失敗（簽名、過期、撤銷等）
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		} catch (Exception e) {
			// 其他未知錯誤
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PostMapping("/valid")
	public ResponseEntity<LoginResponseDTO> valid(
			@CookieValue(value = "id_Token", required = false) String idTokenCookie, HttpServletResponse response)
			throws FirebaseAuthException {
		try {
			String idToken = AuthUtil.extractIdToken(null, idTokenCookie);
			FirebaseToken decodedToken = AuthUtil.verifyIdToken(idToken);
			UserRecord userRecord = FirebaseAuth.getInstance().getUser(decodedToken.getUid());
			LoginResponseDTO dto = AuthUtil.buildLoginDto(decodedToken, userRecord);

			return ResponseEntity.ok(dto);
		} catch (Exception e) {
			// 其他未知錯誤
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

}
