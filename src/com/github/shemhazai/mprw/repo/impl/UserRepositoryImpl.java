package com.github.shemhazai.mprw.repo.impl;

import com.github.shemhazai.mprw.domain.User;
import com.github.shemhazai.mprw.repo.UserRepository;
import com.github.shemhazai.mprw.repo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl() {

    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User selectUserById(int id) {
        try {
            String sql = "select * from user where id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new UserMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("User with id: " + id + " doesn't exist!");
        }
    }

    @Override
    public User selectUserByEmail(String email) {
        try {
            String sql = "select * from user where email = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, new UserMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("User with email: " + email + " doesn't exist!");
        }
    }

    @Override
    public List<User> selectAllUsers() {
        String sql = "select * from user";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    @Override
    public List<User> selectUsersWithEmailAlert() {
        String sql = "select * from user where mailAlert != 0 and verified != 0";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    @Override
    public boolean existsUserWithEmail(String email) {
        String sql = "select count(*) from user where email = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class) != 0;
    }

    @Override
    @Transactional
    public User createUser(String email) {
        String sql = "insert into user (email) values (?)";
        jdbcTemplate.update(sql, new Object[]{email});

        sql = "select max(id) from user";
        int id = jdbcTemplate.queryForObject(sql, Integer.class);
        return selectUserById(id);
    }

    @Override
    public void updateUser(int id, User user) {
        String sql = "update user set email = ?, passwordHash = ?, firstName = ?, lastName = ?,"
                + " phone = ?, verified = ?, mailAlert = ?, phoneAlert = ? where id = ?";
        jdbcTemplate.update(sql,
                new Object[]{user.getEmail(), user.getHashedPassword(), user.getFirstName(),
                        user.getLastName(), user.getPhone(), user.isVerified() ? 1 : 0,
                        user.isMailAlert() ? 1 : 0, user.isPhoneAlert() ? 1 : 0, id});
    }

    @Override
    public void updateUserFirstName(int id, String firstName) {
        String sql = "update user set firstName = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{firstName, id});
    }

    @Override
    public void updateUserLastName(int id, String lastName) {
        String sql = "update user set lastName = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{lastName, id});
    }

    @Override
    public void updateUserEmail(int id, String email) {
        String sql = "update user set email = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{email, id});
    }

    @Override
    public void updateUserPasswordHash(int id, String passwordHash) {
        String sql = "update user set passwordHash = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{passwordHash, id});
    }

    @Override
    public void updateUserPhone(int id, String phone) {
        String sql = "update user set phone = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{phone, id});
    }

    @Override
    public void updateUserMailAlert(int id, boolean mailAlert) {
        String sql = "update user set mailAlert = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{mailAlert ? 1 : 0, id});
    }

    @Override
    public void updateUserPhoneAlert(int id, boolean phoneAlert) {
        String sql = "update user set phoneAlert = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{phoneAlert ? 1 : 0, id});
    }

    @Override
    public void updateUserVerified(int id, boolean verified) {
        String sql = "update user set verified = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{verified ? 1 : 0, id});
    }

    @Override
    public void deleteUser(int id) {
        String sql = "delete from user where id = ?";
        jdbcTemplate.update(sql, new Object[]{id});
    }
}
