package repository;

import com.google.common.collect.Maps;
import model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    public void addUser(User user) {
        System.out.println("[회원가입한 회원의 정보] 아이디 = " + user.getUserId() +
                ", 비밀번호 = " + user.getPassword() + ", 이름 = " + user.getName() + ", 이메일 = " + user.getEmail());
        users.put(user.getUserId(), user);
    }

    public User findUserById(String userId) {
        return users.get(userId);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
