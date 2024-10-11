<%@page session="false"%>
<%@page import="com.forums.forums.model.mo.User"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    User loggedUser = (User) request.getAttribute("loggedUser");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    String menuActiveLink = "Home";
%>

<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.inc"%>
</head>
<style>
    .welcome {
        font-size: 20px;
    }
</style>
<body>
<%@include file="/include/header.inc"%>
<main>
    <div class="welcome">
        <%if (loggedOn) {%>
            Benvenuto @<%=loggedUser.getUsername()%>!<br/>
            Sei loggato e pronto per iniziare.
        <%} else {%>
            Benvenuto!<br/>
            Per accedere a tutte le funzionalità, ti invitiamo a effettuare il logon o a registrarti.
        <%}%>
    </div>
</main>
<%@include file="/include/footer.inc"%>
</html>
