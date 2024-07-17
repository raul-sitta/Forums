<%@ page session="false"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.forums.forums.model.mo.*" %>
<%@ page import="java.util.List" %>

<%
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    User loggedUser = (User) request.getAttribute("loggedUser");
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    String menuActiveLink = "Topics";
    String action = (String) request.getAttribute("action");
    Topic topic = (action.equals("modify")) ? (Topic) request.getAttribute("topic") : null;
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.jsp"%>
</head>
<style>

</style>
<script>
    var status  = "<%=action%>";

    function submitTopic() {
        let f = document.insModForm;

        // Controllo se lo status è 'insert' e assegno il timestamp corrente
        if (status === "insert") {
            f.creationTimestamp.value = new Date().toISOString().slice(0, 19).replace('T', ' ');
        }

        f.controllerAction.value = "TopicManagement." + status;
    }

    function goBack() {
        document.backForm.submit();
    }

    function mainOnLoadHandler() {
        document.insModForm.addEventListener("submit", submitTopic);
    }
</script>
<body>
<%@include file="/include/header.jsp"%>
<main>
    <section id="pageTitle">
        <h1>
            <%=(action.equals("modify")) ? "Modifica Topic" : "Nuovo Topic"%>
        </h1>
    </section>

    <section id="insModFormSection">
        <form name="insModForm" action="Dispatcher" method="post">

            <div class="field clearfix">
                <label for="title">Titolo</label>
                <input type="text" id="title" name="title"
                       value="<%=(action.equals("modify")) ? topic.getTitle() : ""%>"
                       required size="20" maxlength="50" />
            </div>

            <div class="field clearfix">
                <label for="category">Categoria</label>
                <select id="category" name="category" required>
                    <% for (Category category : categories) { %>
                    <option value="<%=category.getName()%>"
                            <%= (action.equals("modify") && topic.getCategory().getName().equals(category.getName())) ? "selected" : "" %>>
                        <%=category.getName()%>
                    </option>
                    <% } %>
                </select>
            </div>

            <div class="field clearfix">
                <label>Topic anonimo</label>
                <div>
                    <input type="radio" id="isAnonymousYes" name="isAnonymous" value="true"
                        <%= (action.equals("modify") && Boolean.TRUE.equals(topic.getAnonymous())) ? "checked" : "" %>
                           required>
                    <label for="isAnonymousYes">Sì</label>

                    <input type="radio" id="isAnonymousNo" name="isAnonymous" value="false"
                        <%= (action.equals("modify") && Boolean.FALSE.equals(topic.getAnonymous())) ? "checked" : (action.equals("modify") ? "" : "checked") %>
                           required>
                    <label for="isAnonymousNo">No</label>
                </div>
            </div>

            <div class="buttonContainer large">
                <input type="submit" name="submitTopicButton" id="submitTopicButton" class="button blue" value="Invia"/>
                <input type="button" name="backButton" id="backButton" class="button red" value="Annulla" onclick="goBack()"/>
            </div>

            <input type="hidden" id="topicID" name="topicID" value="<%=(action.equals("modify")) ? topic.getTopicID() : ""%>"/>
            <input type="hidden" id="creationTimestamp" name="creationTimestamp" value=""/>

            <input type="hidden" name="controllerAction"/>
        </form>
    </section>

    <form name="backForm" method="post" action="Dispatcher">
        <input type="hidden" name="controllerAction" value="TopicManagement.view">
    </form>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>