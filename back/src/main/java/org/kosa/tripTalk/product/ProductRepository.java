package org.kosa.tripTalk.product;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    // ✅ 할인 정보까지 포함한 상품 조회
    @Query("select p from Product p left join fetch p.discount where p.id = :id")
    Optional<Product> findByIdWithDiscount(@Param("id") Long id);

    // ✅ location + 날짜 + 인원수 조건 검색
    @Query("SELECT p FROM Product p " +
    	       "WHERE p.location = :location " +
    	       "AND p.startDate <= :checkOut " +
    	       "AND p.endDate >= :checkIn " +
    	       "AND :people BETWEEN p.minPeople AND p.maxPeople")
    List<Product> findAllByLocationAndDateAndPeople(
        @Param("location") String location,
        @Param("checkIn") LocalDate checkIn,
        @Param("checkOut") LocalDate checkOut,
        @Param("people") int people
    );

    // ✅ 지역 목록 중복 제거 조회 (프론트에서 지역 선택용)
    @Query("SELECT DISTINCT p.location FROM Product p")
    List<String> findDistinctLocations();
}
