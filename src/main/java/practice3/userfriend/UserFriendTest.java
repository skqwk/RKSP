package practice3.userfriend;

import io.reactivex.rxjava3.core.Observable;

import java.util.Random;

public class UserFriendTest {
    private static final UserFriend[] USER_FRIEND_LIST = new UserFriend[]{
            new UserFriend(1, 2),
            new UserFriend(1, 10),
            new UserFriend(2, 3),
            new UserFriend(2, 15),
            new UserFriend(3, 4),
            new UserFriend(3, 20),
            new UserFriend(4, 5),
            new UserFriend(4, 25),
            new UserFriend(5, 6),
            new UserFriend(5, 30),
            new UserFriend(6, 7),
            new UserFriend(6, 35)
    };

    public static void main(String[] args) {
        Observable.just(1, 2, 3, 4)
                .flatMap(UserFriendTest::getFriends)
                .subscribe(UserFriendTest::printUserFriend);
    }

    private static void printUserFriend(UserFriend userFriend) {
        System.out.printf("userId = %s, friendId = %s\n", userFriend.getUserId(), userFriend.getFriendId());
    }

    public static Observable<UserFriend> getFriends(int userId) {
        return Observable.fromArray(USER_FRIEND_LIST)
                .filter(userFriend -> userFriend.getUserId() == userId);
    }
}
