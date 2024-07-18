package com.forums.forums.model.dao;

import com.forums.forums.model.mo.*;

import java.sql.*;
import java.util.List;

public interface NavigationStateDAO {

    public NavigationState create(
            Long topicID,
            Long topicsCurrentPageIndex,
            Boolean topicsSearchResultFlag,
            Long postsCurrentPageIndex
    );
    public void update(NavigationState navigationState);
    public void delete(NavigationState navigationState);
    public NavigationState findNavigationState();
    public NavigationState findOrCreateNavigationState();

}
