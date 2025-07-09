package org.kosa.tripTalk.seller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.kosa.tripTalk.file.File;
import org.kosa.tripTalk.file.FileRepository;
import org.kosa.tripTalk.file.FileService;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final FileRepository fileRepository;

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
    
    @Transactional
    public Long registerSeller(Long userId, String bankName, String accountNumber,
                               String accountHolder, MultipartFile file) throws IOException {

      User user = userRepository.findById(userId)
          .orElseThrow(() -> new RuntimeException("User not found"));

      // 1) 사업자등록증 이미지 저장 (예: 파일시스템, S3 등)
      File storedFilePath = fileService.saveFile(file, "SELLER", userId, 0);

      // 2) Seller 엔티티 생성
      Seller seller = Seller.builder()
          .user(user)
          .bankName(bankName)          // 은행명 or 사업자명 컬럼으로 바꿔주세요
          .accountNumber(accountNumber)   // 계좌번호 대신 사업자 번호 컬럼으로 적합한 컬럼 사용하세요
          .accountHolder(accountHolder)          // 예금주를 연락처 대신 저장한다면 적절히 수정 필요
          .isApply(false)                  // 신청 상태: 미승인
          .build();

      // 추가로 이미지 파일 경로 등도 seller에 저장하려면 컬럼 추가 후 세팅

      Seller savedSeller = sellerRepository.save(seller);

      return savedSeller.getId();
    }

    public List<SellerRequestResponse> getSellerList() {
      List<Seller> sellers = sellerRepository.findAll();
      List<File> sellerFiles = fileRepository.findByOwnerType("SELLER");

      Map<Long, File> fileMap = sellerFiles.stream()
          .collect(Collectors.toMap(File::getOwnerId, file -> file, (a, b) -> a));

      List<SellerRequestResponse> result = new ArrayList<>();
      
      for (File f : sellerFiles) {
        System.out.println("file ownerId: " + f.getOwnerId() + ", originName: " + f.getOriginName());
    }
      

      for (Seller seller : sellers) {
          File file = fileMap.get(seller.getUser().getId());

          SellerRequestResponse dto = SellerRequestResponse.builder()
              .id(seller.getUser().getId())        // 유저 PK
              .sellerId(seller.getId())            // 셀러 PK
              .sellerName(seller.getUser().getName())  // 셀러 이름
              .fileName(file != null ? file.getOriginName() : "없음")
              .status(seller.getIsApply())
              .build();

          System.out.println("id: " + seller.getUser().getId());
          System.out.println("sellerId: " + seller.getId());
          System.out.println("매핑된 파일: " + (file != null ? file.getOriginName() : "없음"));
          result.add(dto);
      }

      return result;
  }

    @Transactional
    public boolean toggleSellerApproval(Long userId) {
        Seller seller = sellerRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("해당 판매자를 찾을 수 없습니다."));

        boolean newStatus = !seller.getIsApply();
        seller.setIsApply(newStatus);
        sellerRepository.save(seller);

        return newStatus;
    }

}