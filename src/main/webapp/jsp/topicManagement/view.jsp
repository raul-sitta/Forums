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
    Long currentPageIndex = (Long) request.getAttribute("currentPageIndex");
    Long pageCount = (Long) request.getAttribute("pageCount");
    Boolean searchResultFlag = (Boolean) request.getAttribute("searchResultFlag");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    String menuActiveLink = "Topics";
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.jsp"%>
</head>
<style>

    #buttonContainer {
        display: flex;  /* Imposta il layout flexibile */
        justify-content: start;  /* Allinea i pulsanti all'inizio del contenitore */
        gap: 10px;  /* Aggiunge uno spazio tra i pulsanti */
    }

    /* Stile dei pulsanti */
    .button {
        padding: 12px 24px;
        font-size: 16px;
        cursor: pointer;
        transition: background-color 0.3s ease;
        margin-bottom: 12px;
    }

    /* Stile specifico per il pulsante "Nuovo Topic" */
    #insertTopicButton {
        background-color: #28a745;
    }

    #insertTopicButton:hover {
        background-color: #1f9233;
    }

    /* Stile specifico per il pulsante "Rierca Topic" */
    #searchTopicButton {
        background-color: #007bff;
    }

    #searchTopicButton:hover {
        background-color: #0056b3;
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

    .topic {
        display: flex;
        border: 2px solid #ccc;
        padding: 10px;
        margin-bottom: 0;
    }

    .topic + .topic {
        border-top: none;
    }

    .categoryImage {
        flex: 0 0 100px; /* Fixed width for image */
        height: 100px; /* Fixed height for image */
        border: 2px solid #ccc;
        margin-right: 15px;
    }

    .categoryImage img {
        width: 100%;
        height: 100%;
        object-fit: cover;
    }

    .topicContent {
        flex: 1;
    }

    .topicHeader h1 {
        font-size: 18px;
        margin: 0 0 5px 0;
    }

    .topicDetails {
        font-size: 14px;
        color: #666;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
    }

    .topicDetails .author,
    .topicDetails .creationDate {
        display: block;
    }

    .topicDetails .category {
        font-weight: bold;
    }

    .topicsNotFound {
        font-size: 20px;
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
        document.querySelector("#insertTopicButton").addEventListener("click",insertTopic);
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

    <section id="buttonContainer">
        <% if (searchResultFlag.equals(false)) {%>

            <input type="button" id="insertTopicButton" name="insertTopicButton" class="button" value="Nuovo Topic"/>

            <input type="button" id="searchTopicButton" name="searchTopicButton" class="button" value="Ricerca Topic"/>

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
                    <span class="topicHeader">
                        <h1><%=topics.get(i).getTitle()%></h1>
                    </span>
                    <div class="topicDetails">
                        <span class="author"><%=(!topics.get(i).getAnonymous()) ? "@" + topics.get(i).getAuthor().getUsername() : "Utente Anonimo"%></span>
                        <span class="creationDate"><%=sdf.format(topics.get(i).getCreationTimestamp())%></span>
                        <span class="category">Categoria: <%=topics.get(i).getCategory().getName()%></span>
                    </div>
                </div>
            </article>
            <%}%>

            <form name="changePageForm" method="post" action="Dispatcher">

                <section class="navigationContainer" id="navigationContainer">
                    <% if (currentPageIndex > 1) {%>
                    <img src="images/previousPage.png" alt="<-" class="navigationButton" onclick="navigateTo(<%= currentPageIndex - 1L%>)" />
                    <%}%>
                    <div class="pageNumber" id="pageNumber">
                        <%= "Pagina " + currentPageIndex + " di " + pageCount%>
                    </div>
                    <% if (currentPageIndex < pageCount) {%>
                    <img src="images/nextPage.png" alt="->" class="navigationButton" onclick="navigateTo(<%= currentPageIndex + 1L%>)" />
                    <%}%>
                </section>

                <input type="hidden" name="currentPageIndex" id="currentPageIndex"  value="<%= currentPageIndex.toString() %>"/>
                <input type="hidden" name="controllerAction" value="TopicManagement.view" />
            </form>

        <%} else {%>
            <span class="topicsNotFound" id="topicsNotFound">
                Nessun topic trovato con i parametri forniti!
            </span>
        <%}%>
    </section>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>
