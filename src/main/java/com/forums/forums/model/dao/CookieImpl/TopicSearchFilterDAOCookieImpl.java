package com.forums.forums.model.dao.CookieImpl;

import com.forums.forums.model.mo.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.forums.forums.model.dao.TopicSearchFilterDAO;
import com.forums.forums.model.mo.TopicSearchFilter;

import java.sql.*;
import java.util.List;


public class TopicSearchFilterDAOCookieImpl implements TopicSearchFilterDAO {

    HttpServletRequest request;
    HttpServletResponse response;

    public TopicSearchFilterDAOCookieImpl(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public TopicSearchFilter create(
            String title,
            String authorName,
            String categoryName,
            Timestamp moreRecentThan,
            Timestamp olderThan,
            Boolean isAnonymous,
            Boolean sortNewestFirst
    ) {

        TopicSearchFilter topicSearchFilter = new TopicSearchFilter();
        topicSearchFilter.setTitle(title);
        topicSearchFilter.setAuthorName(authorName);
        topicSearchFilter.setCategoryName(categoryName);
        topicSearchFilter.setMoreRecentThan(moreRecentThan);
        topicSearchFilter.setOlderThan(olderThan);
        topicSearchFilter.setAnonymous(isAnonymous);
        topicSearchFilter.setSortNewestFirst(sortNewestFirst);


        Cookie cookie;
        cookie = new Cookie("topicSearchFilter", encode(topicSearchFilter));
        cookie.setPath("/");
        response.addCookie(cookie);

        return topicSearchFilter;

    }

    @Override
    public void update(TopicSearchFilter topicSearchFilter) {

        Cookie cookie;
        cookie = new Cookie("topicSearchFilter", encode(topicSearchFilter));
        cookie.setPath("/");
        response.addCookie(cookie);

    }

    @Override
    public void delete(TopicSearchFilter topicSearchFilter) {

        Cookie cookie;
        cookie = new Cookie("topicSearchFilter", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

    }

    @Override
    public TopicSearchFilter findTopicSearchFilter() {

        Cookie[] cookies = request.getCookies();
        TopicSearchFilter topicSearchFilter = null;

        if (cookies != null) {
            for (int i = 0; i < cookies.length && topicSearchFilter == null; i++) {
                if (cookies[i].getName().equals("topicSearchFilter")) {
                    topicSearchFilter = decode(cookies[i].getValue());
                }
            }
        }

        return topicSearchFilter;

    }

    private String encode(TopicSearchFilter topicSearchFilter) {
        String encodedTopicSearchFilter;
        encodedTopicSearchFilter =
                (topicSearchFilter.getTitle() != null ? topicSearchFilter.getTitle().replace(" ", "_") : "null") + "#" +
                        (topicSearchFilter.getAuthorName() != null ? topicSearchFilter.getAuthorName().replace(" ", "_") : "null") + "#" +
                        (topicSearchFilter.getCategoryName() != null ? topicSearchFilter.getCategoryName().replace(" ", "_") : "null") + "#" +
                        (topicSearchFilter.getMoreRecentThan() != null ? topicSearchFilter.getMoreRecentThan().toString().replace(" ", "_") : "null") + "#" +
                        (topicSearchFilter.getOlderThan() != null ? topicSearchFilter.getOlderThan().toString().replace(" ", "_") : "null") + "#" +
                        (topicSearchFilter.getAnonymous() != null ? topicSearchFilter.getAnonymous().toString().replace(" ", "_") : "null") + "#" +
                        (topicSearchFilter.getSortNewestFirst() != null ? topicSearchFilter.getSortNewestFirst().toString().replace(" ", "_") : "null");

        return encodedTopicSearchFilter;
    }

    private TopicSearchFilter decode(String encodedTopicSearchFilter) {
        TopicSearchFilter topicSearchFilter = new TopicSearchFilter();

        String[] values = encodedTopicSearchFilter.split("#");

        topicSearchFilter.setTitle(!"null".equals(values[0]) ? values[0].replace('_', ' ') : null);
        topicSearchFilter.setAuthorName(!"null".equals(values[1]) ? values[1].replace('_', ' ') : null);
        topicSearchFilter.setCategoryName(!"null".equals(values[2]) ? values[2].replace('_', ' ') : null);

        if (!"null".equals(values[3])) {
            try {
                String dateTimeReplaced = values[3].replace('_', ' ');
                topicSearchFilter.setMoreRecentThan(Timestamp.valueOf(dateTimeReplaced));
            } catch (IllegalArgumentException e) {
                topicSearchFilter.setMoreRecentThan(null);
            }
        } else {
            topicSearchFilter.setMoreRecentThan(null);
        }

        if (!"null".equals(values[4])) {
            try {
                String dateTimeReplaced = values[4].replace('_', ' ');
                topicSearchFilter.setOlderThan(Timestamp.valueOf(dateTimeReplaced));
            } catch (IllegalArgumentException e) {
                topicSearchFilter.setOlderThan(null);
            }
        } else {
            topicSearchFilter.setOlderThan(null);
        }

        topicSearchFilter.setAnonymous(!"null".equals(values[5]) ? Boolean.parseBoolean(values[5]) : null);
        topicSearchFilter.setSortNewestFirst(!"null".equals(values[6]) ? Boolean.parseBoolean(values[6]) : null);

        return topicSearchFilter;
    }
}