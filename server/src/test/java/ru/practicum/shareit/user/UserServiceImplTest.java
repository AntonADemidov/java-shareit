package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserDtoFromUser;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Data
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceImplTest {
    UserService service;
    EntityManager em;
    User user = TestHelper.getUserWithoutId1();

    @AfterEach
    void deleteAll() {
        service.deleteAll();
    }

    @Test
    void createUserBasicTest() throws Exception {
        User savedUser = service.createUser(user);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User newUser = query
                .setParameter("email", user.getEmail())
                .getSingleResult();

        assertThat(newUser.getId(), notNullValue());
        assertThat(newUser.getId(), equalTo(savedUser.getId()));
        assertThat(newUser.getName(), equalTo(user.getName()));
        assertThat(newUser.getEmail(), equalTo(user.getEmail()));
    }

    private UserDtoFromUser makeUserDto() {
        UserDtoFromUser user = new UserDtoFromUser();
        user.setName("update");
        user.setEmail("update@user.com");
        return user;
    }

    @Test
    void updateUserWithAllDataTest() throws Exception {
        User savedUser = service.createUser(user);

        UserDtoFromUser userDto = makeUserDto();
        service.updateUser(userDto, savedUser.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User newUser = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(newUser.getId(), notNullValue());
        assertThat(newUser.getId(), equalTo(savedUser.getId()));
        assertThat(newUser.getName(), equalTo(userDto.getName()));
        assertThat(newUser.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUserWithoutEmailTest() throws Exception {
        User savedUser = service.createUser(user);

        UserDtoFromUser userDto = makeUserDto();
        userDto.setEmail(null);

        service.updateUser(userDto, savedUser.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User newUser = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(newUser.getId(), notNullValue());
        assertThat(newUser.getId(), equalTo(savedUser.getId()));
        assertThat(newUser.getName(), equalTo(userDto.getName()));
        assertThat(newUser.getEmail(), equalTo(savedUser.getEmail()));
    }

    @Test
    void updateUserWithoutNameTest() throws Exception {
        User savedUser = service.createUser(user);

        UserDtoFromUser userDto = makeUserDto();
        userDto.setName(null);

        service.updateUser(userDto, savedUser.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User newUser = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(newUser.getId(), notNullValue());
        assertThat(newUser.getId(), equalTo(savedUser.getId()));
        assertThat(newUser.getName(), equalTo(savedUser.getName()));
        assertThat(newUser.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getUserByIdWithCorrectIdTest() throws Exception {
        User newUser = service.createUser(user);

        User secondUser = service.getUserById(newUser.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User thirdUser = query
                .setParameter("id", newUser.getId())
                .getSingleResult();

        assertThat(secondUser.getId(), notNullValue());
        assertThat(thirdUser.getId(), notNullValue());
        assertThat(secondUser.getId(), equalTo(thirdUser.getId()));
        assertThat(secondUser.getName(), equalTo(thirdUser.getName()));
        assertThat(secondUser.getEmail(), equalTo(thirdUser.getEmail()));
    }

    @Test
    void getUserByIdWithIncorrectIdTest() {
        Long id = 999L;

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> service.getUserById(id));
        assertEquals(String.format("Пользователь с id #%d отсутствует в базе.", id), exception.getMessage());
    }

    @Test
    void deleteUserTest() throws Exception {
        User newUser = service.createUser(user);

        service.deleteUser(newUser.getId());

        final NoResultException exception = assertThrows(
                NoResultException.class,
                () -> {
                    TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
                    query
                            .setParameter("email", newUser.getEmail())
                            .getSingleResult();
                });
        assertEquals("No entity found for query", exception.getMessage());
    }

    @Test
    void deleteAllTest() throws Exception {
        User newUser = service.createUser(user);

        service.deleteAll();

        final NoResultException exception = assertThrows(
                NoResultException.class,
                () -> {
                    TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
                    query
                            .setParameter("email", newUser.getEmail())
                            .getSingleResult();
                });
        assertEquals("No entity found for query", exception.getMessage());
    }

    @Test
    void findAllUsersTest() throws Exception {
        List<User> sourceUsers = List.of(
                new User("Ivan", "ivan@email"),
                new User("Petr", "petr@email"),
                new User("Vasilii", "vasilii@email")
        );

        for (User user : sourceUsers) {
            service.createUser(user);
        }
        Collection<User> targetUsers = service.findAllUsers();
        assertThat(targetUsers, hasSize(sourceUsers.size()));

        for (User user : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(user.getName())),
                    hasProperty("email", equalTo(user.getEmail()))
            )));
        }
    }
}