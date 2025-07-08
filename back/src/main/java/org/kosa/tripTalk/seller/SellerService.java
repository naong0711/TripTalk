package org.kosa.tripTalk.seller;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final SellerRepository sellerRepository;

    /**
     * userId로 sellerId 조회
     * @param userId - 로그인 사용자 ID
     * @return sellerId (없으면 null 반환)
     */
    public Long getSellerIdByUserId(Long userId) {
        return sellerRepository.findByUserId(userId)
                .map(Seller::getId)
                .orElse(null);
    }
}