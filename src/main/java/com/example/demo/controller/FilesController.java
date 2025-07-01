package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.PhotoResponseDTO;
import com.example.demo.repository.FilesRepository;
import com.example.demo.util.AuthUtil;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

@RestController
@RequestMapping("/files")
public class FilesController {

	@Autowired
	private FilesRepository filesRepository;

	@GetMapping("/searchByDate")
	public List<PhotoResponseDTO> searchByDate(@RequestParam(required = false) Integer year,
			@RequestParam(required = false) Integer month, @RequestParam(required = false) Integer day,
			@CookieValue("id_Token") String idToken) throws FirebaseAuthException {

		FirebaseToken token = AuthUtil.verifyIdToken(idToken);
		String uid = token.getUid();

		// 過濾邏輯在 Java 中實作（非 SQL 動態條件）
		return filesRepository.findAllByUser_Uid(uid).stream().filter(f -> {
			boolean match = true;
			if (year != null) {
				match &= f.getUploadTime().getYear() == year;
			}
			if (month != null) {
				match &= f.getUploadTime().getMonthValue() == month;
			}
			if (day != null) {
				match &= f.getUploadTime().getDayOfMonth() == day;
			}
			return match;
		}).map(f -> new PhotoResponseDTO(f.getName(), f.getPictureURL(), f.getUploadTime().toString()))
				.collect(Collectors.toList());
	}
}
