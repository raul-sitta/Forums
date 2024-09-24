<%@ page session="false"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.forums.forums.model.mo.*" %>
<%@ page import="com.forums.forums.services.filesystemservice.FileSystemService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.io.File" %>

<%
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    User loggedUser = (User) request.getAttribute("loggedUser");
    Long postID = (Long) request.getAttribute("postID");
    Media media = (Media) request.getAttribute("media");
    NavigationState navigationState = (NavigationState) request.getAttribute("navigationState");
    String menuActiveLink = "Topics";
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.inc"%>
</head>
<style>
    #mediaImage {
        width: 100%;
        overflow: hidden;
    }

    #mediaImage img {
        width: 100%;
        height: auto;
        border-radius: 8px;
    }

    .mediaNotFound {
        font-size: 20px;
    }

</style>
<script>
    <% if (media != null) { %>
        function downloadMedia(mediaPath){
            document.downloadMediaForm.submit();
        }
    <% } %>

    function goBack(){
        document.backForm.submit();
    }
</script>
<body>
<%@include file="/include/header.inc"%>
<main>
    <section id="pageTitle">
        <h1>
            <%=(media != null) ? media.getPath().substring(media.getPath().lastIndexOf("/") + 1) : "Immagine non disponibile."%>
        </h1>
    </section>

    <section class="buttonContainer">

        <input type="button" name="backButton" id="backButton" class="button red" value="Indietro" onclick="javascript:goBack()"/>

        <% if (media != null) { %>
            <input type="button" name="downloadMediaButton" id="downloadMediaButton" class="button green" value="Scarica" onclick="javascript:downloadMedia()"/>
        <% } %>

    </section>

    <% if (media != null) { %>
        <section id="mediaImage" class="clearfix">

            <img src="<%= media.getPath() %>" alt="Immagine del Media  <%=media.getPath().substring(media.getPath().lastIndexOf("/") + 1)%>">

        </section>

        <form name="downloadMediaForm" method="get" action="FileDownloadServlet">
            <input type="hidden" name="mediaPath" value="<%=media.getPath()%>"/>
        </form>
    <% } %>

    <form name="backForm" method="post" action="Dispatcher">
        <input type="hidden" name="postID" value="<%=postID%>"/>
        <input type="hidden" name="controllerAction" value="MediaManagement.view"/>
    </form>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>