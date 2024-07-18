package com.forums.forums.model.dao.CookieImpl;

import com.forums.forums.model.dao.*;
import com.forums.forums.model.mo.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.sql.*;
import java.util.List;

public class NavigationStateDAOCookieImpl implements NavigationStateDAO {
    HttpServletRequest request;
    HttpServletResponse response;

    public NavigationStateDAOCookieImpl(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public NavigationState create(
            Long topicID,
            Long topicsCurrentPageIndex,
            Boolean topicsSearchResultFlag,
            Long postsCurrentPageIndex
    ) {
        NavigationState navigationState = new NavigationState();
        navigationState.setTopicID(topicID);
        navigationState.setTopicsCurrentPageIndex(topicsCurrentPageIndex);
        navigationState.setTopicsSearchResultFlag(topicsSearchResultFlag);
        navigationState.setPostsCurrentPageIndex(postsCurrentPageIndex);

        // Assumi che `encode` converta l'oggetto in una stringa adatta per il cookie
        Cookie cookie = new Cookie("navigationState", encode(navigationState));
        cookie.setPath("/");
        cookie.setMaxAge(3600); // Ad esempio, il cookie scade dopo un'ora
        response.addCookie(cookie);

        return navigationState;
    }

    @Override
    public void update(NavigationState navigationState) {
        Cookie cookie = new Cookie("navigationState", encode(navigationState));
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @Override
    public void delete(NavigationState navigationState) {
        Cookie cookie = new Cookie("navigationState", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @Override
    public NavigationState findNavigationState() {
        Cookie[] cookies = request.getCookies();
        NavigationState navigationState = null;

        if (cookies != null) {
            for (int i = 0; i < cookies.length && navigationState == null; i++) {
                if (cookies[i].getName().equals("navigationState")) {
                    navigationState = decode(cookies[i].getValue());
                }
            }
        }

        return navigationState;
    }

    @Override
    public NavigationState findOrCreateNavigationState() {
        Cookie[] cookies = request.getCookies();
        NavigationState navigationState = null;

        if (cookies != null) {
            for (int i = 0; i < cookies.length && navigationState == null; i++) {
                if (cookies[i].getName().equals("navigationState")) {
                    navigationState = decode(cookies[i].getValue());
                }
            }
        }

        if (navigationState == null) navigationState = create(null,1L,false,null);

        return navigationState;
    }

    private String encode(NavigationState navigationState) {
        String encodedNavigationState;
        encodedNavigationState =
                (navigationState.getTopicID() != null ? navigationState.getTopicID().toString() : "null") + "#" +
                        (navigationState.getTopicsCurrentPageIndex() != null ? navigationState.getTopicsCurrentPageIndex().toString() : "null") + "#" +
                        (navigationState.getTopicsSearchResultFlag() != null ? navigationState.getTopicsSearchResultFlag().toString() : "null") + "#" +
                        (navigationState.getPostsCurrentPageIndex() != null ? navigationState.getPostsCurrentPageIndex().toString() : "null");

        return encodedNavigationState;
    }

    private NavigationState decode(String encodedNavigationState) {
        NavigationState navigationState = new NavigationState();

        String[] values = encodedNavigationState.split("#");

        navigationState.setTopicID(!"null".equals(values[0]) ? Long.parseLong(values[0]) : null);
        navigationState.setTopicsCurrentPageIndex(!"null".equals(values[1]) ? Long.parseLong(values[1]) : null);
        navigationState.setTopicsSearchResultFlag(!"null".equals(values[2]) ? Boolean.parseBoolean(values[2]) : null);
        navigationState.setPostsCurrentPageIndex(!"null".equals(values[3]) ? Long.parseLong(values[3]) : null);

        return navigationState;
    }

}
