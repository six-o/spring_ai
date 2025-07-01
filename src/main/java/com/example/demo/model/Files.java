package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Files {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "User_ID", nullable = false)
	private User user;

	@Column(name = "Unique_ID", length = 64, nullable = false, unique = true)
	private String uniqueId;

	@Column(name = "Object_name", length = 255, nullable = false)
	private String objectName;

	@Column(name = "Name", length = 128, nullable = false)
	private String name;

	// 新增：儲存圖片在雲端的網址
	@Column(name = "Picture_URL", length = 255, nullable = false)
	private String pictureURL;

	@Column(name = "Upload_time", nullable = false)
	private LocalDateTime uploadTime;

	public Files() {
	}

	public Files(Long id, User user, String uniqueId, String objectName, String name, String pictureURL,
			LocalDateTime uploadTime) {
		this.id = id;
		this.user = user;
		this.uniqueId = uniqueId;
		this.objectName = objectName;
		this.name = name;
		this.pictureURL = pictureURL;
		this.uploadTime = uploadTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPictureURL() {
		return pictureURL;
	}

	public void setPictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
	}

	public LocalDateTime getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(LocalDateTime uploadTime) {
		this.uploadTime = uploadTime;
	}

}
