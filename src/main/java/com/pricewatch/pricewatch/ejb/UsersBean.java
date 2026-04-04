package com.pricewatch.pricewatch.ejb;

import com.pricewatch.pricewatch.common.UserDto;
import com.pricewatch.pricewatch.entities.Users;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class UsersBean {
    private static final Logger LOG = Logger.getLogger(UsersBean.class.getName());

    @PersistenceContext
    EntityManager entityManager;

    public List<UserDto> findAllUsers() {
        LOG.info("Fetching all users from database");
        TypedQuery<Users> query = entityManager.createQuery("SELECT u FROM Users u", Users.class);
        List<Users> usersEntities = query.getResultList();

        List<UserDto> usersDtoList = new ArrayList<>();

        for (Users entity : usersEntities) {
            UserDto dto = new UserDto(
                    entity.getId(),
                    entity.getUsername(),
                    entity.getEmail(),
                    entity.getRole() != null ? entity.getRole().getroleName() : "Unknown"
            );
            usersDtoList.add(dto);
        }
        return usersDtoList;
    }

    public void deleteUser(Long userId) {
        LOG.info("Deleting user with ID: " + userId);
        Users user = entityManager.find(Users.class, userId);
        if (user != null) {
            entityManager.remove(user);
        }
    }

    public Integer findUserIdByUsername(String username) {
        LOG.info("Searching ID for username: " + username);

        try {
            // selectam doar id ul pentru eficienta si evitam erorile de conversie
            TypedQuery<Number> query = entityManager.createQuery(
                    "SELECT u.id FROM Users u WHERE u.username = :username", Number.class);
            query.setParameter("username", username);

            Number id = query.getSingleResult();

            // convertim in integer pentru a se potrivi in sesiune
            return id != null ? id.intValue() : null;

        } catch (NoResultException e) {
            LOG.warning("No user found with username: " + username);
            return null;
        } catch (Exception e) {
            LOG.severe("Error finding ID for user: " + username + " - " + e.getMessage());
            return null;
        }
    }

}