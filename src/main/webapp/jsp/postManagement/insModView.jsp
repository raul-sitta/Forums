<%@ page session="false"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.forums.forums.model.mo.*" %>
<%@ page import="java.util.List" %>

<%
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    User loggedUser = (User) request.getAttribute("loggedUser");
    String menuActiveLink = "Supporto";
    String action = (String) request.getAttribute("action");
    Post post = (action.equals("modify")) ? (Post) request.getAttribute("post") : null;
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.inc"%>
</head>
<style>
    #content {
        resize: none;
        overflow-y: auto;
        padding: 10px;
        font-size: 16px;
        font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
        border: 2px solid #ccc;
        border-radius: 8px;
    }

</style>
<script>
    var status  = "<%=action%>";

    function submitPost() {
        let f = document.insModForm;

        // Controllo se lo status è 'insert' e assegno il timestamp corrente
        if (status === "insert") {
            f.creationTimestamp.value = new Date().toISOString().slice(0, 19).replace('T', ' ');
        }

        f.controllerAction.value = "PostManagement." + status;
    }

    function deletePost() {
        if(confirm("Attenzione! Questa azione e' irreversibile. Vuoi procedere?")){
            document.deletePostForm.postID.value = <%=(action.equals("modify")) ? post.getPostID() : ""%>
                document.deletePostForm.submit();
        }
    }

    function goBack() {
        document.backForm.submit();
    }

    function mainOnLoadHandler() {
        document.insModForm.addEventListener("submit", submitPost);
    }
</script>
<body>
<%@include file="/include/header.inc"%>
<main>
    <section id="pageTitle">
        <h1>
            <%=(action.equals("modify")) ? "Modifica Post" : "Nuovo Post"%>
        </h1>
    </section>

    <section id="insModFormSection">
        <form name="insModForm" action="Dispatcher" method="post">

            <div class="field clearfix">
                <label for="content"></label>
                <textarea id="content" name="content" rows="10" cols="50"
                          maxlength="10000" required><%=(action.equals("modify")) ? post.getContent() : ""%></textarea>
            </div>

            <div class="buttonContainer large">
                <input type="submit" name="submitPostButton" id="submitPostButton" class="button blue" value="Invia"/>
                <% if (action.equals("modify")) { %>
                    <input type="button" name="deletePostButton" id="deletePostButton" class="button red" value="Elimina" onclick="deletePost()"/>
                <% } %>
                <input type="button" name="backButton" id="backButton" class="button red" value="Annulla" onclick="goBack()"/>
            </div>

            <input type="hidden" id="postID" name="postID" value="<%=(action.equals("modify")) ? post.getPostID() : ""%>"/>
            <input type="hidden" id="creationTimestamp" name="creationTimestamp" value=""/>
            <input type="hidden" id="authorID" name="authorID" value="<%=loggedUser.getUserID()%>"/>

            <input type="hidden" name="controllerAction"/>
        </form>
    </section>

    <form name="backForm" method="post" action="Dispatcher">
        <input type="hidden" name="controllerAction" value="PostManagement.view">
    </form>

    <form name="deletePostForm" method="post" action="Dispatcher">
        <input type="hidden" name="postID"/>
        <input type="hidden" name="controllerAction" value="PostManagement.delete"/>
    </form>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>