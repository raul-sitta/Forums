<%@ page session="false"%>
<%@ page import="com.forums.forums.model.mo.User" %>
<%@ page import="com.forums.forums.services.filesystemservice.FileSystemService" %>
<%@ page import="com.forums.forums.model.mo.NavigationState" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    User loggedUser = (User) request.getAttribute("loggedUser");
    User user = (User) request.getAttribute("user");
    NavigationState navigationState = (NavigationState) request.getAttribute("navigationState");
    List<Long> userStats = (List<Long>) request.getAttribute("userStats");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    String menuActiveLink = "Topics";
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.jsp"%>
    <style>

    </style>
    <script>
        function goBack(){
            document.backForm.submit();
        }

        <% if (loggedUser.getRole().equals("Admin")) { %>

            function banUser(userID){
                if(confirm("Attenzione! Questa azione e' irreversibile. Vuoi procedere?")){
                    let f = document.banForm;
                    f.userID.value = userID;
                    f.submit();
                }
            }

        <% } %>

    </script>
</head>
<style>
    .userProfile {
        display: flex;
        background-color: #f4f4f4;
        padding: 20px;
        border: 2px solid #ccc;
        border-radius: 8px;
        box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        box-sizing: border-box;
        margin: 0 auto;
    }

    .profilePhoto {
        width: 250px;
        height: 250px;
        margin: 0 20px;
        border: 2px solid #ccc;
        border-radius: 8px;
    }

    .profilePhoto img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 8px;
    }

    .userInfo {
        flex-basis: 60%;
        display: flex;
        flex-direction: column;
        padding-left: 20px;
    }

    .userInfo span {
        font-size: 18px;
        margin-bottom: 10px;
    }

    .userInfo > .username {
        font-weight: bold;
        font-size: 24px;
        margin-bottom: 20px;
    }
</style>
<body>
<%@include file="/include/header.jsp"%>
<main>
    <section id="pageTitle">
        <h1>Profilo di @<%=user.getUsername()%></h1>
    </section>

    <section class="buttonContainer">

        <% if (loggedUser.getRole().equals("Admin")) { %>
        <input type="button" id="banButton" name="banButton"
               class="button red" value="Banna" onclick="goBack()"/>
        <% } %>

        <input type="button" id="backButton" name="backButton" class="button red" value="Indietro" onclick="goBack()"/>
    </section>

    <article class="userProfile">
        <div class="profilePhoto">
            <img src="<%= user.getProfilePicPath() %>" alt="Foto Profilo" />
        </div>
        <div class="userInfo">
            <span class="username">@<%= user.getUsername() %></span>
            <span>Ruolo: <%= user.getRole() %></span>
            <span>Iscritto in data: <%= sdf.format(user.getRegistrationTimestamp()) %></span>
            <span>Topic creati: <%= userStats.get(0) %></span>
            <span>Post creati: <%= userStats.get(1) %></span>
        </div>
    </article>

    <form name="backForm" method="post" action="Dispatcher">
        <% if (navigationState.getTopicID() == null && navigationState.getPostsCurrentPageIndex() == null) { %>
            <input type="hidden" name="controllerAction" value="TopicManagement.changePageView">
        <% } else { %>
            <input type="hidden" name="controllerAction" value="PostManagement.view">
        <% } %>
    </form>

    <form name="banForm" method="post" action="Dispatcher">
        <input type="hidden" name="userID"/>
        <input type="hidden" name="controllerAction" value="UserManagement.ban"/>
    </form>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>