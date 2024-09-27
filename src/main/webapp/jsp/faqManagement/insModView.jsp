<%@ page session="false"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.forums.forums.model.mo.*" %>
<%@ page import="java.util.List" %>

<%
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    User loggedUser = (User) request.getAttribute("loggedUser");
    String menuActiveLink = "FAQ";
    String action = (String) request.getAttribute("action");
    FAQ faq = (action.equals("modify")) ? (FAQ) request.getAttribute("faq") : null;
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.inc"%>
</head>
<style>
    .faqContent {
        resize: none;
        overflow-y: auto;
        padding: 10px;
        font-size: 16px;
        font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
        border: 2px solid #ccc;
        border-radius: 8px;
    }

    .field .faqContent {
        margin-bottom: 20px;
    }

    .field .faqContent:last-child {
        margin-bottom: 0;
    }


</style>
<script>
    var status  = "<%=action%>";

    function submitFAQ() {
        let f = document.insModForm;

        // Controllo se lo status è 'insert' e assegno il timestamp corrente
        if (status === "insert") {
            f.creationTimestamp.value = new Date().toISOString().slice(0, 19).replace('T', ' ');
        }

        f.controllerAction.value = "FAQManagement." + status;
    }

    function deleteFAQ() {
        if(confirm("Attenzione! Questa azione e' irreversibile. Vuoi procedere?")){
            document.deleteForm.faqID.value = <%=(action.equals("modify")) ? faq.getFaqID() : ""%>
                document.deleteForm.submit();
        }
    }

    function goBack() {
        document.backForm.submit();
    }

    function mainOnLoadHandler() {
        document.insModForm.addEventListener("submit", submitFAQ);
    }
</script>
<body>
<%@include file="/include/header.inc"%>
<main>
    <section id="pageTitle">
        <h1>
            <%=(action.equals("modify")) ? "Modifica FAQ" : "Nuova FAQ"%>
        </h1>
    </section>

    <section id="insModFormSection">
        <form name="insModForm" action="Dispatcher" method="post">

            <div class="field clearfix">
                <label for="faqQuestion"></label>
                <textarea id="faqQuestion" name="faqQuestion" class="faqContent" rows="5" cols="50"
                          maxlength="100" required><%=(action.equals("modify")) ? faq.getQuestion() : ""%></textarea>

                <label for="faqAnswer"></label>
                <textarea id="faqAnswer" name="faqAnswer" class="faqContent" rows="5" cols="50"
                          maxlength="500" required><%=(action.equals("modify")) ? faq.getAnswer() : ""%></textarea>
            </div>

            <div class="buttonContainer large">
                <input type="submit" name="submitFAQButton" id="submitFAQButton" class="button blue" value="Invia"/>
                <% if (action.equals("modify")) { %>
                <input type="button" name="deleteFAQButton" id="deleteFAQButton" class="button red" value="Elimina" onclick="deleteFAQ()"/>
                <% } %>
                <input type="button" name="backButton" id="backButton" class="button red" value="Annulla" onclick="goBack()"/>
            </div>

            <input type="hidden" id="faqID" name="faqID" value="<%=(action.equals("modify")) ? faq.getFaqID() : ""%>"/>
            <input type="hidden" id="creationTimestamp" name="creationTimestamp" value=""/>
            <input type="hidden" id="authorID" name="authorID" value="<%=loggedUser.getUserID()%>"/>

            <input type="hidden" name="controllerAction"/>
        </form>
    </section>

    <form name="backForm" method="post" action="Dispatcher">
        <input type="hidden" name="controllerAction" value="FAQManagement.view">
    </form>

    <form name="deleteForm" method="post" action="Dispatcher">
        <input type="hidden" name="faqID"/>
        <input type="hidden" name="controllerAction" value="FAQManagement.delete"/>
    </form>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>