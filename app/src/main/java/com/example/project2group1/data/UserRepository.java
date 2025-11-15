package com.example.project2group1.data;

public class UserRepository {
    private final UserDao dao;

    public UserRepository(UserDao dao) {
        this.dao = dao;
    }

    public boolean createUser(String username, String password, boolean isAdmin) throws Exception {
        if (username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Empty fields");
        }
        User existing = dao.getUser(username);
        if (existing != null) {
            throw new IllegalStateException("User already exists");
        }
        dao.insert(new User(username, password, isAdmin));
        return true;
    }

    public User validateLogin(String username, String password) throws Exception {
        User u = dao.getUser(username);
        if (u == null) throw new IllegalArgumentException("User not found");
        if (!u.password.equals(password)) throw new IllegalArgumentException("Incorrect password");
        return u;
    }
}
