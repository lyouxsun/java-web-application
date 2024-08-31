package repository;

import com.google.common.collect.Maps;
import model.User;

import java.util.Collection;
import java.util.Map;

public class MemoryMemberRepository {
    private static MemoryMemberRepository repository;

    public static MemoryMemberRepository getInstance() {
        if (repository == null) {
            repository = new MemoryMemberRepository();
            return repository;
        } else {
            return repository;
        }
    }

    private static Map<String, User> users = Maps.newHashMap();

    public static void addUser(User user) {
        System.out.println("[회원가입한 회원의 정보] 아이디 = " + user.getUserId() +
                ", 비밀번호 = " + user.getPassword() + ", 이름 = " + user.getName() + ", 이메일 = " + user.getEmail());
        users.put(user.getUserId(), user);
    }

    public static User findUserById(String userId) {
        return users.get(userId);
    }

    public static Collection<User> findAll() {
        return users.values();
    }
}
