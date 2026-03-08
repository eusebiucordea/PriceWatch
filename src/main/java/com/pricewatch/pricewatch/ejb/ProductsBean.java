package com.pricewatch.pricewatch.ejb;

import com.pricewatch.pricewatch.common.ProductDto;
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
}
