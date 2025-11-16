package com.example.project2group1.data;

public class UserRepository {
    private final UserDao dao;

    public UserRepository(UserDao dao) {
        this.dao = dao;
    }

    public void createUser(String username, String password, boolean isAdmin) throws Exception {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Please fill in all fields");
        }
        User existing = dao.getUser(username.trim());
        if (existing != null) {
            throw new IllegalStateException("User already exists");
        }
        dao.insert(new User(username.trim(), password, isAdmin));
    }

    public User validateLogin(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Please fill in all fields");
        }
        User u = dao.getUser(username.trim());
        if (u == null) throw new IllegalArgumentException("User not found");
        if (!u.password.equals(password)) throw new IllegalArgumentException("Incorrect password");
        return u;
    }
}
