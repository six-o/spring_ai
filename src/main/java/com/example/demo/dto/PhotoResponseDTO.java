package com.example.demo.dto;

public class PhotoResponseDTO {
	private String filename;
	private String pictureURL;
	private String uploadTime;

	public PhotoResponseDTO(String filename, String pictureURL, String uploadTime) {
		super();
		this.filename = filename;
		this.pictureURL = pictureURL;
		this.uploadTime = uploadTime;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPictureURL() {
		return pictureURL;
	}

	public void setPictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
	}

	public String getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}

}
