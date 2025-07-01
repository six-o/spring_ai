package com.example.demo.controller;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/download")
public class DownloadController {

	@GetMapping
	public ResponseEntity<byte[]> proxyDownload(@RequestParam("url") String fileUrl,
			@RequestParam("filename") String filename) {
		try {
			URL url = new URL(fileUrl);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0"); // 防止被拒絕存取

			try (InputStream is = conn.getInputStream()) {
				byte[] content = is.readAllBytes();
				return ResponseEntity.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
						.contentType(MediaType.APPLICATION_OCTET_STREAM).body(content);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(null);
		}
	}
}
