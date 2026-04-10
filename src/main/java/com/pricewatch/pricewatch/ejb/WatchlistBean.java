package com.pricewatch.pricewatch.ejb;

import com.pricewatch.pricewatch.common.AlertDTO;
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

    // seteaza procentajul de reducere dorit pentru un anumit produs din watchlist
    public void setPriceAlert(int userId, int productId, int targetDiscount) {
        LOG.info("setting price alert of " + targetDiscount + "% for product " + productId + " and user " + userId);

        try {
            // actualizam campul direct in baza de date
            int updatedRows = entityManager.createQuery(
                            "UPDATE WatchList w SET w.targetDiscount = :targetDiscount WHERE w.userId = :userId AND w.productId = :productId")
                    .setParameter("targetDiscount", targetDiscount)
                    .setParameter("userId", userId)
                    .setParameter("productId", productId)
                    .executeUpdate();

            // aruncam o eroare daca produsul nu a fost gasit in lista utilizatorului
            if (updatedRows == 0) {
                LOG.warning("could not set alert product " + productId + " is not in watchlist for user " + userId);
                throw new IllegalArgumentException("Product is not in your watchlist");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            // inregistram eroarea in loguri si aruncam o exceptie generala pentru servlet
            LOG.severe("error setting price alert for user " + userId + " on product " + productId + ": " + e.getMessage());
            throw new RuntimeException("Error saving price alert", e);
        }
    }

    // aduce alertele din dashboard care au target_discount > 0
    public List<AlertDTO> getActiveAlerts(int userId) {
        // luam id-ul, numele, pretul si discountul setat, DOAR daca discountul este mai mare ca 0
        String query = "SELECT new com.pricewatch.pricewatch.common.AlertDTO(p.id, p.name, p.current_price, w.targetDiscount) " +
                "FROM Products p JOIN WatchList w ON p.id = w.productId " +
                "WHERE w.userId = :userId AND w.targetDiscount > 0";

        return entityManager.createQuery(query, AlertDTO.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public void removeAlert(int userId, Long productId) {
        LOG.info("Attempting to remove alert for user " + userId + " and product " + productId);

        int result = entityManager.createQuery(
                        "UPDATE WatchList w SET w.targetDiscount = NULL " +
                                "WHERE w.userId = :userId AND w.productId = :productId")
                .setParameter("userId", userId)
                .setParameter("productId", productId)
                .executeUpdate();

        LOG.info("Rows updated: " + result); // daca aici scrie 0, inseamna ca WHERE-ul nu a gasit nimic
    }

}