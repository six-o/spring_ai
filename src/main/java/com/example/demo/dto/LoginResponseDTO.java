package com.example.demo.dto;

public class LoginResponseDTO {
	private String uid;
	private String name;
	private String email;
	private String pictureUrl;
	private Boolean emailVerified;

	public LoginResponseDTO(String uid, String name, String email, String pictureUrl, Boolean emailVerified) {
		super();
		this.uid = uid;
		this.name = name;
		this.email = email;
		this.pictureUrl = pictureUrl;
		this.emailVerified = emailVerified;
	}

	public String getUid() {
		return uid;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public Boolean getEmailVerified() {
		return emailVerified;
	}

}
