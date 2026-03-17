package com.pricewatch.pricewatch.ejb;

import com.pricewatch.pricewatch.common.UserDto;
import com.pricewatch.pricewatch.entities.Users;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class UsersBean {
    private static final Logger LOG = Logger.getLogger(UsersBean.class.getName());

    @PersistenceContext
    EntityManager entityManager;

    public List<UserDto> findAllUsers() {
        LOG.info("Se extrag toti userii din baza de date");
        TypedQuery<Users> query = entityManager.createQuery("SELECT u FROM Users u", Users.class);
        List<Users> usersEntities = query.getResultList();

        List<UserDto> usersDtoList = new ArrayList<>();

        for (Users entity : usersEntities) {
            UserDto dto = new UserDto(
                    entity.getId(),
                    entity.getUsername(),
                    entity.getEmail(),
                    entity.getRole() != null ? entity.getRole().getRoleName() : "Unknown"
            );
            usersDtoList.add(dto);
        }
        return usersDtoList;
    }

    public void deleteUser(Long userId) {
        LOG.info("Se sterge utilizatorul cu ID-ul: " + userId);
        Users user = entityManager.find(Users.class, userId);
        if (user != null) {
            entityManager.remove(user);
        }
    }

}
