<%@ page session="false"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.forums.forums.model.mo.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    int i = 0;
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    User loggedUser = (User) request.getAttribute("loggedUser");
    Topic topic = (Topic) request.getAttribute("topic");
    List<String> profilePicPaths = (List<String>) request.getAttribute("profilePicPaths");
    Long currentPageIndex = (Long) request.getAttribute("currentPageIndex");
    Long pageCount = (Long) request.getAttribute("pageCount");
    Long topicsCurrentPageIndex = (Long) request.getAttribute("topicsCurrentPageIndex");
    Boolean topicsSearchResultFlag = (Boolean) request.getAttribute("topicsSearchResultFlag");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    String menuActiveLink = "Topics";
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.jsp"%>
</head>
<style>

    .postsNotFound {
        font-size: 20px;
    }

</style>
<script>
    function navigateTo(nextPageIndex) {
        let currentPageInput = document.getElementById('currentPageIndex');
        currentPageInput.value = nextPageIndex.toString();
        document.changePageForm.submit();
    }

    function insertPost() {
        document.insertForm.submit();
    }

    function modifyTopic() {
        document.modifyTopicForm.submit();
    }

    function modifyPost(postID) {
        let f = document.modifyForm;
        f.postID.value = postID;
        f.submit();
    }

    function goBack(){
        document.backForm.submit();
    }

</script>
<body>
<%@include file="/include/header.jsp"%>
<main>
    <section id="pageTitle">
        <h1>
            <%=topic.getTitle()%>
        </h1>
    </section>

    <section class="buttonContainer">

        <input type="button" id="insertPostButton" name="insertPostButton" class="button green" value="Nuovo Post" onclick="javascript:insertPost()" />

        <% if (loggedUser.getUserID() == topic.getAuthor().getUserID()) { %>
            <input type="button" id="modifyTopicButton" name="modifyTopicButton" class="button blue" value="Modifica Topic" onclick="javascript:modifyTopic()" />
        <% } %>
        <input type="button" name="backButton" id="backButton" class="button red" value="Indietro" onclick="javascript:goBack()"/>

    </section>

    <section id="posts" class="clearfix">
        <% if (!topic.getPosts().isEmpty()) {%>
        <% for (i = 0; i < topic.getPosts().size(); i++) {%>
        <article class="post">
            <div class="authorProfilePic">
                <img src="<%= (profilePicPaths.get(i)) %>" alt="Foto profilo di @<%=topic.getPosts().get(i).getAuthor().getUsername()%>"/>
            </div>

            <div class="postDetails">
                <span class="author">
                    <%= (topic.getPosts().get(i).getAuthor().getUserID().equals(topic.getAuthor().getUserID()) && topic.getAnonymous()) ? "Utente Anonimo" :
                            "@" + topic.getPosts().get(i).getAuthor().getUsername() +
                                    (topic.getPosts().get(i).getAuthor().getUserID().equals(topic.getAuthor().getUserID()) ? " (Autore)" : "")
                    %>
                </span>
                &middot;
                <span class="creationDate"><%=sdf.format(topic.getPosts().get(i).getCreationTimestamp())%></span>
                <span class="content"><%= (topic.getPosts().get(i).getContent())%></span>
            </div>
        </article>
        <%}%>

        <form name="changePageForm" method="post" action="Dispatcher">

            <section class="navigationContainer" id="navigationContainer">
                <img src="images/firstPage.png" alt="<<-" class="navigationButton adjusted" style="<%= currentPageIndex > 1 ? "" : "visibility: hidden;" %>" onclick="navigateTo(1)" />
                <img src="images/previousPage.png" alt="<-" class="navigationButton" style="<%= currentPageIndex > 1 ? "" : "visibility: hidden;" %>" onclick="navigateTo(<%= currentPageIndex - 1L %>)" />
                <div class="pageNumber" id="pageNumber">
                    <%= "Pagina " + currentPageIndex + " di " + pageCount %>
                </div>
                <img src="images/nextPage.png" alt="->" class="navigationButton" style="<%= currentPageIndex < pageCount ? "" : "visibility: hidden;" %>" onclick="navigateTo(<%= currentPageIndex + 1L %>)" />
                <img src="images/lastPage.png" alt="->>" class="navigationButton adjusted" style="<%= currentPageIndex < pageCount ? "" : "visibility: hidden;" %>" onclick="navigateTo(<%= pageCount %>)" />
            </section>

            <input type="hidden" name="currentPageIndex" id="currentPageIndex"  value="<%= currentPageIndex.toString() %>"/>
            <input type="hidden" name="controllerAction" value="TopicManagement.changePageView" />
        </form>

        <%} else {%>
        <p class="postsNotFound" id="postsNotFound">
            Ancora nessun post.
        </p>
        <%}%>
    </section>

    <form name="backForm" method="post" action="Dispatcher">
        <input type="hidden" name="currentPageIndex" value="<%=topicsCurrentPageIndex%>"/>
        <input type="hidden" name="searchResultFlag" value="<%=topicsSearchResultFlag%>"/>
        <input type="hidden" name="controllerAction" value="TopicManagement.changePageView"/>
    </form>

    <form name="insertForm" method="post" action="Dispatcher">
        <input type="hidden" name="controllerAction" value="PostManagement.insertView"/>
    </form>

    <form name="modifyForm" method="post" action="Dispatcher">
        <input type="hidden" name="postID"/>
        <input type="hidden" name="controllerAction" value="PostManagement.modifyView"/>
    </form>

    <form name="modifyTopicForm" method="post" action="Dispatcher">
        <input type="hidden" name="topicID" value="<%=topic.getTopicID()%>"/>
        <input type="hidden" name="controllerAction" value="TopicManagement.modifyView"/>
    </form>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>
