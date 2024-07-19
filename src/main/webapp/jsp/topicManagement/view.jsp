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
    List<Topic> topics = (List<Topic>) request.getAttribute("topics");
    Long topicsPageCount = (Long) request.getAttribute("topicsPageCount");
    NavigationState navigationState = (NavigationState) request.getAttribute("navigationState");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    String menuActiveLink = "Topics";
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.jsp"%>
</head>
<style>

    .topic {
        display: flex;
        border: 2px solid #ccc;
        padding: 10px;
        margin-bottom: 0;
        border-radius: 8px;
    }

    .topic + .topic {
        border-top: none;
    }

    .categoryImage {
        flex: 0 0 100px;
        height: 100px;
        border: 2px solid #ccc;
        margin-right: 15px;
        border-radius: 8px;
    }

    .categoryImage img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 8px;
    }

    .topicContent {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
    }

    .topicHeader {
        font-size: 22px;
        margin: 0 0 5px 0;
        font-weight: bold;
        color: #666;
    }

    .topicHeader:hover {
        text-decoration: underline;
    }

    .topicDetails {
        font-size: 16px;
        color: #666;
        display: flex;
        flex-direction: column;
        justify-content: flex-end;
    }
    .topicDetails span {
        margin-bottom: 6px;
    }

    .topicDetails span:last-child {
        margin-bottom: 0;
        margin-top: 0;
    }

    .topicDetails .author,
    .topicDetails .creationDate {
        display: block;
    }

    .topicDetails .category,
    .topicDetails .author {
        font-weight: bold;
    }

    .topicsNotFound {
        font-size: 20px;
    }

    .pageNumber {
        background-image: url('images/pageBox.png');
    }

</style>
<script>
    function navigateTo(nextPageIndex) {
        let currentPageInput = document.getElementById('topicsCurrentPageIndex');
        currentPageInput.value = nextPageIndex.toString();
        document.changePageForm.submit();
    }

    function insertTopic() {
        document.insertForm.submit();
    }

    function searchTopic() {
        document.searchForm.submit();
    }

    function viewTopic(topicID) {
        let f = document.viewForm;
        f.topicID.value = topicID;
        f.submit();
    }

    function viewUser(userID) {
        let f = document.viewUserForm;
        f.userID.value = userID;
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
            <%=(navigationState.getTopicsSearchResultFlag().equals(true)) ? "Risultati della ricerca" : "Topics più recenti"%>
        </h1>
    </section>

    <section class="buttonContainer">
        <% if (navigationState.getTopicsSearchResultFlag().equals(false)) {%>

            <input type="button" id="insertTopicButton" name="insertTopicButton" class="button green" value="Nuovo Topic" onclick="javascript:insertTopic()" />

            <input type="button" id="searchTopicButton" name="searchTopicButton" class="button blue" value="Ricerca Topic" onclick="javascript:searchTopic()" />

        <%} else {%>

            <input type="button" name="backButton" id="backButton" class="button red" value="Indietro" onclick="javascript:goBack()"/>

        <%}%>
    </section>

    <section id="topics" class="clearfix">
        <% if (!topics.isEmpty()) {%>
            <% for (i = 0; i < topics.size(); i++) {%>
            <article class="topic">
                <div class="categoryImage">
                    <img src="<%= (!topics.get(i).getAnonymous()) ?
                    "images/categoryImages/" + topics.get(i).getCategory().getCategoryID() + ".png" :
                    "images/categoryImages/anonymous.png"%>" alt="Immagine Categoria"/>
                </div>
                <div class="topicContent">
                    <a class="topicHeader" href="javascript:viewTopic(<%=topics.get(i).getTopicID()%>)">
                        <%=topics.get(i).getTitle()%>
                    </a>
                    <div class="topicDetails">
                        <span class="author<%=(!topics.get(i).getAuthor().getDeleted() && !topics.get(i).getAnonymous()) ? " clickable" : ""%>"
                                <%=(!topics.get(i).getAuthor().getDeleted() && !topics.get(i).getAnonymous()) ? "onclick = \"javascript:viewUser(" + topics.get(i).getAuthor().getUserID() + ")\"" : ""%>
                        ><%=(!topics.get(i).getAnonymous()) ? "@" + topics.get(i).getAuthor().getUsername() + ((topics.get(i).getAuthor().getDeleted()) ? " (Eliminato)" : "" ) : "Utente Anonimo"%></span>
                        <span class="creationDate"><%=sdf.format(topics.get(i).getCreationTimestamp())%></span>
                        <span class="category">Categoria: <%=topics.get(i).getCategory().getName()%></span>
                    </div>
                </div>
            </article>
            <%}%>

            <form name="changePageForm" method="post" action="Dispatcher">

                <section class="navigationContainer" id="navigationContainer">
                    <img src="images/firstPage.png" alt="<<-" class="navigationButton adjusted" style="<%=navigationState.getTopicsCurrentPageIndex() > 1 ? "" : "visibility: hidden;" %>" onclick="navigateTo(1)" />
                    <img src="images/previousPage.png" alt="<-" class="navigationButton" style="<%=navigationState.getTopicsCurrentPageIndex() > 1 ? "" : "visibility: hidden;" %>" onclick="navigateTo(<%= navigationState.getTopicsCurrentPageIndex() - 1L %>)" />
                    <div class="pageNumber" id="pageNumber">
                        <%= "Pagina " + navigationState.getTopicsCurrentPageIndex() + " di " + topicsPageCount %>
                    </div>
                    <img src="images/nextPage.png" alt="->" class="navigationButton" style="<%=navigationState.getTopicsCurrentPageIndex() < topicsPageCount ? "" : "visibility: hidden;" %>" onclick="navigateTo(<%= navigationState.getTopicsCurrentPageIndex() + 1L %>)" />
                    <img src="images/lastPage.png" alt="->>" class="navigationButton adjusted" style="<%=navigationState.getTopicsCurrentPageIndex() < topicsPageCount ? "" : "visibility: hidden;" %>" onclick="navigateTo(<%= topicsPageCount %>)" />
                </section>

                <input type="hidden" name="topicsCurrentPageIndex" id="topicsCurrentPageIndex"  value="<%= navigationState.getTopicsCurrentPageIndex() %>"/>
                <input type="hidden" name="controllerAction" value="TopicManagement.changePageView" />
            </form>

        <%} else {%>
            <p class="topicsNotFound" id="topicsNotFound">
                Nessun topic trovato con i parametri forniti.
            </p>
        <%}%>
    </section>

    <form name="insertForm" method="post" action="Dispatcher">
        <input type="hidden" name="controllerAction" value="TopicManagement.insertView"/>
    </form>

    <form name="searchForm" method="post" action="Dispatcher">
        <input type="hidden" name="controllerAction" value="TopicManagement.searchView"/>
    </form>

    <form name="backForm" method="post" action="Dispatcher">
        <input type="hidden" name="controllerAction" value="TopicManagement.searchView">
    </form>

    <form name="viewForm" method="post" action="Dispatcher">
        <input type="hidden" name="topicID"/>
        <input type="hidden" name="postsCurrentPageIndex" value="1"/>
        <input type="hidden" name="controllerAction" value="PostManagement.view"/>
    </form>

    <form name="viewUserForm" method="post" action="Dispatcher">
        <input type="hidden" name="userID"/>
        <input type="hidden" name="controllerAction" value="UserManagement.profileView"/>
    </form>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>
