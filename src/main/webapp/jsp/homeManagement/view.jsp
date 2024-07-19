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
            Benvenuto @<%=loggedUser.getUsername()%> su Forums! Qui puoi esplorare una vasta gamma di topic e partecipare a discussioni su diversi argomenti.<br/><br/>
            Crea nuovi topic, rispondi ai post e condividi le tue opinioni con una comunità di persone che condividono i tuoi interessi.<br/><br/>
            Esplora le diverse categorie, resta aggiornato sugli ultimi Topic e diventa un membro attivo della nostra comunità.<br/><br/>
            Grazie per essere parte del nostro forum!<br/><br/>
            Sei loggato e pronto a partecipare!
        <%} else {%>
            Benvenuto su Forums, la piattaforma ideale per esprimere le tue opinioni e interagire con altri utenti su una varietà di argomenti.<br/><br/>
            Per accedere a tutte le funzionalità, ti invitiamo a effettuare il logon o a registrarti.<br/><br/>
            Unisciti a noi oggi stesso per iniziare a esplorare nuovi contenuti, ampliare la tua rete e partecipare a numerose conversazioni.<br/><br/>
            La registrazione è semplice e veloce: non perdere l'opportunità di far sentire la tua voce nel forum!
        <%}%>
    </div>
</main>
<%@include file="/include/footer.inc"%>
</html>
