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
    NavigationState navigationState = (NavigationState) request.getAttribute("navigationState");
    Long postsPageCount = (Long) request.getAttribute("postsPageCount");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    String menuActiveLink = "Topics";
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.inc"%>
</head>
<style>

    .post {
        display: flex;
        align-items: flex-start;
        border: 2px solid #ccc;
        padding: 25px;
        border-radius: 8px;
    }

    .post + .post {
        border-top: none;
    }

    .authorProfile {
        width: 250px;
        text-align: center;
    }

    .authorProfilePic {
        width: 250px;
        height: 250px;
        margin: 0 auto;
        border: 2px solid #ccc;
        border-radius: 8px;
    }

    .authorProfilePic img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 8px;
    }

    .authorDetails {
        margin-top: 10px;
        font-size: 18px;
        color: #666;
    }

    .username, .role, .creationDate, .edited {
        display: block;
        margin-bottom: 5px;
        font-size: 16px;
    }

    .insertMediasButton img {
        width: 60px;
        height: auto;
        transition: filter 0.3s ease;
        padding: 0;
    }

    .insertMediasButton img:hover {
        filter: brightness(85%);
    }

    .postContent {
        flex-grow: 1;
        padding-left: 25px;
    }

    .content {
        white-space: pre-wrap;
        font-size: 20px;
    }

    .viewMediaButton {
        padding-top: 24px;
    }

    .username {
        font-size: 20px;
        font-weight: bold;
    }

    .role, .creationDate, .edited {
        font-size: 18px;
        color: #666;
    }

    .edited {
        font-style: italic;
    }

    .postsNotFound {
        font-size: 20px;
    }

    .pageNumber {
        background-image: url('images/pageBox.png');
    }

</style>
<script>
    function navigateTo(nextPageIndex) {
        let currentPageInput = document.getElementById('postsCurrentPageIndex');
        currentPageInput.value = nextPageIndex.toString();
        document.changePageForm.submit();
    }

    function insertPost() {
        document.insertForm.submit();
    }

    function modifyTopic() {
        document.modifyTopicForm.submit();
    }

    function deleteTopic() {
        if(confirm("Attenzione! Questa azione e' irreversibile. Vuoi procedere?")){
            document.deleteTopicForm.topicID.value = <%=topic.getTopicID()%>
            document.deleteTopicForm.submit();
        }
    }

    function modifyPost(postID) {
        let f = document.modifyForm;
        f.postID.value = postID;
        f.submit();
    }

    function viewUser(userID) {
        let f = document.viewUserForm;
        f.userID.value = userID;
        f.submit();
    }

    function insertMedias(postID) {
        let f = document.insertMediasForm;
        f.postID.value = postID;
        f.submit();
    }

    function viewMedias(postID) {
        let f = document.viewMediasForm;
        f.postID.value = postID;
        f.submit();
    }

    function goBack(){
        document.backForm.submit();
    }

</script>
<body>
<%@include file="/include/header.inc"%>
<main>
    <section id="pageTitle">
        <h1>
            <%=topic.getTitle()%>
        </h1>
    </section>

    <section class="buttonContainer">

        <input type="button" id="insertPostButton" name="insertPostButton" class="button green" value="Nuovo Post" onclick="javascript:insertPost()" />

        <% if ((loggedUser.getUserID() == topic.getAuthor().getUserID()) || (loggedUser.getRole().equals("Admin"))) { %>
            <input type="button" id="modifyTopicButton" name="modifyTopicButton" class="button blue" value="Modifica Topic" onclick="javascript:modifyTopic()" />
            <input type="button" id="deleteTopicButton" name="deleteTopicButton" class="button red" value="Elimina Topic" onclick="javascript:deleteTopic()" />
        <% } %>
        <input type="button" name="backButton" id="backButton" class="button red" value="Indietro" onclick="javascript:goBack()"/>

    </section>

    <section id="posts" class="clearfix">
        <% if (topic != null && topic.getPosts() != null && !topic.getPosts().isEmpty()) {%>
            <% for (Post post : topic.getPosts()) {%>
            <article class="post">
                <div class="authorProfile">
                    <div class="authorProfilePic">
                        <img src="<%= (topic.getAnonymous() && post.getAuthor().getUserID().equals(topic.getAuthor().getUserID())) ?
                            "/images/categoryImages/anonymous.png" :
                                post.getAuthor().getProfilePicPath() %>?cache=<%=System.currentTimeMillis()%>"
                                    alt="Foto profilo di @<%=post.getAuthor().getUsername()%>"/>
                    </div>
                    <div class="authorDetails">
                        <span class="username<%=(!post.getAuthor().getDeleted() && (!topic.getAnonymous() || post.getAuthor().getUserID() != topic.getAuthor().getUserID())) ? " clickable" : ""%>"
                                <%=(!post.getAuthor().getDeleted() && (!topic.getAnonymous() || post.getAuthor().getUserID() != topic.getAuthor().getUserID())) ? "onclick = \"javascript:viewUser(" + post.getAuthor().getUserID() + ")\"" : ""%>
                                    ><%= (post.getAuthor().getUserID().equals(topic.getAuthor().getUserID()) && topic.getAnonymous()) ? "Utente Anonimo" :
                                            "@" + post.getAuthor().getUsername() +
                                                            ((post.getAuthor().getDeleted()) ? " (Eliminato)" : "")%>
                                                                <%=((loggedUser.getUserID() == post.getAuthor().getUserID()) ? " (Tu)" : "")%>
                                                                    <%=(post.getAuthor().getUserID().equals(topic.getAuthor().getUserID()) ? " (Autore)" : "")%>
                        </span>
                        <% if (!topic.getAnonymous() || !post.getAuthor().getUserID().equals(topic.getAuthor().getUserID())) {%>
                            <span class="role"><%=post.getAuthor().getRole()%></span>
                        <% } %>
                        <span class="creationDate"><%=sdf.format(post.getCreationTimestamp())%></span>
                        <% if (post.getEdited()) {%>
                            <span class="edited">(modificato)</span>
                        <% } %>
                    </div>
                    <% if (post.getAuthor().getUserID() == loggedUser.getUserID()) {%>
                    <div class="insertMediasButton">
                        <img src="images/addMedia.png" alt="Aggiungi Media" class="button adjusted" onclick="insertMedias(<%=post.getPostID()%>)" />
                    </div>
                    <% } %>
                </div>
                <div class="postContent">
                    <% if ((post.getAuthor().getUserID() == loggedUser.getUserID()) || (loggedUser.getRole().equals("Admin"))) {%>
                        <p class="content clickable" onclick="javascript:modifyPost(<%=post.getPostID()%>)"><%= (post.getContent())%></p>
                    <% } else { %>
                        <p class="content"><%= (post.getContent())%></p>
                    <% }%>

                    <% if (post.getMedias() != null) { %>
                        <div class="viewMediaButton">
                            <input type="button" name="viewMediaButton" class="button green" value="Visualizza Allegati" onclick="javascript:viewMedias(<%=post.getPostID()%>)"/>
                        </div>
                    <% } %>
                </div>
            </article>
            <%}%>

            <form name="changePageForm" method="post" action="Dispatcher">

                <section class="navigationContainer" id="navigationContainer">
                    <img src="images/firstPage.png" alt="<<-" class="navigationButton adjusted" style="<%=navigationState.getPostsCurrentPageIndex() > 1 ? "" : "visibility: hidden;" %>" onclick="navigateTo(1)" />
                    <img src="images/previousPage.png" alt="<-" class="navigationButton" style="<%=navigationState.getPostsCurrentPageIndex() > 1 ? "" : "visibility: hidden;" %>" onclick="navigateTo(<%= navigationState.getPostsCurrentPageIndex() - 1L %>)" />
                    <div class="pageNumber" id="pageNumber">
                        <%= "Pagina " + navigationState.getPostsCurrentPageIndex() + " di " + postsPageCount %>
                    </div>
                    <img src="images/nextPage.png" alt="->" class="navigationButton" style="<%=navigationState.getPostsCurrentPageIndex() < postsPageCount ? "" : "visibility: hidden;" %>" onclick="navigateTo(<%= navigationState.getPostsCurrentPageIndex() + 1L %>)" />
                    <img src="images/lastPage.png" alt="->>" class="navigationButton adjusted" style="<%=navigationState.getPostsCurrentPageIndex() < postsPageCount ? "" : "visibility: hidden;" %>" onclick="navigateTo(<%= postsPageCount %>)" />
                </section>

                <input type="hidden" name="postsCurrentPageIndex" id="postsCurrentPageIndex"  value="<%= navigationState.getPostsCurrentPageIndex() %>"/>
                <input type="hidden" name="controllerAction" value="PostManagement.view" />
            </form>

        <% } else if (topic != null){%>
            <p class="postsNotFound" id="postsNotFound">
                Ancora nessun post.
            </p>
        <% } else { %>
            <p class="topicNotFound" id="topicNotFound">
                Topic non trovato.
            </p>
        <% } %>
    </section>

    <form name="backForm" method="post" action="Dispatcher">
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
        <input type="hidden" name="controllerAction" value="TopicManagement.modifyView"/>
    </form>

    <form name="deleteTopicForm" method="post" action="Dispatcher">
        <input type="hidden" name="topicID"/>
        <input type="hidden" name="controllerAction" value="TopicManagement.delete"/>
    </form>

    <form name="viewUserForm" method="post" action="Dispatcher">
        <input type="hidden" name="userID"/>
        <input type="hidden" name="controllerAction" value="UserManagement.profileView"/>
    </form>

    <form name="insertMediasForm" method="post" action="Dispatcher">
        <input type="hidden" name="postID"/>
        <input type="hidden" name="controllerAction" value="MediaManagement.insertView"/>
    </form>

    <form name="viewMediasForm" method="post" action="Dispatcher">
        <input type="hidden" name="postID"/>
        <input type="hidden" name="controllerAction" value="MediaManagement.view"/>
    </form>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>
