package org.kosa.tripTalk.seller;

import org.kosa.tripTalk.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService;

    /**
     * 현재 로그인한 사용자의 sellerId 조회
     */
    @GetMapping("/my-id")
    public ResponseEntity<Long> getMySellerId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        System.out.println(user.getId());
        Long sellerId = sellerService.getSellerIdByUserId(user.getId()); // 또는 user.getUserId() - 구현 방식에 따라
        System.out.println(sellerId);
        return ResponseEntity.ok(sellerId);
        
    }
}