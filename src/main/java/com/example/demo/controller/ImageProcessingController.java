package com.example.demo.controller;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.client.FastApiClient;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/process")
public class ImageProcessingController {

	private final FastApiClient fastApiClient;
	private final Map<String, Function<MultipartFile, byte[]>> processors = new HashMap<>();

	public ImageProcessingController(FastApiClient fastApiClient) {
		this.fastApiClient = fastApiClient;
	}

	@PostConstruct
	public void init() {
		processors.put("esrgan", fastApiClient::ImageRestore_esrgan);
		processors.put("deoldify", fastApiClient::ImageRestore_deoldify);
	}

	/**
	 * 接收前端上傳的 MultipartFile，呼叫 FastApiClient.uploadImage， 拿回 byte[]，再包成
	 * ResponseEntity 回給前端瀏覽器下載或顯示。
	 */
	@PostMapping("/{model}")
	public ResponseEntity<byte[]> processImage(@PathVariable String model, @RequestPart("file") MultipartFile file) {

		Function<MultipartFile, byte[]> fn = processors.get(model.toLowerCase());
		if (fn == null) {
			return ResponseEntity.badRequest().body(("Unknown model: " + model).getBytes(StandardCharsets.UTF_8));
		}

		byte[] out = fn.apply(file);
		return buildImageResponse(out, file);
	}

	private ResponseEntity<byte[]> buildImageResponse(byte[] data, MultipartFile original) {
		// 保留原始 Content-Type
		MediaType mt;
		try {
			mt = MediaType.parseMediaType(original.getContentType());
		} catch (Exception e) {
			mt = MediaType.APPLICATION_OCTET_STREAM;
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mt);
		headers.setContentLength(data.length);

		return new ResponseEntity<>(data, headers, HttpStatus.OK);
	}
}
