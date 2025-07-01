package com.example.demo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "fastApiClient", url = "${fastapi.url}")
public interface FastApiClient {

	/**
	 * 對應 FastAPI 的 POST /upload consumes = multipart/form-data 回傳 raw byte[] 圖片
	 */
	@PostMapping(value = "/upload_esrgan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	byte[] ImageRestore_esrgan(@RequestPart("file") MultipartFile file);

	@PostMapping(value = "/upload_deoldify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	byte[] ImageRestore_deoldify(@RequestPart("file") MultipartFile file);
}
