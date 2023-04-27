package com.geopokrovskiy.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.geopokrovskiy.DAO.DAO;
import com.geopokrovskiy.dto.ResponseResult;
import com.geopokrovskiy.model.Category;
import com.geopokrovskiy.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/category")
public class CategoryServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    {
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * post – осуществляет добавление новой категории для пользователя с заданным id в базу данных
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ResponseResult<Category> responseResult = new ResponseResult<>();
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        try {
            String name = req.getParameter("name");
            long user_id = Long.parseLong(req.getParameter("user_id"));
            User user = (User) DAO.getObjectById(user_id, User.class);
            Category category = new Category(name, user);
            DAO.addObject(category);
            responseResult.setResult(true);
            responseResult.setData(category);
            DAO.closeOpenedSession();
        } catch (NumberFormatException exception) {
            responseResult.setMessage("Incorrect input!");
            resp.setStatus(400);
        } catch (IllegalArgumentException exception) {
            responseResult.setMessage("This category already exists.");
            resp.setStatus(400);
        }
        this.objectMapper.writeValue(resp.getWriter(), responseResult);
    }


    @Override
    protected synchronized void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        if (req.getParameter("user_id") != null) {
            ResponseResult<List<Category>> responseResult = new ResponseResult<>();
                try {
                    long userId = Long.parseLong(req.getParameter("user_id"));

                    User user = (User) DAO.getObjectById(userId, User.class);
                    List<Category> categoryList = user.getCategories();
                    // System.out.println(categoryList);
                    responseResult.setData(categoryList);
                    responseResult.setResult(true);
                } catch (NumberFormatException exception) {
                    responseResult.setMessage("Incorrect number format.");
                }
            this.objectMapper.writeValue(resp.getWriter(), responseResult);
        } else if (req.getParameter("id") != null) {
            ResponseResult<Category> responseResult = new ResponseResult<>();
            try {
                long id = Long.parseLong(req.getParameter("id"));
                Category category = (Category) DAO.getObjectById(id, Category.class);
                if (category != null) {
                    responseResult.setData(category);
                    responseResult.setResult(true);
                } else {
                    responseResult.setMessage("The category cannot be obtained.");
                    resp.setStatus(400);
                }
            } catch (NumberFormatException exception) {
                responseResult.setMessage("Incorrect number format.");
                resp.setStatus(400);
            }
            this.objectMapper.writeValue(resp.getWriter(), responseResult);
        }
        DAO.closeOpenedSession();
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        ResponseResult<Category> responseResult = new ResponseResult<>();
        try {
            //Category category = this.objectMapper.readValue(req.getReader(), Category.class);
            long categoryId = Long.parseLong(req.getParameter("id"));
            String categoryName = req.getParameter("name");
            if (DAO.getObjectById(categoryId, Category.class) != null) {
                Category oldCategory = (Category) DAO.getObjectById(categoryId, Category.class);
                oldCategory.setName(categoryName);
                DAO.closeOpenedSession();
                DAO.updateObject(oldCategory);
                responseResult.setData(oldCategory);
                responseResult.setResult(true);
                DAO.closeOpenedSession();
            } else {
                responseResult.setMessage("Category with given id does not exist.");
                resp.setStatus(400);
            }
        } catch (NumberFormatException e) {
            responseResult.setMessage("Incorrect id format.");
            resp.setStatus(400);
        }
        this.objectMapper.writeValue(resp.getWriter(), responseResult);
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        ResponseResult<Category> responseResult = new ResponseResult<>();
        try {
            long id = Long.parseLong(req.getParameter("id"));
            Category toDelete = (Category) DAO.getObjectById(id, Category.class);
            DAO.closeOpenedSession();
            if (toDelete != null) {
                DAO.deleteObject(toDelete);
                responseResult.setData(toDelete);
                responseResult.setResult(true);
            } else {
                responseResult.setMessage("The object cannot be deleted.");
                resp.setStatus(400);
            }
            DAO.closeOpenedSession();
        } catch (NumberFormatException exception) {
            responseResult.setMessage("Incorrect id format.");
            resp.setStatus(400);
        }
        this.objectMapper.writeValue(resp.getWriter(), responseResult);
    }
}
