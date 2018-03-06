package com.albertodepaola.fileconsumersb;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileConsumerSbApplication {

	public static void main(String[] args) {
		
		System.setProperty("output", "file:///" + new File("results.txt").getAbsolutePath());
		
		SpringApplication.run(FileConsumerSbApplication.class, args);
	}
}
