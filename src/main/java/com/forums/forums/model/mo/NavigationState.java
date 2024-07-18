package com.forums.forums.model.mo;

public class NavigationState {

    private Long topicID;

    private Long topicsCurrentPageIndex;

    private Boolean topicsSearchResultFlag;

    private Long postsCurrentPageIndex;

    public Long getTopicID() {
        return topicID;
    }

    public void setTopicID(Long topicID) {
        this.topicID = topicID;
    }

    public Long getTopicsCurrentPageIndex() {
        return topicsCurrentPageIndex;
    }

    public void setTopicsCurrentPageIndex(Long topicsCurrentPageIndex) {
        this.topicsCurrentPageIndex = topicsCurrentPageIndex;
    }

    public Boolean getTopicsSearchResultFlag() {
        return topicsSearchResultFlag;
    }

    public void setTopicsSearchResultFlag(Boolean topicsSearchResultFlag) {
        this.topicsSearchResultFlag = topicsSearchResultFlag;
    }

    public Long getPostsCurrentPageIndex() {
        return postsCurrentPageIndex;
    }

    public void setPostsCurrentPageIndex(Long postsCurrentPageIndex) {
        this.postsCurrentPageIndex = postsCurrentPageIndex;
    }
}
