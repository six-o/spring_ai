package com.example.demo.dto;

public class LoginResponseDTO {
	private String uid;
	private String name;
	private String email;
	private String pictureUrl = "https://media.istockphoto.com/id/1320815200/photo/wall-black-background-for-design-stone-black-texture-background.jpg?s=612x612&w=0&k=20&c=hqcH1pKLCLn_ZQ5vUPUfi3BOqMWoBzbk5-61Xq7UMsU=";
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
