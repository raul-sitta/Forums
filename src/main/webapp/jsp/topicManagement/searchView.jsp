<%@ page session="false"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.forums.forums.model.mo.*" %>
<%@ page import="com.forums.forums.services.filesystemservice.FileSystemService" %>

<%
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    User loggedUser = (User) request.getAttribute("loggedUser");
    TopicSearchFilter topicSearchFilter = (TopicSearchFilter) request.getAttribute("topicSearchFilter");
    String menuActiveLink = "Topics";
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.jsp"%>
</head>
<style>

</style>
<script>
    function submitSearch(){
        document.searchForm.submit();
    }

    function goBack(){
        document.backForm.submit();
    }

    function updateAnonymousCheckboxChange(source) {
        let showAnonymous = document.getElementById('showAnonymous');
        let showNonAnonymous = document.getElementById('showNonAnonymous');

        if (source === 'anonymous' && showAnonymous.checked) {
            showNonAnonymous.checked = false;
        } else if (source === 'nonAnonymous' && showNonAnonymous.checked) {
            showAnonymous.checked = false;
        }
    }

    function resetInputs() {
        document.getElementById('title').value = "";
        document.getElementById('authorName').value = "";
        document.getElementById('categoryName').value = "";
        document.getElementById('moreRecentThan').value = "";
        document.getElementById('olderThan').value = "";

        document.getElementById('showAnonymous').checked = false;
        document.getElementById('showNonAnonymous').checked = false;

        document.getElementById('sortNewestFirst').checked = true;
        document.getElementById('sortOldestFirst').checked = false;
    }

    function mainOnLoadHandler(){
        document.getElementById('submitSearchButton').addEventListener("click",submitSearch);
        document.getElementById('backButton').addEventListener("click", goBack);
    }
</script>
<body>
<%@include file="/include/header.jsp"%>
<main>
    <section id="pageTitle">
        <h1>
            Ricerca tra i Topic
        </h1>
    </section>

    <section id="searchFormSection">
        <form name="searchForm" action="Dispatcher" method="post">

            <div class="field clearfix">
                <label for="title">Titolo</label>
                <input type="text" id="title" name="title"
                       value="<%=(topicSearchFilter.getTitle() != null) ? topicSearchFilter.getTitle() : ""%>"
                       size="20" maxlength="100" />
            </div>

            <div class="field clearfix">
                <label for="authorName">Username dell'autore</label>
                <input type="text" id="authorName" name="authorName"
                       value="<%=(topicSearchFilter.getAuthorName() != null) ? topicSearchFilter.getAuthorName() : ""%>"
                       size="20" maxlength="40" />
            </div>

            <div class="field clearfix">
                <label for="categoryName">Categoria</label>
                <input type="text" id="categoryName" name="categoryName"
                       value="<%=(topicSearchFilter.getCategoryName() != null) ? topicSearchFilter.getCategoryName() : ""%>"
                       size="20" maxlength="100" />
            </div>

            <div class="field clearfix">
                <label for="moreRecentThan">Creati dopo il</label>
                <input type="datetime-local" id="moreRecentThan" name="moreRecentThan"
                       value="<%=(topicSearchFilter.getMoreRecentThan() != null) ? topicSearchFilter.getMoreRecentThan() : ""%>"
                />
            </div>

            <div class="field clearfix">
                <label for="olderThan">Creati prima del</label>
                <input type="datetime-local" id="olderThan" name="moreRecentThan"
                       value="<%=(topicSearchFilter.getOlderThan() != null) ? topicSearchFilter.getOlderThan() : ""%>"
                />
            </div>

            <div class="field clearfix">
                <label>Filtra per</label>
                <div>
                    <input type="checkbox" id="showAnonymous" name="showAnonymous"
                        <%=(Boolean.TRUE.equals(topicSearchFilter.getAnonymous())) ? "checked" : ""%>
                           onchange="updateAnonymousCheckboxChange('anonymous')">
                    <label for="showAnonymous">Anonimi</label>
                </div>
                <div>
                    <input type="checkbox" id="showNonAnonymous" name="showNonAnonymous"
                        <%=(Boolean.FALSE.equals(topicSearchFilter.getAnonymous())) ? "checked" : ""%>
                           onchange="updateAnonymousCheckboxChange('nonAnonymous')">
                    <label for="showNonAnonymous">Non anonimi</label>
                </div>
            </div>

            <div class="field clearfix">
                <label>Ordina per</label>
                <div>
                    <input type="radio" id="sortNewestFirst" name="sortOrder" value="true"
                        <%= (Boolean.TRUE.equals(topicSearchFilter.getSortNewestFirst())) ? "checked" : "" %>>
                    <label for="sortNewestFirst">Più recenti</label>
                </div>
                <div>
                    <input type="radio" id="sortOldestFirst" name="sortOrder" value="false"
                        <%= (Boolean.FALSE.equals(topicSearchFilter.getSortNewestFirst())) ? "checked" : "" %>>
                    <label for="sortOldestFirst">Meno recenti</label>
                </div>
            </div>

            <div class="buttonContainer">
                <input type="button" name="resetInputsButton" id="resetInputsButton" class="button blue" value="Reimposta" onclick="resetInputs()"/>
            </div>

            <div class="buttonContainer large">
                <input type="submit" name="submitUserButton" id="submitUserButton" class="button blue" value="Cerca"/>
                <input type="button" name="backButton" id="backButton" class="button red" value="Annulla"/>
            </div>

            <input type="hidden" name="controllerAction" value="TopicManagement.search"/>
        </form>
    </section>

    <form name="backForm" method="post" action="Dispatcher">
        <input type="hidden" name="controllerAction" value="TopicManagement.view">
    </form>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>
