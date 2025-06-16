package org.kosa.tripTalk.seller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SellerController {
	private final SellerService sellerService;
}
