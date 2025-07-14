package org.kosa.tripTalk.itinerarybot;

import lombok.Data;

@Data
public class ChatInput {
    private ChatRequest chatRequest;
    private String userInput;
}