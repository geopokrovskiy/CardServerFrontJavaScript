package com.geopokrovskiy;

import com.geopokrovskiy.DAO.DAO;
import com.geopokrovskiy.model.Category;
import com.geopokrovskiy.model.User;

public class Main {
    public static void main(String[] args) {
        User user = (User) DAO.getObjectById(1L, User.class);
        DAO.closeOpenedSession();
        Category category = new Category("politics", user);
        DAO.updateObject(category);
    }
}