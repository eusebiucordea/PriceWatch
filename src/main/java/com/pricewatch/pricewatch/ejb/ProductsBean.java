package com.pricewatch.pricewatch.ejb;

import com.pricewatch.pricewatch.common.ProductDto;
import com.pricewatch.pricewatch.common.ProductHistoryDto;
import com.pricewatch.pricewatch.common.ProductLinkDto;
import com.pricewatch.pricewatch.entities.ProductLink;
import com.pricewatch.pricewatch.entities.Products;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class ProductsBean {
    private static final Logger LOG = Logger.getLogger(ProductsBean.class.getName());

    @PersistenceContext
    EntityManager entityManager;

    public List<ProductDto> findAllProducts() {
        LOG.info("Se extrag toate produsele din baza de date");

        // interogare baza de date
        TypedQuery<Products> query = entityManager.createQuery("SELECT p FROM Products p", Products.class);
        List<Products> productsEntities = query.getResultList();

        // list  de DTO uri
        List<ProductDto> productsDtoList = new ArrayList<>();

        // transformam entity in dto
        for (Products entity : productsEntities) {
            ProductDto dto = new ProductDto(
                    entity.getId(),
                    entity.getName(),
                    entity.getImage_url(),
                    entity.getAll_time_low(),
                    entity.getCurrent_price()
            );
            productsDtoList.add(dto);
        }
        return productsDtoList;
    }

    public ProductDto findById(Long id) {
        // cautam produsul principal
        Products entity = entityManager.find(Products.class, id);

        if (entity != null) {
            // cream dto ul produsului
            ProductDto productDto = new ProductDto(
                    entity.getId(),
                    entity.getName(),
                    entity.getImage_url(),
                    entity.getAll_time_low(),
                    entity.getCurrent_price()
            );

            // extragem link urile asociate acestui produs
            List<com.pricewatch.pricewatch.entities.ProductLink> linkEntities = entityManager.createQuery(
                            "SELECT pl FROM ProductLink pl WHERE pl.product.id = :prodId", com.pricewatch.pricewatch.entities.ProductLink.class)
                    .setParameter("prodId", id)
                    .getResultList();

            List<ProductLinkDto> linkDtos = new ArrayList<>();

            for (com.pricewatch.pricewatch.entities.ProductLink linkEntity : linkEntities) {
                // cream dto ul pentru link
                ProductLinkDto linkDto = new ProductLinkDto(
                        linkEntity.getId(),
                        linkEntity.getStoreName(),
                        linkEntity.getUrl(),
                        linkEntity.getLastPrice(),
                        linkEntity.getLastChecked()
                );

                // extragem istoricul de preturi pentru acest link
                List<com.pricewatch.pricewatch.entities.PriceHistory> historyEntities = entityManager.createQuery(
                                "SELECT ph FROM PriceHistory ph WHERE ph.productLink.id = :linkId ORDER BY ph.recordedAt ASC", com.pricewatch.pricewatch.entities.PriceHistory.class)
                        .setParameter("linkId", linkEntity.getId())
                        .getResultList();

                List<ProductHistoryDto> historyDtos = new ArrayList<>();
                for (com.pricewatch.pricewatch.entities.PriceHistory histEntity : historyEntities) {
                    // cream dto ul pentru istoric
                    historyDtos.add(new ProductHistoryDto(
                            histEntity.getId(),
                            histEntity.getPrice(),
                            histEntity.getRecordedAt()
                    ));
                }

                // punem istoricul in link apoi link ul in lista finala
                linkDto.setPriceHistories(historyDtos);
                linkDtos.add(linkDto);
            }

            // atașam lista de link uri la produsul nostru final
            productDto.setLinks(linkDtos);

            return productDto;
        }
        return null;
    }

    public void updateProduct(Long id, String name, Double currentPrice, Double allTimeLow) {
        Products entity = entityManager.find(Products.class, id);
        if (entity != null) {
            entity.setName(name);
            entity.setCurrent_price(currentPrice);
            entity.setAll_time_low(allTimeLow);
            entityManager.merge(entity);
        }
    }

    public void deleteProduct(Long productId) {
        Products product = entityManager.find(Products.class, productId);
        if (product != null) {
            // stergem produsul din toate listele de urmarire (WatchList) ale tuturor utilizatorilor
            entityManager.createQuery("DELETE FROM WatchList w WHERE w.productId = :prodId")
                    .setParameter("prodId", productId)
                    .executeUpdate();

            // stergem istoricul preturilor asociat cu linkurile acestui produs
            entityManager.createQuery("DELETE FROM PriceHistory ph WHERE ph.productLink.product.id = :prodId")
                    .setParameter("prodId", productId)
                    .executeUpdate();

            // stergem linkurile asociate produsului
            entityManager.createQuery("DELETE FROM ProductLink pl WHERE pl.product.id = :prodId")
                    .setParameter("prodId", productId)
                    .executeUpdate();

            // la final stergem produsul
            entityManager.remove(product);
        }
    }
}
