package org.kosa.tripTalk.exception;

public record ErrorResponse(
			String code,
			String message
		) {}