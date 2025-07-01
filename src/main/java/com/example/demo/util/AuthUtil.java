package com.example.demo.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import com.example.demo.dto.LoginResponseDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

import jakarta.servlet.http.HttpServletResponse;

public class AuthUtil {
	/**
	 * 從 Authorization 頭或名為 id_Token 的 Cookie 中提取 idToken。
	 *
	 * @param authorizationHeader HTTP Header 中的 Authorization 字段（可帶 “Bearer ” 前綴）
	 * @param idTokenCookie       名为 id_Token 的 Cookie 值
	 * @return 提取并去掉 “Bearer ” 前缀後的原始 idToken
	 * @throws IllegalArgumentException 如果兩者都為空或格式不正確
	 */
	public static String extractIdToken(String authorizationHeader, String idTokenCookie) {
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}
		if (idTokenCookie != null && !idTokenCookie.isBlank()) {
			return idTokenCookie;
		}
		throw new IllegalArgumentException("必须提供 Authorization header 或 id_Token cookie");
	}

//	d 9 5 6 2 0 8 1  -  8 4 3 7  -  4 3 7 2  -  a 1 3 3  -  2 4 d 9 0 d 7 2 a f 9 f  -  Error  .  png
//	e 5 7 6 8 5 f 4  -  c c 2 0  -  4 a 0 e  -  b 6 8 c  -  1 b b e 5 5 b 9 f f 6 8  -  e r r  .  png
//	1 e e c e c 9 a  -  f b 4 d  -  4 5 9 a  -  8 f 6 6  -  2 a 2 e f 5 c 1 e 1 d a  -  w e b  .  png
//  [      0      ]  -  [  1  ]  -  [  2  ]  -  [  3  ]  -  [          4          ]  -  name   .  ext
	/**
	 * 验证并解析 idToken。
	 *
	 * @param idToken 从前端拿到的原始 token
	 * @return FirebaseToken 解码后的 Token 对象
	 * @throws FirebaseAuthException 如果 token 验证失败（签名、过期、撤销 等）
	 */
	public static FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
		return FirebaseAuth.getInstance().verifyIdToken(idToken, true);
	}

	public static void addHttpOnlyCookie(HttpServletResponse resp, String name, String value, long maxAgeSeconds,
			String path) {
		// 用 Spring 的 ResponseCookie 來建
		ResponseCookie cookie = ResponseCookie.from(name, value).httpOnly(true).secure(false) // 本地測試用 http；部署到 https 時改
				.path(path).maxAge(maxAgeSeconds).sameSite("Lax") // ← 關鍵，讓跨站 XHR/Fetch 能帶上
				.build();

		resp.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

	public static LoginResponseDTO buildLoginDto(FirebaseToken decoded, UserRecord userRecord) {
		return new LoginResponseDTO(decoded.getUid(), decoded.getName(), decoded.getEmail(), decoded.getPicture(),
				userRecord.isEmailVerified());
	}
}
