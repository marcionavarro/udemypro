package com.mn.pdv.repository;

import com.mn.pdv.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductREpository extends JpaRepository<Product, Long> {
}
