package com.projetApply.Project_Apply.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projetApply.Project_Apply.model.Payment;
import com.projetApply.Project_Apply.model.Product;
import com.projetApply.Project_Apply.model.Scan;
import com.projetApply.Project_Apply.model.User;

import java.util.List;

@Repository
public interface ScanRepository extends JpaRepository<Scan, Integer> {

    List<Scan> findByUser(User user);

    List<Scan> findByProduct(Product product);

    List<Scan> findByPayment(Payment payment);

    @Query("SELECT s.product AS product, COUNT(s) AS qty FROM Scan s WHERE s.payment=:payment GROUP BY s.product")

    List<Object[]> findProductQuantitiesByPayment(@Param("payment") Payment payment);

}
