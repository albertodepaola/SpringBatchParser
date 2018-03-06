package com.albertodepaola.fileconsumersb.model;

import org.springframework.batch.support.annotation.Classifier;

public class LogLineClassifier {

	@Classifier
	public String classify(LogLine classifiable) {
		return classifiable.getType();
	}
}