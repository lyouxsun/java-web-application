package repository;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import model.User;

public class MemoryMemberRepository {
    private static MemoryMemberRepository repository;
    public static MemoryMemberRepository getInstance(){
        if(repository == null){
            repository = new MemoryMemberRepository();
            return repository;
        }
        else {
            return repository;
        }
    }

    private static Map<String, User> users = Maps.newHashMap();

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static User findUserById(String userId) {
        return users.get(userId);
    }

    public static Collection<User> findAll() {
        return users.values();
    }
}
