package com.pricewatch.pricewatch.ejb;

import com.pricewatch.pricewatch.common.UserDto;
import com.pricewatch.pricewatch.entities.Roles;
import com.pricewatch.pricewatch.entities.Users;
import jakarta.ejb.EJB;
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
            // extragem entitatea completa pentru a evita erorile de conversie
            TypedQuery<Users> query = entityManager.createQuery(
                    "SELECT u FROM Users u WHERE u.username = :username", Users.class);
            query.setParameter("username", username);

            Users user = query.getSingleResult();

            // luam id ul din entitate si il facem integer
            return user.getId() != null ? user.getId().intValue() : null;

        } catch (NoResultException e) {
            LOG.warning("No user found with username: " + username);
            return null;
        } catch (Exception e) {
            LOG.severe("Error finding ID for user: " + username + " - " + e.getMessage());
            // printam in consola linia exacta unde a picat in caz de probleme
            e.printStackTrace();
            return null;
        }
    }


    @EJB
    PasswordBean passwordBean;

    public void createUser(String username, String email, String password, Collection<String> rolesList) {
        LOG.info("creating new user: " + username);

        Users newUser = new Users();
        newUser.setUsername(username);
        newUser.setEmail(email);

        // hash uim parola
        newUser.setPassword(passwordBean.convertToSha256(password));

        if (rolesList != null && !rolesList.isEmpty()) {
            // luam primul rol din lista
            String roleNameStr = rolesList.iterator().next();
            Roles userRole = null;

            try {
                // incercam sa gasim rolul in baza de date
                TypedQuery<Roles> query = entityManager.createQuery(
                        "SELECT r FROM Roles r WHERE r.roleName = :roleName", Roles.class);
                query.setParameter("roleName", roleNameStr);
                userRole = query.getSingleResult();

            } catch (NoResultException e) {
                // daca rolul nu exista deloc in baza de date il cream acum
                LOG.info("role " + roleNameStr + " not found, creating a new one");
                userRole = new Roles();
                userRole.setroleName(roleNameStr);

                // nu mai setam username ul pe rol deoarece acest rol e impartit de mai multi useri
                entityManager.persist(userRole);
            }

            // legam rolul gasit sau creat de noul utilizator
            newUser.setRole(userRole);
        }

        // la final salvam utilizatorul
        entityManager.persist(newUser);
    }

    private void assignRolesToUser(String username, Collection<String> roles) {
        LOG.info("Assigning roles for user: " + username);

        for (String roleName : roles) {
            // instantiem entitatea folosind un nume diferit fata de string
            Roles userRole = new Roles();

            // setam proprietatile pe instanta creata nu pe clasa
            userRole.setUsername(username);
            userRole.setroleName(roleName);

            // salvam rolul in baza de date
            entityManager.persist(userRole);
        }
    }

    }