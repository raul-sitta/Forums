<%@ page session="false"%>
<%@ page import="com.forums.forums.model.mo.User" %>
<%@ page import="com.forums.forums.services.filesystemservice.FileSystemService" %>

<%
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    User loggedUser = (User) request.getAttribute("loggedUser");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    String menuActiveLink = (loggedUser!=null) ? "Account" : "Registrati";
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.inc"%>
    <style>

        #profileContainer {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
        }

        #profilePic img {
            width: 100px;
            height: 100px;
            border-radius: 50%;
            overflow: hidden;
            border: 2px solid #ccc;
            margin-right: 20px;
            object-fit: cover;
        }

        #profileInfo h2 {
            margin: 0;
            font-size: 22px;
        }

        #profileInfo p {
            margin: 5px 0;
            font-size: 16px;
        }

    </style>
    <script>
        function insertUser(){
            document.insertForm.submit();
        }
        function mainOnLoadHandler(){
            document.querySelector("#newUserButton").addEventListener("click",insertUser);
        }
        function modifyUser(){
            document.modifyForm.submit();
        }
        function deleteUser(){
            if(confirm("Attenzione! Questa azione e' irreversibile. Vuoi procedere?")){
                document.deleteForm.submit();
            }
        }
    </script>
</head>
<body>
<%@include file="/include/header.inc"%>
<main>
    <section id="pageTitle">
        <h1>Gestione Account</h1>
    </section>

    <%if (!loggedOn){%>
    <section class="buttonContainer">
        <input type="button" id="insertUserButton" name="insertUserButton"
               class="button green" value="Registrati" onclick="insertUser()"/>
    </section>
    <%}%>

    <%if (loggedOn){%>
    <section id="userProfilePreview">
        <div class="profileContainer" id="profileContainer">
            <div class="profilePic" id="profilePic">
                <img src="<%=loggedUser.getProfilePicPath()%>?cache=<%=System.currentTimeMillis()%>" alt="Foto Profilo" />
            </div>
            <div class="profileInfo" id="profileInfo">
                <h2><%="@" + loggedUser.getUsername() %></h2>
                <p><%=loggedUser.getFirstname() %> <%=loggedUser.getSurname() %></p>
                <p><%=loggedUser.getRole() %></p>
            </div>
        </div>
    </section>

    <section class="buttonContainer">
        <input type="button" id="modifyUserButton" name="modifyUserButton"
               class="button blue" value="Modifica i dati" onclick="modifyUser()"/>
        <input type="button" id="deleteUserButton" name="deleteUserButton"
               class="button red" value="Elimina l'account" onclick="deleteUser()"/>
    </section>

    <%}%>
    <form name="insertForm" method="post" action="Dispatcher">
        <input type="hidden" name="controllerAction" value="UserManagement.insertView"/>
    </form>

    <form name="modifyForm" method="post" action="Dispatcher">
        <input type="hidden" name="controllerAction" value="UserManagement.modifyView"/>
    </form>

    <form name="deleteForm" method="post" action="Dispatcher">
        <input type="hidden" name="controllerAction" value="UserManagement.delete"/>
    </form>
</main>
<%@include file="/include/footer.inc"%>
</body>
</html>
