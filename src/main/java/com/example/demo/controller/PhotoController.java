package com.example.demo.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.PhotoResponseDTO;
import com.example.demo.model.Files;
import com.example.demo.model.User;
import com.example.demo.repository.FilesRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.AuthUtil;
import com.google.cloud.storage.Bucket;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.StorageClient;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/photo")
public class PhotoController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FilesRepository filesRepository;

	@PostMapping("/uploadimage")
	public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file,
			@CookieValue(value = "id_Token", required = false) String idTokenCookie, HttpServletResponse response) {
		// 1. 先明確檢查 null 或空字串
		String idToken = AuthUtil.extractIdToken(null, idTokenCookie);
		try {
			FirebaseToken decodedToken = AuthUtil.verifyIdToken(idToken);
			String userId = decodedToken.getUid();
			String name = file.getOriginalFilename();
			String uniqueId = UUID.randomUUID().toString();
			String objectName = uniqueId + "-" + file.getOriginalFilename();
			User user = userRepository.findById(userId)
					.orElseThrow(() -> new RuntimeException("User not Found: " + userId));

			Optional<Files> existing = filesRepository.findByUser_UidAndName(userId, name);
			if (existing.isPresent()) {
				// 如果已經存在，直接回傳舊的下載 URL
				return ResponseEntity.ok(existing.get().getPictureURL());
			}

			Bucket bucket = StorageClient.getInstance().bucket();

			// 將圖片上傳
			bucket.create(objectName, file.getInputStream(), file.getContentType());

			// 建立公開下載 URL
			String downloadUrl = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
					bucket.getName(), java.net.URLEncoder.encode(objectName, "UTF-8"));

			Files fileEntity = new Files();
			fileEntity.setUser(user);
			fileEntity.setUniqueId(uniqueId);
			fileEntity.setObjectName(objectName);
			fileEntity.setName(name);
			fileEntity.setPictureURL(downloadUrl);
			fileEntity.setUploadTime(LocalDateTime.now());

			filesRepository.save(fileEntity);

			return ResponseEntity.ok(downloadUrl);
		} catch (FirebaseAuthException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cookie 無效或已過期");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("上傳失敗: " + e.getMessage());
		}
	}

	@GetMapping("/getallimages")
	public ResponseEntity<?> getAllImages(@CookieValue(value = "id_Token", required = false) String idTokenCookie,
			HttpServletResponse response) throws Exception {
		// 1. 先明確檢查 null 或空字串
		String idToken = AuthUtil.extractIdToken(null, idTokenCookie);
		try {
			// 2. 驗證 Session Cookie
			FirebaseToken decodedToken = AuthUtil.verifyIdToken(idToken);
			String userUid = decodedToken.getUid();

			// 3. 撈出該使用者所有 Files
			List<Files> files = filesRepository.findAllByUser_Uid(userUid);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

			// 4. 只取 pictureURL
			List<PhotoResponseDTO> photos = files.stream().map(f -> new PhotoResponseDTO(f.getName(), // 原始檔名
					f.getPictureURL(), f.getUploadTime().format(formatter))).collect(Collectors.toList());

			// 5. 回傳 JSON 陣列
			return ResponseEntity.ok(photos);
		} catch (FirebaseAuthException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cookie 無效或已過期");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("上傳失敗: " + e.getMessage());
		}
	}

	@PostMapping("/deleteimage")
	public ResponseEntity<String> deleteImageViaPost(@RequestParam("name") String fileName,
			@CookieValue(value = "id_Token", required = false) String idTokenCookie) {
		return deleteImage(fileName, idTokenCookie);
	}

	private ResponseEntity<String> deleteImage(String fileName, String idTokenCookie) {
		try {
			String idToken = AuthUtil.extractIdToken(null, idTokenCookie);
			FirebaseToken decodedToken = AuthUtil.verifyIdToken(idToken);
			String userId = decodedToken.getUid();

			Optional<Files> optionalFile = filesRepository.findByUser_UidAndName(userId, fileName);
			if (optionalFile.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到指定圖片");
			}

			Files file = optionalFile.get();
			Bucket bucket = StorageClient.getInstance().bucket();
			if (bucket.get(file.getObjectName()) != null) {
				bucket.get(file.getObjectName()).delete();
			}
			filesRepository.delete(file);
			return ResponseEntity.ok("圖片已成功刪除");

		} catch (FirebaseAuthException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("無效的使用者憑證");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("圖片刪除失敗: " + e.getMessage());
		}
	}

	@PutMapping("/uploadimage/{fileId}")
	public ResponseEntity<String> updateImage(@PathVariable Long fileId, @RequestParam("file") MultipartFile file,
			@CookieValue(value = "id_Token", required = false) String idTokenCookie) throws Exception {

		// 驗證 token、取得 userId…（同 uploadImage）
		String idToken = AuthUtil.extractIdToken(null, idTokenCookie);
		FirebaseToken decodedToken = AuthUtil.verifyIdToken(idToken);
		String userId = decodedToken.getUid();

		// 抓出要更新的資料庫紀錄
		Files fileEntity = filesRepository.findById(fileId).orElseThrow(() -> new RuntimeException("找不到檔案：" + fileId));

		// 確保這筆紀錄是屬於同個 user（多餘但安全）
		if (!fileEntity.getUser().getUid().equals(userId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("沒有權限");
		}

		// 覆寫同一個 objectName
		Bucket bucket = StorageClient.getInstance().bucket();
		String objectName = fileEntity.getObjectName();
		bucket.create(objectName, file.getInputStream(), file.getContentType());

		// 更新上傳時間
		fileEntity.setUploadTime(LocalDateTime.now());
		filesRepository.save(fileEntity);

		// 回傳同樣的下載 URL
		String downloadUrl = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
				bucket.getName(), URLEncoder.encode(objectName, StandardCharsets.UTF_8));
		return ResponseEntity.ok(downloadUrl);
	}

}
