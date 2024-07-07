<%@ page session="false"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.forums.forums.model.mo.User" %>

<%
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    User loggedUser = (User) request.getAttribute("loggedUser");
    User user = (User) request.getAttribute("user");
    String menuActiveLink = (loggedUser !=null) ? "Account" : "Registrati";
    String action = (loggedUser !=null) ? "modify" : "insert";
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

    /* Stile del pulsante "Annulla" */
    .field input[name="backButton"] {
        background-color: #dc3545;
    }

    /* Allinea il pulsante "Annulla" a destra */
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
</style>
<script>
    var status  = "<%=action%>";

    function submitUser(){
        let f;
        f = document.insModForm;
        f.controllerAction.value = "UserManagement."+status;
    }
    function goBack(){
        document.backForm.submit();
    }

    function mainOnLoadHandler(){
        document.insModForm.addEventListener("submit",submitUser);
        document.insModForm.backButton.addEventListener("click", goBack);
    }
</script>
<body>
<%@include file="/include/header.jsp"%>
<main>
    <section id="pageTitle">
        <h1>
            <%=(action.equals("modify")) ? "Modifica i dati dell'account" : "Inserisci i dati dell'account"%>
        </h1>
    </section>

    <section id="insModFormSection">
        <form name="insModForm" action="Dispatcher" method="post">

            <div class="field clearfix">
                <label for="username">Username</label>
                <input type="text" id="username" name="username"
                       value="<%=(action.equals("modify")) ? user.getUsername() : ""%>"
                       required size="20" maxlength="40"/>
            </div>
            <div class="field clearfix">
                <label for="password">Password</label>
                <input type="password" id="password" name="password"
                       value="<%=(action.equals("modify")) ? user.getPassword() : ""%>"
                       required size="20" maxlength="40"/>
            </div>
            <div class="field clearfix">
                <label for="firstname">Nome</label>
                <input type="text" id="firstname" name="firstname"
                       value="<%=(action.equals("modify")) ? user.getFirstname() : ""%>"
                       required size="20" maxlength="50"/>
            </div>
            <div class="field clearfix">
                <label for="surname">Cognome</label>
                <input type="text" id="surname" name="surname"
                       value="<%=(action.equals("modify")) ? user.getSurname() : ""%>"
                       required size="20" maxlength="50"/>
            </div>
            <div class="field clearfix">
                <label for="email">Email</label>
                <input type="email" id="email" name="email"
                       value="<%=(action.equals("modify")) ? user.getEmail() : ""%>"
                       required size="20" maxlength="100"/>
            </div>
            <div class="field clearfix">
                <label for="birthDate">Data di Nascita</label>
                <input type="date" id="birthDate" name="birthDate"
                       value="<%=(action.equals("modify")) ? user.getBirthDate() : ""%>"
                       required />
            </div>

            <input type="hidden" id="role" name="role" value="<%=(action.equals("modify")) ? user.getRole() : "User"%>"/>
            <input type="hidden" id="deleted" name="deleted" value="<%=(action.equals("modify")) ? user.getDeleted() : "N"%>"/>

            <div class="field clearfix">
                <label>&#160;</label>
                <input type="submit" class="button" value="Invia"/>
                <input type="button" name="backButton" class="button" value="Annulla"/>
            </div>

            <input type="hidden" name="controllerAction"/>
        </form>
    </section>

    <form name="backForm" method="post" action="Dispatcher">
        <input type="hidden" name="controllerAction" value="UserManagement.view">
    </form>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>
