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
        document.getElementById('submitLogonButton').addEventListener("click",submitLogon);
        document.getElementById('backButton').addEventListener("click", goBack);
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

            <div class="buttonContainer large">
                <input type="submit" name="submitLogonButton" id="submitLogonButton" class="button blue" value="Invia"/>
                <input type="button" name="backButton" id="backButton" class="button red" value="Annulla"/>
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
