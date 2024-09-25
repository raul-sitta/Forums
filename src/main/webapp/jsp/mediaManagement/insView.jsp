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
    Post post = (Post) request.getAttribute("post");
    Long mediasInDataBase = (Long) request.getAttribute("mediasInDataBase");
    NavigationState navigationState = (NavigationState) request.getAttribute("navigationState");
    String menuActiveLink = "Topics";
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.inc"%>
</head>
<style>
    .mediasCount {
        font-size: 20px;
    }

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
</style>
<script>

    let fileIDCounter = 0;
    let maxFilesAllowed = 10;
    let mediasInDataBase = <%=mediasInDataBase%>;
    let maxMediasAllowed = 20;

    function goBack(){
        document.backForm.submit();
    }

    function submitMedias() {
        let f = document.insForm;
        f.creationTimestamp.value = new Date().toISOString().slice(0, 19).replace('T', ' ');

        let fileInputs = f.querySelectorAll('input[type="file"]');
        fileInputs.forEach(input => {
            if (!input.value) {
                input.remove();
            }
        });
    }

    function updateMediaCount() {
        var fileInputs = document.querySelectorAll('input[type="file"]');
        var nonEmptyFiles = 0;
        fileInputs.forEach(function(input) {
            if (input.files.length > 0 && input.files[0].size > 0) {
                nonEmptyFiles++;
            }
        });
        var mediasCount = document.getElementById('mediasCount');
        mediasCount.textContent = nonEmptyFiles + " file selezionat";
        if (nonEmptyFiles !== 1) mediasCount.textContent += "i.";
        else mediasCount.textContent += "o.";
    }

    function formatFileSize(bytes) {
        const k = 1024;
        if (bytes >= k * k) {
            return (bytes / (k * k)).toFixed(2) + ' MB';
        } else if (bytes >= k) {
            return (bytes / k).toFixed(2) + ' KB';
        } else {
            return bytes + ' bytes';
        }
    }

    function createMediaArticle(file, img) {
        var article = document.createElement('article');
        article.className = "media";

        var divPreview = document.createElement('div');
        divPreview.className = "mediaPreview";
        divPreview.appendChild(img);

        var divDetails = document.createElement('div');
        divDetails.className = "mediaDetails";

        var spanTitle = document.createElement('span');
        spanTitle.className = "mediaTitle";
        spanTitle.textContent = file.name;

        var spanSize = document.createElement('span');
        spanSize.className = "mediaSize";
        spanSize.textContent = "Dimensione: " + formatFileSize(file.size);

        var divButtons = document.createElement('div');
        divButtons.className = "mediaButtons";

        divDetails.appendChild(spanTitle);
        divDetails.appendChild(spanSize);
        divDetails.appendChild(divButtons);

        article.appendChild(divPreview);
        article.appendChild(divDetails);

        return article;
    }

    function createCancelButton(id, article, img) {
        var button = document.createElement('img');
        button.src = "images/deleteMedia.png";
        button.alt = "Elimina Media";
        button.className = "button adjusted";
        button.onclick = function() {
            var fileInputToRemove = document.getElementById('file-' + id);
            if (fileInputToRemove) {
                document.getElementById('insForm').removeChild(fileInputToRemove);
            }
            article.remove();
            URL.revokeObjectURL(img.src);
            updateMediaCount();
        };
        return button;
    }

    function createFileInput() {
        var newFileInput = document.createElement('input');
        newFileInput.type = 'file';
        newFileInput.name = 'files[]';
        newFileInput.id = 'file-' + fileIDCounter;
        newFileInput.style.display = 'none';

        newFileInput.onchange = function() {
            var file = newFileInput.files[0];

            // Controllo che il file abbia dimensione < di 100 mega
            if (file.size > 100 * 1024 * 1024) {
                alert("Il file " + file.name + " deve avere dimensione inferiore a 100 MB.");
                newFileInput.remove();
                return;
            }

            var img = document.createElement('img');
            if (file.type.startsWith('image/')) {
                img.src = URL.createObjectURL(file);
            } else {
                img.src = "images/genericFile.png";
            }
            img.onload = function() {
                URL.revokeObjectURL(this.src);
            };
            img.onerror = function() {
                this.src = "images/genericFile.png";
            };

            var container = document.getElementById('medias');
            var mediaArticle = createMediaArticle(file, img);
            container.prepend(mediaArticle);

            var cancelBtn = createCancelButton(fileIDCounter, mediaArticle, img);
            mediaArticle.querySelector('.mediaButtons').appendChild(cancelBtn);

            updateMediaCount();

            fileIDCounter++;
        };

        document.getElementById('insForm').appendChild(newFileInput);
        newFileInput.click();
    }

    function uploadMedia() {
        var existingInputs = document.querySelectorAll('input[type="file"]');
        var foundEmpty = false;

        existingInputs.forEach(input => {
            if (!input.value && !foundEmpty) {
                input.click();
                foundEmpty = true;
            }
        });

        if (!foundEmpty) {
            var nonEmptyFiles = Array.from(existingInputs).filter(input => input.value).length;

            if (nonEmptyFiles < maxFilesAllowed) {
                if (nonEmptyFiles + mediasInDataBase < maxMediasAllowed) {
                    createFileInput();
                } else {
                    alert("Massimo " + maxMediasAllowed + " media consentiti per post. Attualmente, " + mediasInDataBase + " sono già stati caricati.");
                }
            } else {
                alert("Non è possibile caricare più di " + maxFilesAllowed + " file alla volta.");
            }
        }
    }


    function mainOnLoadHandler() {
        document.getElementById('submitMediasButton').addEventListener("click",submitMedias);
        document.getElementById('uploadMediaButton').addEventListener('click', uploadMedia);
        document.getElementById('backButton').addEventListener("click", goBack);
    }

</script>
<body>
<%@include file="/include/header.inc"%>
<main>
    <section id="pageTitle">
        <h1>
            Inserisci allegati al post "<%=(post.getContent().length() > 30) ? post.getContent().substring(0, 30) + "..." : post.getContent()%>"
        </h1>
    </section>

    <section class="buttonContainer">

        <input type="button" name="uploadMediaButton" id="uploadMediaButton" class="button blue" value="Scegli File"/>

    </section>

    <section id="mediasCountSection" class="clearfix">
        <p class="mediasCount" id="mediasCount">
            0 file selezionati.
        </p>
    </section>

    <section id="medias" class="clearfix">
        <form name="insForm" id="insForm" action="Dispatcher" method="post" enctype="multipart/form-data">

            <div class="buttonContainer large">
                <input type="submit" name="submitMediasButton" id="submitMediasButton" class="button blue" value="Invia"/>
                <input type="button" name="backButton" id="backButton" class="button red" value="Annulla"/>
            </div>

            <input type="hidden" id="creationTimestamp" name="creationTimestamp" />
            <input type="hidden" id="uploaderID" name="uploaderID" value="<%=loggedUser.getUserID()%>"/>
            <input type="hidden" id="postID" name="postID" value="<%=post.getPostID()%>"/>
            <input type="hidden" name="controllerAction" value="MediaManagement.insert"/>

        </form>
    </section>

    <form name="backForm" method="post" action="Dispatcher">
        <input type="hidden" name="controllerAction" value="PostManagement.view"/>
    </form>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>