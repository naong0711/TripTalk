package org.kosa.tripTalk.itinerarybot;

import java.util.List;

import org.kosa.tripTalk.itinerarybot.ChatRequest.Message;

import lombok.Data;

@Data
public class ChatResponse {
	private List<Candidate> candidates;
	
	@Data
	public static class Candidate{
		private Message content;
	}
	
	@Data
	public static class Message{
		private String role;
		private List<Part> parts;
	}
	
	@Data
	public static class Part{
		private String text;
	}
	
	/*단방향 대화
	@Data
	public static class Candidate{
		private Content content;
	}
	
	@Data
	public static class Content{
		private List<Part> parts;
	}
	
	@Data
	public static class Part{
		private String text;
	}
	*/
	
	/*
	 * Open AI용
	private String id;
	private String onject;
	private long created;
	private String model;
	private List<Choice> choices;
	
	@Data
	public static class Choice{
		private int index;
		private Message message;
		private Object logprobs;
		private String finish_reason;
	}
	
	@Data
	public static class Message{
		private String role;
		private String content;
		private Object refusal;
		private List<Object> annotations;
	}
	*/

}
