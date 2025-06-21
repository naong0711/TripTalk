package org.kosa.tripTalk.itinerarybot;

import java.util.List;

import lombok.Data;

@Data
public class ChatRequest {
	private List<Content> contents;
	
	@Data
	public static class Content{
		private List<Part> parts;
	}
	
	@Data
	public static class Part{
		private String text;
	}
	
	/*
	private String model = "";
	private List<Message> messages;
	
	@Data
	public static class Message{
		String role;
		String content;
	}
	*/
}
