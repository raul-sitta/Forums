<%@ page session="false"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    String menuActiveLink = "Logon";
    String action = "logon";
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
    .field input[type="password"] {
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

    function submitLogon(){
        let f;
        f = document.logonForm;
        f.controllerAction.value = "HomeManagement."+status;
    }
    function goBack(){
        document.backForm.submit();
    }

    function mainOnLoadHandler(){
        document.logonForm.addEventListener("submit",submitLogon);
        document.logonForm.backButton.addEventListener("click", goBack);
    }
</script>
<body>
<%@include file="/include/header.jsp"%>
<main>
    <section id="pageTitle">
        <h1>
            Inserisci i dati dell'account
        </h1>
    </section>

    <section id="logonFormSection">
        <form name="logonForm" action="Dispatcher" method="post">

            <div class="field clearfix">
                <label for="username">Username</label>
                <input type="text" id="username" name="username"
                       required size="20" maxlength="40"/>
            </div>
            <div class="field clearfix">
                <label for="password">Password</label>
                <input type="password" id="password" name="password"
                       required size="20" maxlength="40"/>
            </div>

            <div class="field clearfix">
                <label>&#160;</label>
                <input type="submit" class="button" value="Invia"/>
                <input type="button" name="backButton" class="button" value="Annulla"/>
            </div>

            <input type="hidden" name="controllerAction"/>
        </form>
    </section>

    <form name="backForm" method="post" action="Dispatcher">
        <input type="hidden" name="controllerAction" value="HomeManagement.view">
    </form>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>
