package com.pricewatch.pricewatch.ejb;

import com.pricewatch.pricewatch.common.ProductDto;
import com.pricewatch.pricewatch.entities.Products;
import com.pricewatch.pricewatch.entities.WatchList;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class WatchlistBean {
    private static final Logger LOG = Logger.getLogger(WatchlistBean.class.getName());

    @PersistenceContext
    EntityManager entityManager;

    // adauga un produs in watchlist ul utilizatorului
    public void addToWatchlist(int userId, int productId) {
        LOG.info("adding product " + productId + " to watchlist for user " + userId);

        // verificam daca exista deja pentru a evita erori
        if (!isProductInWatchlist(userId, productId)) {
            WatchList watchListEntry = new WatchList(userId, productId);
            entityManager.persist(watchListEntry);
        } else {
            LOG.info("product " + productId + " is already in watchlist for user " + userId);
        }
    }

    // sterge un produs din watchlist ul utilizatorului
    public void removeFromWatchlist(int userId, int productId) {
        LOG.info("removing product " + productId + " from watchlist for user " + userId);

        entityManager.createQuery("DELETE FROM WatchList w WHERE w.userId = :userId AND w.productId = :productId")
                .setParameter("userId", userId)
                .setParameter("productId", productId)
                .executeUpdate();
    }

    // verifica daca un produs se afla deja in watchlist
    public boolean isProductInWatchlist(int userId, int productId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(w) FROM WatchList w WHERE w.userId = :userId AND w.productId = :productId", Long.class)
                .setParameter("userId", userId)
                .setParameter("productId", productId)
                .getSingleResult();

        return count > 0;
    }

    // extrage toate produsele din watchlist ul utilizatorului
    public List<ProductDto> getUserWatchlist(int userId) {
        LOG.info("extracting watchlist for user " + userId);

        // join manual in jpql intre products si watchlist
        TypedQuery<Products> query = entityManager.createQuery(
                "SELECT p FROM Products p, WatchList w WHERE p.id = w.productId AND w.userId = :userId ORDER BY w.addedAt DESC",
                Products.class
        );
        query.setParameter("userId", userId);
        List<Products> productsEntities = query.getResultList();

        List<ProductDto> watchlistDtoList = new ArrayList<>();

        // transformam entity in dto
        for (Products entity : productsEntities) {
            ProductDto dto = new ProductDto(
                    entity.getId(),
                    entity.getName(),
                    entity.getImage_url(),
                    entity.getAll_time_low(),
                    entity.getCurrent_price()
            );
            watchlistDtoList.add(dto);
        }

        return watchlistDtoList;
    }
}