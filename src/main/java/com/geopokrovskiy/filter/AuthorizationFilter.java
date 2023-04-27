package com.geopokrovskiy.filter;

import com.geopokrovskiy.DAO.DAO;
import com.geopokrovskiy.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class AuthorizationFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Cookie[] cookies = request.getCookies();
        String value = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("hash")) {
                    value = cookie.getValue();
                }
            }
        }

        System.out.println(request.getRequestURI());
        String loginURI = request.getContextPath() + "/user";
        String registerURI = request.getContextPath() + "/registration";

        boolean loginRequest = request.getRequestURI().contains(loginURI);
        boolean registerRequest = request.getRequestURI().contains(registerURI);
        if (request.getRequestURI().endsWith("js") || loginRequest || registerRequest
                || value != null && DAO.getObjectByParam("hash", value, User.class) != null) {
            filterChain.doFilter(request, response);
        } else {
            response.sendRedirect(loginURI + ".html");
        }
    }

    @Override
    public void destroy() {

    }
}
