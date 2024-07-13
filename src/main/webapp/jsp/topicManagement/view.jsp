<%@ page session="false"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.forums.forums.model.mo.*" %>
<%@ page import="java.util.List" %>

<%
    int i = 0;
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    User loggedUser = (User) request.getAttribute("loggedUser");
    List<Topic> topics = (List<Topic>) request.getAttribute("topics");
    Long currentPageIndex = (Long) request.getAttribute("currentPageIndex");
    Long pageCount = (Long) request.getAttribute("pageCount");
    Boolean searchResultFlag = (Boolean) request.getAttribute("searchResultFlag");
    String menuActiveLink = "Topics";

    //Parametri della ricerca
    String sortNewestFirst = (String) request.getAttribute("sortNewestFirst");
    String title = (String) request.getAttribute("title");
    String authorName = (String) request.getAttribute("authorName");
    String categoryName = (String) request.getAttribute("categoryName");
    String moreRecentThan = (String) request.getAttribute("moreRecentThan");
    String olderThan = (String) request.getAttribute("olderThan");
    String isAnonymous = (String) request.getAttribute("isAnonymous");
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.jsp"%>
</head>
<style>
    /* Allinea gli elementi del form in colonne */
    .field {
        display: flex;
        flex-direction: column;
        margin-bottom: 10px;
    }

    /* Aggiusta lo stile delle etichette dei campi */
    .field label {
        font-weight: bold;
    }

    /* Stile degli input */
    .field input[type="text"],
    .field input[type="password"],
    .field input[type="date"],
    .field input[type="email"] {
        padding: 5px;
        border: 1px solid #ccc;
        border-radius: 4px;
        font-size: 14px;
    }

    /* Stile dei pulsanti */
    .field input[type="submit"],
    .field input[type="button"] {
        padding: 10px 20px;
        background-color: #007bff;
        color: #fff;
        border: none;
        border-radius: 4px;
        font-size: 14px;
        cursor: pointer;
        margin-top: 10px;
    }

    /* Stile del pulsante "Indietro" */
    .field input[name="backButton"] {
        background-color: #dc3545;
    }

    /* Allinea il pulsante "Indietro" a destra */
    .field label:last-child {
        display: flex;
        justify-content: flex-end;
        align-items: center;
    }

    /* Aggiusta il margine superiore del titolo della sezione */
    #pageTitle h1 {
        margin-top: 0;
        font-size: 24px;
    }

    .navigationContainer {
        display: flex;
        justify-content: center;
        align-items: center;
        margin-top: 20px;
    }

    .navigationButton {
        cursor: pointer;
        width: 50px;
        height: 50px;
        margin: 0 10px;
    }

    .pageNumber {
        font-size: 20px;
        min-width: 50px;
        text-align: center;
        background-size: cover;
        display: flex;
        justify-content: center;
        align-items: center;
        width: 150px;
        height: 50px;
        color: black;
        background-image: url('images/pageBox.png');
    }

</style>
<script>
    function navigateTo(nextPageIndex) {
        let currentPageInput = document.getElementById('currentPageIndex');
        currentPageInput.value = nextPageIndex.toString();
        document.changePageForm.submit();
    }

    function insertTopic() {
        document.insertForm.submit();
    }

    function viewTopic(topicID) {
        f = document.viewForm;
        f.topicID.value = topicID;
        f.submit();
    }

    function goBack(){
        document.backForm.submit();
    }

    function mainOnLoadHandler(){
        document.querySelector("#newTopicButton").addEventListener("click",insertTopic);
    }

</script>
<body>
<%@include file="/include/header.jsp"%>
<main>
    <section id="pageTitle">
        <h1>
            <%=(searchResultFlag.equals(true)) ? "Risultati della ricerca" : "Topics più recenti"%>
        </h1>
    </section>

    <section id="newTopicButtonSection">
        <input type="button" id="newTopicButton" name="newTopicButton"
               class="button" value="Nuovo Topic"/>
    </section>

    <section id="topics" class="clearfix">
        <%for (i = 0; i < topics.size(); i++) {%>
        <article class="title">
            <h1>
                <a href="javascript:viewTopic(<%=topics.get(i).getTopicID()%>)">
                    <%=topics.get(i).getTitle()%>
                </a>
            </h1>
            <span class="author"><%= topics.get(i).getAnonymous() ? "Utente Anonimo" : "@" + topics.get(i).getAuthor().getUsername()%></span>
            <br/>
            <span class="category"><%= topics.get(i).getCategory().getName()%></span>

        </article>
        <%}%>
    </section>

    <form name="changePageForm" method="post" action="Dispatcher">

        <section class="navigationContainer" id="navigationContainer">
            <% if (currentPageIndex > 1) {%>
                <img src="images/previousPage.png" alt="<-" class="navigationButton" onclick="navigateTo(<%= currentPageIndex - 1L%>)">
            <%}%>
            <div class="pageNumber" id="pageNumber">
                <%= "Pagina " + currentPageIndex + " di " + pageCount%>
            </div>
            <% if (currentPageIndex < pageCount) {%>
                <img src="images/nextPage.png" alt="->" class="navigationButton" onclick="navigateTo(<%= currentPageIndex + 1L%>)">
            <%}%>
        </section>

        <input type="hidden" name="currentPageIndex" id="currentPageIndex"  value="<%= currentPageIndex.toString() %>"/>
        <input type="hidden" name="sortNewestFirst" id="sortNewestFirst" value="<%= sortNewestFirst %>"/>
        <input type="hidden" name="title" id="title" value="<%= title %>"/>
        <input type="hidden" name="authorName" id="authorName" value="<%= authorName %>"/>
        <input type="hidden" name="categoryName" id="categoryName" value="<%= categoryName %>"/>
        <input type="hidden" name="moreRecentThan" id="moreRecentThan" value="<%= moreRecentThan%>"/>
        <input type="hidden" name="olderThan" id="olderThan" value="<%= olderThan%>"/>
        <input type="hidden" name="isAnonymous" id="isAnonymous" value="<%= isAnonymous%>"/>

        <input type="hidden" name="controllerAction" value="TopicManagement.view">
    </form>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>
