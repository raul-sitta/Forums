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

    .faqAuthor {
        font-size: 15px;
        font-style: italic;
        color: #707070;
        padding-top:0;
    }

    .faqsNotFound {
        font-size: 20px;
    }

    .highlight {
        background-color: yellow;
    }
</style>
<script>

    function filterFAQs() {
        let input = document.getElementById('searchFAQ');
        let filter = input.value.toLowerCase();
        let faqsSection = document.getElementsByClassName('faq');

        for (let i = 0; i < faqsSection.length; i++) {
            let question = faqsSection[i].getElementsByTagName('summary')[0];
            let answer = faqsSection[i].getElementsByTagName('p')[0];

            if (question.innerHTML.toLowerCase().indexOf(filter) > -1 || answer.innerHTML.toLowerCase().indexOf(filter) > -1) {
                faqsSection[i].style.display = "";
            } else {
                faqsSection[i].style.display = "none";
            }
        }
    }

    <% if (loggedUser.getRole().equals("Admin")) { %>
        function insertFAQ() {
            document.insertForm.submit();
        }

        function modifyFAQ(faqID) {
            let f = document.modifyForm;
            f.faqID.value = faqID;
            f.submit();
        }
    <% } %>
</script>
<body>
<%@include file="/include/header.inc"%>
<main>
    <section id="pageTitle">
        <h1>
            Domande Frequenti
        </h1>
    </section>

    <% if (loggedUser.getRole().equals("Admin")) { %>
        <section class="buttonContainer">

            <input type="button" name="insertFAQButton" id="insertFAQButton" class="button green" value="Nuova FAQ" onclick="javascript:insertFAQ()"/>

        </section>
    <% } %>

    <section id="searchContainer" class="field clearfix">
        <label for="searchFAQ">Filtra per parola chiave</label>
        <input type="text" id="searchFAQ" onkeyup="filterFAQs()">
    </section>

    <section class="faqs">
        <% if (faqs != null && !faqs.isEmpty()) {%>
            <% for (FAQ faq : faqs) {%>
            <article class="faq">
                <details>
                    <summary><%=faq.getQuestion()%></summary>
                    <% if (loggedUser.getRole().equals("Admin")) { %>
                        <p class="clickable" onclick="javascript:modifyFAQ(<%=faq.getFaqID()%>)"><%=faq.getAnswer()%></p>
                    <% } else { %>
                        <p><%=faq.getAnswer()%></p>
                    <% } %>
                        <p class="faqAuthor">Scritto da @<%=faq.getAuthor().getUsername()%> in data <%=sdf.format(faq.getCreationTimestamp())%></p>
                </details>
            </article>
            <% } %>
        <%} else {%>
            <p class="faqsNotFound" id="faqsNotFound">
                Ancora nessuna domanda.
            </p>
        <%}%>
    </section>

    <% if (loggedUser.getRole().equals("Admin")) { %>

        <form name="insertForm" method="post" action="Dispatcher">
            <input type="hidden" name="controllerAction" value="FAQManagement.insertView"/>
        </form>

        <form name="modifyForm" method="post" action="Dispatcher">
            <input type="hidden" name="faqID"/>
            <input type="hidden" name="controllerAction" value="FAQManagement.modifyView"/>
        </form>

    <% } %>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>