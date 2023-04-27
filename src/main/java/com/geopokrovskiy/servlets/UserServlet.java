package com.geopokrovskiy.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.geopokrovskiy.DAO.DAO;
import com.geopokrovskiy.DAO.HibernateUtil;
import com.geopokrovskiy.dto.ResponseResult;
import com.geopokrovskiy.model.User;
import com.geopokrovskiy.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/user")
public class UserServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    {
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * post – осуществляет проверку соответствия логина и пароля для пользователя в базе данных
     *
     * @param req
     * @param resp
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ResponseResult<User> responseResult = new ResponseResult<>();
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        String[] params = new String[] {"login", "password"};
        String[] params0 = new String[] {login, password};
        User user = (User) DAO.getObjectByParams(params, params0, User.class);
        DAO.closeOpenedSession();
        if (user != null) {
            String hash = StringUtil.generateHash();
            user.setHash(hash);
            DAO.updateObject(user);

            Cookie cookieHash = new Cookie("hash", hash);
            cookieHash.setMaxAge(10 * 60);
            cookieHash.setPath("/");
            resp.addCookie(cookieHash);
            Cookie cookieUserId = new Cookie("user", String.valueOf(user.getId()));
            cookieUserId.setMaxAge(10 * 60);
            cookieUserId.setPath("/");
            resp.addCookie(cookieUserId);

            responseResult.setResult(true);
            responseResult.setData(user);
        } else {
            responseResult.setMessage("Incorrect login or password.");
            resp.setStatus(400);
        }
        this.objectMapper.writeValue(resp.getWriter(), responseResult);
    }

    /**
     * get – осуществляет отображение пользователя с заданным id
     *
     * @param req
     * @param resp
     * @throws IOException
     */
    @Override
    protected synchronized void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        if(req.getParameter("id") == null){
                ResponseResult<List<User>> responseResult = new ResponseResult<>();
                List<User> users = DAO.getAllObjects(User.class);
                responseResult.setData(users);
                responseResult.setResult(true);
                this.objectMapper.writeValue(resp.getWriter(), responseResult);
                DAO.closeOpenedSession();
            }
        else {
            ResponseResult<User> responseResult = new ResponseResult<>();
            try {
                long id = Long.parseLong(req.getParameter("id"));
                User user = (User) DAO.getObjectById(id, User.class);
                DAO.closeOpenedSession();
                if (user != null) {
                    responseResult.setData(user);
                    responseResult.setResult(true);
                } else {
                    responseResult.setMessage("User with a given id does not exist.");
                    resp.setStatus(400);
                }
            } catch (NumberFormatException exception) {
                responseResult.setMessage("Incorrect id format.");
                resp.setStatus(400);
            }
            this.objectMapper.writeValue(resp.getWriter(), responseResult);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        String id = req.getParameter("id") ;
        ResponseResult<User> responseResult = new ResponseResult<>();
        if(id != null){
            try{
                User user = (User) DAO.getObjectById(Long.parseLong(id), User.class);
                DAO.closeOpenedSession();
                if(user != null){
                    user.setHash(null);
                    Cookie[] cookies = req.getCookies();
                    if(cookies != null){
                        for(Cookie cookie : cookies){
                            cookie.setValue(null);
                            cookie.setMaxAge(0);
                            cookie.setPath("/");
                            resp.addCookie(cookie);
                        }
                    } else {
                        responseResult.setMessage("No cookie");
                        resp.setStatus(400);
                    }
                }
                DAO.updateObject(user);
                responseResult.setData(user);
                responseResult.setResult(true);
            }
            catch (NumberFormatException e){
                resp.setStatus(400);
                responseResult.setMessage("Incorrect id format");
            }
        } else {
            responseResult.setMessage("Incorrect id");
            resp.setStatus(400);
        }
        this.objectMapper.writeValue(resp.getWriter(), responseResult);
    }

    /**
     * delete – осуществляет удаление пользователя с заданным id из базы данных,
     * а так же каскадное удаление всей информации, связанной с ним
     *
     * @param req
     * @param resp
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ResponseResult<User> responseResult = new ResponseResult<>();
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        try {
            long id = Long.parseLong(req.getParameter("id"));
            User toDelete = (User) DAO.getObjectById(id, User.class);
            if (toDelete != null) {
                DAO.deleteObjectById(id, User.class);
                responseResult.setData(toDelete);
                responseResult.setResult(true);
            } else {
                responseResult.setMessage("User with the given id cannot be deleted.");
                resp.setStatus(400);
            }
        } catch (NumberFormatException exception) {
            responseResult.setMessage("Incorrect id format.");
            resp.setStatus(400);
        }
        this.objectMapper.writeValue(resp.getWriter(), responseResult);
        DAO.closeOpenedSession();
    }
}
