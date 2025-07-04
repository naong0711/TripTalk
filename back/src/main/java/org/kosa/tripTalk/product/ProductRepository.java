package org.kosa.tripTalk.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    @Query("select p from Product p left join fetch p.discount where p.id = :id")
    Optional<Product> findByIdWithDiscount(@Param("id") Long id);
}
