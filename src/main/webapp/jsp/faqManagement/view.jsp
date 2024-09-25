<%@ page session="false"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.forums.forums.model.mo.*" %>
<%@ page import="com.forums.forums.services.filesystemservice.FileSystemService" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>

<%
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    User loggedUser = (User) request.getAttribute("loggedUser");
    List<FAQ> faqs = (List<FAQ>) request.getAttribute("faqs");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    String menuActiveLink = "FAQ";
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.inc"%>
</head>
<style>

    .faqs {
        margin: 0 auto;
    }

    .faq {
        border: 2px solid #ccc;
    }

    .faq + .faq {
        border-top: none;
    }

    details {
        font-size: 18px;
        border-radius: 8px;
        margin-bottom: 5px;
    }

    summary {
        padding: 10px;
        font-size: 20px;
        cursor: pointer;
        outline: none;
    }

    summary:hover {
        text-decoration: underline;
    }

    details[open] summary {
        border-bottom: 1px solid white;
    }

    details p {
        padding: 20px;
    }

    .faqsNotFound {
        font-size: 20px;
    }
</style>
<script>
    function insertFAQ() {

    }
</script>
<body>
<%@include file="/include/header.inc"%>
<main>
    <section id="pageTitle">
        <h1>
            Domande Frequenti
        </h1>
    </section>

    <section class="buttonContainer">

        <input type="button" name="insertFAQButton" id="insertFAQButton" class="button green" value="Nuova FAQ" onclick="javascript:insertFAQ()"/>

    </section>

    <section class="faqs">
        <% if (faqs != null && !faqs.isEmpty()) {%>
            <% for (FAQ faq : faqs) {%>
            <article class="faq">
                <details>
                    <summary><%=faq.getQuestion()%></summary>
                    <p><%=faq.getAnswer()%></p>
                </details>
            </article>
            <% } %>
        <%} else {%>
            <p class="faqsNotFound" id="faqsNotFound">
                Ancora nessuna domanda.
            </p>
        <%}%>
    </section>

    <form name="insertForm" method="post" action="Dispatcher">
        <input type="hidden" name="controllerAction" value="FAQManagement.insertView"/>
    </form>

    <form name="modifyForm" method="post" action="Dispatcher">
        <input type="hidden" name="faqID"/>
        <input type="hidden" name="controllerAction" value="FAQManagement.modifyView"/>
    </form>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>