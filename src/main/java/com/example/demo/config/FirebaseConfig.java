// src/main/java/com/example/demo/config/FirebaseConfig.java
package com.example.demo.config;

import java.io.IOException;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

@Configuration
public class FirebaseConfig {

	@Bean
	public FirebaseApp firebaseApp() throws IOException {
//        var serviceAccount = new ClassPathResource("serviceAccountKey.json").getInputStream();
		var serviceAccount = new ClassPathResource("test-2ea39-firebase-adminsdk.json").getInputStream();
		var options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.setStorageBucket("test-2ea39.firebasestorage.app").build();
		// 2. 检查是否已有初始化过的 DEFAULT 实例
		List<FirebaseApp> apps = FirebaseApp.getApps();
		if (apps.stream().noneMatch(app -> FirebaseApp.DEFAULT_APP_NAME.equals(app.getName()))) {
			// 如果还没有，就初始化一个
			return FirebaseApp.initializeApp(options);
		} else {
			// 否则直接复用第一个（也是 DEFAULT）
			return FirebaseApp.getInstance();
		}
	}

	@Bean
	public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
		return FirebaseAuth.getInstance(firebaseApp);
	}
}
