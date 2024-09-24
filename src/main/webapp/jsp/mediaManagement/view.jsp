<%@ page session="false"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.forums.forums.model.mo.*" %>
<%@ page import="com.forums.forums.services.filesystemservice.FileSystemService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.io.File" %>

<%
    int i = 0;
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    User loggedUser = (User) request.getAttribute("loggedUser");
    Post post = (Post) request.getAttribute("post");
    NavigationState navigationState = (NavigationState) request.getAttribute("navigationState");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    String menuActiveLink = "Topics";
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.inc"%>
</head>
<style>
    .media {
        display: flex;
        border: 2px solid #ccc;
        padding: 10px;
        margin-bottom: 0;
        border-radius: 8px;
    }

    .media + .media {
        border-top: none;
    }

    .mediaPreview {
        width: 150px;
        height: 150px;
        border: 2px solid #ccc;
        margin-right: 15px;
        border-radius: 8px;
    }

    .mediaPreview img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 8px;
    }

    .mediaDetails {
        font-size: 18px;
        color: #666;
        display: flex;
        flex-direction: column;
    }

    .mediaButtons {
        margin-top: auto;
    }

    .mediaDetails span {
        margin-bottom: 6px;
    }

    .mediaDetails .mediaTitle {
        font-size: 22px;
        font-weight: bold;
        margin-bottom: 5px;
    }

    .mediaButtons img {
        width: 60px;
        height: auto;
        transition: filter 0.3s ease;
        padding: 0;
    }

    .mediaButtons img+img {
        margin-left: 6px;
    }

    .mediaButtons img:hover {
        filter: brightness(85%);
    }

    .mediasNotFound {
        font-size: 20px;
    }

</style>
<script>
    function deleteMedia(mediaID) {
        if(confirm("Attenzione! Questa azione e' irreversibile. Vuoi procedere?")){
            document.deleteMediaForm.mediaID.value = mediaID
                document.deleteMediaForm.submit();
        }
    }

    function viewImage(mediaID) {
        let f = document.viewImageForm;
        f.mediaID.value = mediaID;
        f.submit();
    }

    function downloadMedia(mediaPath){
        let f = document.downloadMediaForm;
        f.mediaPath.value = mediaPath;
        f.submit();
    }

    function goBack(){
        document.backForm.submit();
    }
</script>
<body>
<%@include file="/include/header.inc"%>
<main>
    <section id="pageTitle">
        <h1>
            Allegati al post "<%=(post.getContent().length() > 30) ? post.getContent().substring(0, 30) + "..." : post.getContent()%>"
        </h1>
    </section>

    <section class="buttonContainer">

        <input type="button" name="backButton" id="backButton" class="button red" value="Indietro" onclick="javascript:goBack()"/>

    </section>

    <section id="medias" class="clearfix">
        <% if (post.getMedias() != null && !post.getMedias().isEmpty()) { %>
            <% for (Media media : post.getMedias()) { %>
            <article class="media">
                <div class="mediaPreview">
                    <img src="<%= media.getPath().matches(".*\\.(png|jpg|jpeg|gif)$") ? media.getPath() : "images/genericFile.png" %>" alt="Media Preview">
                </div>
                <div class="mediaDetails">
                    <span class="mediaTitle"><%= media.getPath().substring(media.getPath().lastIndexOf("/") + 1) %></span>
                    <span class="mediaDesc">
                        <%= FileSystemService.getFileDescription(
                                media.getPath().substring(media.getPath().lastIndexOf(".") + 1)
                        ) %>
                    </span>
                    <span class="mediaDate">Aggiunto in data <%= sdf.format(media.getCreationTimestamp()) %></span>
                    <div class="mediaButtons">
                        <img src="images/downloadMedia.png" alt="Scarica Media" class="button adjusted" onclick="downloadMedia('<%=media.getPath()%>')" />

                        <% if (media.getPath().matches(".*\\.(png|jpg|jpeg|gif)$")) { %>
                            <img src="images/viewImage.png" alt="Visualizza Immagine" class="button adjusted" onclick="viewImage('<%=media.getMediaID()%>')" />
                        <% } %>

                        <% if ((loggedUser.getUserID() == post.getAuthor().getUserID()) || (loggedUser.getRole().equals("admin"))) { %>
                            <img src="images/deleteMedia.png" alt="Elimina Media" class="button adjusted" onclick="deleteMedia('<%=media.getMediaID()%>')" />
                        <% } %>
                    </div>
                </div>
            </article>
            <% } %>
        <% } else {%>
            <p class="mediasNotFound" id="mediasNotFound">
                Ancora nessun allegato.
            </p>
        <%}%>
    </section>

    <form name="downloadMediaForm" method="get" action="FileDownloadServlet">
        <input type="hidden" name="mediaPath" />
    </form>

    <form name="viewImageForm" method="get" action="Dispatcher">
        <input type="hidden" name="mediaID" />
        <input type="hidden" name="postID" value="<%=post.getPostID()%>"/>
        <input type="hidden" name="controllerAction" value="MediaManagement.imageView"/>
    </form>

    <form name="deleteMediaForm" method="post" action="Dispatcher">
        <input type="hidden" name="mediaID"/>
        <input type="hidden" name="postID" value="<%=post.getPostID()%>"/>
        <input type="hidden" name="controllerAction" value="MediaManagement.delete"/>
    </form>

    <form name="backForm" method="post" action="Dispatcher">
        <input type="hidden" name="controllerAction" value="PostManagement.view"/>
    </form>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>