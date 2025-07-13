package org.kosa.tripTalk.seller;

import java.io.IOException;
import org.kosa.tripTalk.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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
    
    //셀러 신청
    @PostMapping("/apply")
    public ResponseEntity<?> registerSeller(
        @RequestParam("bankName") String bankName,
        @RequestParam("accountNumber") String accountNumber,
        @RequestParam("accountHolder") String accountHolder,
        @RequestParam("file") MultipartFile file,
        Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        try {
            Long sellerId = sellerService.registerSeller(userId, bankName, accountNumber, accountHolder, file);
            return ResponseEntity.ok(sellerId);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("파일 업로드 실패");
        }
    }
    
    //셀러 신청 조회
    @GetMapping("/list")
    public ResponseEntity<?> registerSellerList() {
      
      return ResponseEntity.ok(sellerService.getSellerList());
    }
    
    //셀러 신청 삭제
    
    //셀러 신청 승인
    @PostMapping("/approve/{userId}")
    public ResponseEntity<?> toggleSellerApproval(@PathVariable("userId") Long userId) {
      System.out.println("승인 토글 요청 userId: " + userId);
      boolean updated = sellerService.toggleSellerApproval(userId);
      
      return ResponseEntity.ok().body("승인 상태: " + (updated ? "승인됨" : "미승인"));
    }
    
    
    
}