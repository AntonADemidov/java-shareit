package ru.practicum.shareit;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDtoForUser;
import ru.practicum.shareit.user.dto.User;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TestHelper {
    static String actionWithBookings = "/bookings/";
    static String actionWithItems = "/items/";
    static String actionWithUsers = "/users/";
    static String actionWithRequests = "/requests/";
    static String userHeader = "X-Sharer-User-Id";
    static String expId = "$.id";
    static String expStart = "$.start";
    static String expEnd = "$.end";
    static String expStatus = "$.status";
    static String expName = "$.name";
    static String expDescription = "$.description";
    static String expEmail = "$.email";
    static String expAvailable = "$.available";
    static String expText = "$.text";
    static String expAuthorName = "$.authorName";
    static String expCreated = "$.created";
    static String expItems = "$.items";
    static String expRequestId = "$.requestId";
    static String expBookerId = "$.bookerId";
    static String expItemId = "$.itemId";
    static String expBasic = "$";
    static String approved = "approved";
    static String text = "text";
    static String shoeBrush = "Хотел бы воспользоваться щёткой для обуви";
    static User user1 = new User(1L, "user", "user@user.com");
    static User user2 = new User(2L, "update", "update@user.com");
    static User userWithoutId1 = new User("user", "user@user.com");
    static User userWithoutId2 = new User("update", "update@user.com");
    static User userWithoutId3 = new User("user3", "user3@user.com");
    static ItemDtoForUser item1 = new ItemDtoForUser(1L, "Дрель", "Простая дрель", true, user1, null, null, null, null);
    static ItemDtoForUser item2 = new ItemDtoForUser(2L, "Отвертка", "Аккумуляторная отвертка", true, user1, null, null, null, null);
    static ItemDtoForUser itemWithoutId1 = new ItemDtoForUser("Дрель", "Простая дрель", true, user1);

    public static String getActionWithBookings() {
        return actionWithBookings;
    }

    public static String getUserHeader() {
        return userHeader;
    }

    public static String getExpId() {
        return expId;
    }

    public static String getExpStart() {
        return expStart;
    }

    public static String getExpEnd() {
        return expEnd;
    }

    public static String getExpStatus() {
        return expStatus;
    }

    public static String getExpBasic() {
        return expBasic;
    }

    public static String getApproved() {
        return approved;
    }

    public static String getActionWithItems() {
        return actionWithItems;
    }

    public static String getExpName() {
        return expName;
    }

    public static String getExpAvailable() {
        return expAvailable;
    }

    public static String getText() {
        return text;
    }

    public static String getExpText() {
        return expText;
    }

    public static String getExpAuthorName() {
        return expAuthorName;
    }

    public static String getExpCreated() {
        return expCreated;
    }

    public static String getActionWithUsers() {
        return actionWithUsers;
    }

    public static String getExpEmail() {
        return expEmail;
    }

    public static String getActionWithRequests() {
        return actionWithRequests;
    }

    public static String getExpDescription() {
        return expDescription;
    }

    public static String getExpItems() {
        return expItems;
    }

    public static User getUser1() {
        return user1;
    }

    public static User getUser2() {
        return user2;
    }

    public static ItemDtoForUser getItem1() {
        return item1;
    }

    public static ItemDtoForUser getItem2() {
        return item2;
    }

    public static User getUserWithoutId1() {
        return userWithoutId1;
    }

    public static User getUserWithoutId2() {
        return userWithoutId2;
    }

    public static String getShoeBrush() {
        return shoeBrush;
    }

    public static ItemDtoForUser getItemWithoutId1() {
        return itemWithoutId1;
    }

    public static String getExpRequestId() {
        return expRequestId;
    }

    public static User getUserWithoutId3() {
        return userWithoutId3;
    }

    public static String getExpBookerId() {
        return expBookerId;
    }

    public static String getExpItemId() {
        return expItemId;
    }
}