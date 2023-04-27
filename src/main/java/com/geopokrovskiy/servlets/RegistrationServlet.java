package com.geopokrovskiy.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.geopokrovskiy.DAO.DAO;
import com.geopokrovskiy.dto.ResponseResult;
import com.geopokrovskiy.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    {
        this.objectMapper.registerModule(new JavaTimeModule());
    }


    /**
     * post – осуществляет прием данных и производит регистрацию нового пользователя в системе.
     * Корректно обрабатывает существование пользователя в базе данных
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
        User user = this.objectMapper.readValue(req.getReader(), User.class);
        user.setRegDate(LocalDate.now());
        try {
            DAO.addObject(user);
            responseResult.setData(user);
            responseResult.setResult(true);
            DAO.closeOpenedSession();
        }
        catch (IllegalArgumentException exception){
            responseResult.setMessage(exception.getMessage());
            resp.setStatus(400);
        }
        this.objectMapper.writeValue(resp.getWriter(), responseResult);
    }
}
