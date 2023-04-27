package com.geopokrovskiy.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.geopokrovskiy.DAO.DAO;
import com.geopokrovskiy.dto.ResponseResult;
import com.geopokrovskiy.model.Card;
import com.geopokrovskiy.model.Category;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/card")
public class CardServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    {
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        ResponseResult<Card> responseResult = new ResponseResult<>();
        try {
            long categoryId = Long.parseLong(req.getParameter("category_id"));
            Category category = (Category) DAO.getObjectById(categoryId, Category.class);
           // Card card = this.objectMapper.readValue(req.getReader(), Card.class);
            String question = req.getParameter("question");
            String answer = req.getParameter("answer");
            Card card = new Card();
            card.setQuestion(question);
            card.setAnswer(answer);
            DAO.closeOpenedSession();
            String[] prm = new String[] {"question", "answer", "category"};
            Object[] prm0 = new Object[] {question, answer, category};
            long duplicateCardsCount = ((List<Card>) DAO.getObjectsByParams(prm, prm0, Card.class)).size();
            if(duplicateCardsCount > 0) throw new IllegalArgumentException();
            DAO.closeOpenedSession();
            card.setCategory(category);
            card.setCreationDate(LocalDate.now());
            DAO.addObject(card);
            DAO.closeOpenedSession();
            responseResult.setData(card);
            responseResult.setResult(true);
            DAO.closeOpenedSession();
        } catch (NumberFormatException e) {
            responseResult.setMessage("Incorrect id format.");
            resp.setStatus(400);
        } catch (IllegalArgumentException e) {
            responseResult.setMessage("The card has been already added.");
            resp.setStatus(400);
        }
        this.objectMapper.writeValue(resp.getWriter(), responseResult);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        if (req.getParameter("id") == null
                && req.getParameter("user_id") == null
                && req.getParameter("category_id") == null) {
            ResponseResult<List<Card>> responseResult = new ResponseResult<>();
            List<Card> cardList = DAO.getAllObjects(Card.class);
            responseResult.setData(cardList);
            responseResult.setResult(true);
            this.objectMapper.writeValue(resp.getWriter(), responseResult);
            DAO.closeOpenedSession();
        } else if (req.getParameter("user_id") != null) {
            ResponseResult<List<Card>> responseResult = new ResponseResult<>();
            try {
                long userId = Long.parseLong(req.getParameter("user_id"));
                User user = (User) DAO.getObjectById(userId, User.class);
                if (user != null) {
                    List<Category> categories = user.getCategories();
                    List<Card> cardList = new ArrayList<>();
                    categories.stream().forEach(x -> cardList.addAll(x.getCardList()));
                    responseResult.setData(cardList);
                    responseResult.setResult(true);
                } else {
                    responseResult.setMessage("User with such id does not exist.");
                    resp.setStatus(400);
                }
            } catch (NumberFormatException exception) {
                responseResult.setMessage("Incorrect id format.");
                resp.setStatus(400);
            }
            this.objectMapper.writeValue(resp.getWriter(), responseResult);
            DAO.closeOpenedSession();
        } else if (req.getParameter("category_id") != null) {
            ResponseResult<List<Card>> responseResult = new ResponseResult<>();
            try {
                long categoryId = Long.parseLong(req.getParameter("category_id"));
                Category category = (Category) DAO.getObjectById(categoryId, Category.class);
                if (category != null) {
                    List<Card> cardList = category.getCardList();
                    responseResult.setData(cardList);
                    responseResult.setResult(true);
                } else {
                    responseResult.setMessage("A category with such id does not exist.");
                    resp.setStatus(400);
                }
            } catch (NumberFormatException exception) {
                responseResult.setMessage("Incorrect id format.");
                resp.setStatus(400);
            }
            this.objectMapper.writeValue(resp.getWriter(), responseResult);
            DAO.closeOpenedSession();
        } else if (req.getParameter("id") != null) {
            ResponseResult<Card> responseResult = new ResponseResult<>();
            try {
                long id = Long.parseLong(req.getParameter("id"));
                Card card = (Card) DAO.getObjectById(id, Card.class);
                if (card != null) {
                    responseResult.setData(card);
                    responseResult.setResult(true);
                } else {
                    responseResult.setMessage("A card with such id does not exist.");
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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        ResponseResult<Card> responseResult = new ResponseResult<>();
        try {
          //  Card card = this.objectMapper.readValue(req.getReader(), Card.class);
            Long cardId = Long.parseLong(req.getParameter("id"));
            String question = req.getParameter("question");
            String answer = req.getParameter("answer");
            if (DAO.getObjectById(cardId, Card.class) != null) {
                Card oldCard = (Card) DAO.getObjectById(cardId, Card.class);
                oldCard.setQuestion(question);
                oldCard.setAnswer(answer);
                DAO.closeOpenedSession();
                DAO.updateObject(oldCard);
                responseResult.setData(oldCard);
                responseResult.setResult(true);
                DAO.closeOpenedSession();
            } else {
                responseResult.setMessage("Card with given id does not exist.");
                resp.setStatus(400);
            }
        } catch (NumberFormatException e) {
            responseResult.setMessage("Incorrect id format.");
            resp.setStatus(400);
        }
        this.objectMapper.writeValue(resp.getWriter(), responseResult);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        ResponseResult<Card> responseResult = new ResponseResult<>();
        try {
            long id = Long.parseLong(req.getParameter("id"));
            Card toDelete = (Card) DAO.getObjectById(id, Card.class);
            DAO.closeOpenedSession();
            if (toDelete != null) {
                DAO.deleteObjectById(id, Card.class);
                responseResult.setData(toDelete);
                responseResult.setResult(true);
            } else {
                responseResult.setMessage("The object cannot be deleted.");
                resp.setStatus(400);
            }
            DAO.closeOpenedSession();
        } catch (NumberFormatException exception) {
            responseResult.setMessage("Incorrect id format");
            resp.setStatus(400);
        }
        this.objectMapper.writeValue(resp.getWriter(), responseResult);
        DAO.closeOpenedSession();
    }
}
