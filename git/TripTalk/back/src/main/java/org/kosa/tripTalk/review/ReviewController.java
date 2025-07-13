package org.kosa.tripTalk.review;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;
}
