package com.sweey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VideoShareApplication {
	
//	static {
//		try {
//			System.out.println("run.......");
//			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//		} catch (UnsatisfiedLinkError ignore) {
//		}
//	}
	
	public static void main(String[] args) {
		SpringApplication.run(VideoShareApplication.class, args);
	}

}
