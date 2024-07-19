package com.mn.cryptoapp.repository;

import com.mn.cryptoapp.dto.CoinTransactionDTO;
import com.mn.cryptoapp.entity.Coin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableAutoConfiguration
public class CoinRepository {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public Coin insert(Coin coin) {
        entityManager.persist(coin);
        return coin;
    }

    @Transactional
    public Coin update(Coin coin) {
        entityManager.merge(coin);
        return coin;
    }

    public List<CoinTransactionDTO> getAll() {
        String jpql = "select new com.mn.cryptoapp.dto.CoinTransactionDTO(c.name, sum(c.quantity)) from Coin c group by c.name";
        TypedQuery<CoinTransactionDTO> query = entityManager.createQuery(jpql, CoinTransactionDTO.class);
        return query.getResultList();
    }

    public List<Coin> getByName(String name) {
        String jpql = "SELECT c FROM Coin c WHERE c.name LIKE :name";
        TypedQuery<Coin> query = entityManager.createQuery(jpql, Coin.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }

    @Transactional
    public boolean remove(int id) {
        Coin coin = entityManager.find(Coin.class, id);

        if (coin == null) throw new RuntimeException();

        entityManager.remove(coin);
        return true;
    }
}
