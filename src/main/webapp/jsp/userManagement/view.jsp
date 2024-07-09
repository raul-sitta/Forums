<%@ page session="false"%>
<%@ page import="com.forums.forums.model.mo.User" %>
<%@ page import="com.forums.forums.services.filesystemservice.FileSystemService" %>

<%
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    User loggedUser = (User) request.getAttribute("loggedUser");
    String profilePicPath = (String) request.getAttribute("profilePicPath");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    String action = (loggedUser!=null) ? "modify" : "insert";
    String menuActiveLink = (loggedUser!=null) ? "Account" : "Registrati";
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.jsp"%>
    <style>
        main {
            display: flex;
            justify-content: space-between;
            flex-direction: column;
        }
        /* Stile dei pulsanti */
        .button {
            padding: 12px 24px;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s ease;
            margin-bottom: 12px;
        }

        /* Stile specifico per il pulsante "Registrati" */
        #insertUserButton {
            background-color: #28a745;
        }

        #insertUserButton:hover {
            background-color: #1f9233;
        }

        /* Stile specifico per il pulsante "Modifica i dati" */
        #modifyUserButton {
            background-color: #28a745;
        }

        #modifyUserButton:hover {
            background-color: #1f9233;
        }

        /* Stile specifico per il pulsante "Elimina l'account" */
        #deleteUserButton {
            background-color: #dc3545;
        }

        #deleteUserButton:hover {
            background-color: #b72230;
        }

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
        }

        #profileInfo h2 {
            margin: 0;
            font-size: 24px;
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
<%@include file="/include/header.jsp"%>
<main>
    <section id="pageTitle">
        <h1>Gestione Account</h1>
    </section>

    <%if (!loggedOn){%>
    <section id="insertUserButtonSelection">
        <input type="button" id="insertUserButton" name="insertUserButton"
               class="button" value="Registrati" onclick="insertUser()"/>
    </section>
    <%}%>

    <%if (loggedOn){%>
    <section id="userProfilePreview">
        <div class="profileContainer" id="profileContainer">
            <div class="profilePic" id="profilePic">
                <img src="<%=profilePicPath%>" alt="Foto Profilo" />
            </div>
            <div class="profileInfo" id="profileInfo">
                <h2><%="@" + loggedUser.getUsername() %></h2>
                <p><%=loggedUser.getFirstname() %> <%=loggedUser.getSurname() %></p>
                <p><%=loggedUser.getRole() %></p>
            </div>
        </div>
    </section>
    <section id="modifyUserButtonSelection">
        <input type="button" id="modifyUserButton" name="modifyUserButton"
               class="button" value="Modifica i dati" onclick="modifyUser()"/>
    </section>

    <section id="deleteUserButtonSelection">
        <input type="button" id="deleteUserButton" name="deleteUserButton"
               class="button" value="Elimina l'account" onclick="deleteUser()"/>
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
