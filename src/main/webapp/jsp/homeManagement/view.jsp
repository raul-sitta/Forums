<%@page session="false"%>
<%@page import="com.forums.forums.model.mo.User"%>

<%
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    User loggedUser = (User) request.getAttribute("loggedUser");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    String menuActiveLink = "Home";
%>

<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.jsp"%>
</head>
<body>
<%@include file="/include/header.jsp"%>
<main>
    <%if (loggedOn) {%>
    Benvenuto @<%=loggedUser.getUsername()%> su Forums!<br/>
    Sei loggato.
    <%} else {%>
    Benvenuto.
    Fai il logon o registrati.
    <%}%>
</main>
<%@include file="/include/footer.inc"%>
</html>
