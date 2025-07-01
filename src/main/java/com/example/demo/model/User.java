package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class User {
	@Id
	@Column(name = "UID", length = 64, nullable = false)
	private String uid;

	@Column(name = "Name", length = 16, nullable = false)
	private String name;

	@Column(name = "Head", length = 256)
	private String head;

	@Column(name = "Email", length = 64, nullable = false)
	private String email;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Files> files = new ArrayList<>();

	public User() {
	}

	public User(String uid, String name, String head, String email, List<Files> files) {
		super();
		this.uid = uid;
		this.name = name;
		this.head = head;
		this.email = email;
		this.files = files;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Files> getFiles() {
		return files;
	}

	public void setFiles(List<Files> files) {
		this.files = files;
	}

}