<%@ page session="false"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.forums.forums.model.mo.*" %>
<%@ page import="com.forums.forums.services.filesystemservice.FileSystemService" %>

<%
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    User loggedUser = (User) request.getAttribute("loggedUser");
    User user = (loggedUser !=null) ? (User) request.getAttribute("user") : null;
    String menuActiveLink = (loggedUser !=null) ? "Account" : "Registrati";
    String action = (loggedUser !=null) ? "modify" : "insert";
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/include/htmlHead.jsp"%>
</head>
<style>
    /* Preview dell'immagine */
    #preview{
        width: 250px;
        height: 250px;
    }
</style>
<script>
    var status  = "<%=action%>";

    var defaultImage = "<%=FileSystemService.DEFAULT_PROFILE_PIC_PATH%>";

    function submitUser() {
        let f = document.insModForm;

        let username = f.username.value;
        let firstname = f.firstname.value;
        let surname = f.surname.value;

        // Formatto i parametri già validati
        username = username.toLowerCase();
        firstname = capitalizeWords(firstname);
        surname = capitalizeWords(surname);

        // Assegno i valori validati ai campi del form
        f.username.value = username;
        f.firstname.value = firstname;
        f.surname.value = surname;

        // Controllo se lo status è 'insert' e assegno il timestamp corrente
        if (status === "insert") {
            f.registrationTimestamp.value = new Date().toISOString().slice(0, 19).replace('T', ' ');
        }

        f.controllerAction.value = "UserManagement." + status;
    }

    function capitalizeWords(string) {
        return string.split(' ').map(word =>
            word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()
        ).join(' ');
    }

    function goBack(){
        document.backForm.submit();
    }

    function validateUsername() {
        var usernameInput = document.getElementById('username');
        var regex = /^[a-zA-Z0-9.]+$/;

        usernameInput.setCustomValidity('');

        if (!regex.test(usernameInput.value)) {
            usernameInput.setCustomValidity("L'username può contenere solo lettere, numeri e punti.");
        }

        usernameInput.reportValidity();
    }

    function validateImage() {
        let fileInput = document.querySelector('input[type=file]');
        let file = fileInput.files[0];

        const maxFileSize = 5 * 1024 * 1024; // 5 MB
        const minWidth = 250;
        const minHeight = 250;
        const maxWidth = 2500;
        const maxHeight = 2500;

        if (file.size > maxFileSize) {
            fileInput.setCustomValidity('La dimensione del file non deve superare i 5 MB.');
            setPreview("");
            return;
        }

        if (file.type !== 'image/png') {
            fileInput.setCustomValidity('L\'immagine deve essere in formato PNG.');
            setPreview("");
            return;
        }

        var reader = new FileReader();
        reader.onload = function (e) {
            var image = new Image();
            image.onload = function () {
                setPreview(e.target.result);
                if (image.width > maxWidth || image.height > maxHeight) {
                    fileInput.setCustomValidity('Le dimensioni dell\'immagine non devono superare 2500x2500 pixel.');
                } else if (image.width < minWidth || image.height < minHeight) {
                    fileInput.setCustomValidity('L\'immagine deve essere almeno di 250x250 pixel.');
                } else {
                    fileInput.setCustomValidity(''); // L'immagine è valida
                    setDeleteFlag(false);
                    setUpdateFlag(true);
                }
            };
            image.src = e.target.result;
        };
        reader.readAsDataURL(file);
    }

    function requestImageUpload() {
        document.getElementById('image').click();
    }

    function requestImageDeletion() {
        setDeleteFlag(true);
        setUpdateFlag(false);
        setPreview(defaultImage);
        // Rimuovo temporaneamente il listener per evitare trigger non desiderati
        let fileInput = document.getElementById('image');
        fileInput.removeEventListener('change', validateImage);

        // Resetto il campo file
        fileInput.value = "";

        // Resetto la validità
        fileInput.setCustomValidity("");

        // Riaggancio il listener di change
        fileInput.addEventListener('change', validateImage);
    }

    function setPreview(image) {
        preview.src = image;
    }

    function setDeleteFlag(state) {
        var f = document.insModForm;
        f.deleteImage.value = state ? "true" : "false";
    }

    function setUpdateFlag(state) {
        var f = document.insModForm;
        f.updateImage.value = state ? "true" : "false";
    }

    function mainOnLoadHandler(){
        document.getElementById('submitUserButton').addEventListener("click",submitUser);
        document.getElementById('backButton').addEventListener("click", goBack);
        document.getElementById('image').addEventListener('change', validateImage);
        document.getElementById('uploadImageButton').addEventListener("click", requestImageUpload);
        document.getElementById('deleteImageButton').addEventListener("click", requestImageDeletion);
    }
</script>
<body>
<%@include file="/include/header.jsp"%>
<main>
    <section id="pageTitle">
        <h1>
            <%=(action.equals("modify")) ? "Modifica i dati dell'account" : "Inserisci i dati dell'account"%>
        </h1>
    </section>

    <section id="insModFormSection">
        <form name="insModForm" action="Dispatcher" method="post" enctype="multipart/form-data">

            <div class="field clearfix">
                <label for="username">Username</label>
                <input type="text" id="username" name="username"
                       value="<%=(action.equals("modify")) ? user.getUsername() : ""%>"
                       required size="20" maxlength="40" onchange="validateUsername()">
            </div>

            <div class="field clearfix">
                <label for="password">Password</label>
                <input type="password" id="password" name="password"
                       value="<%=(action.equals("modify")) ? user.getPassword() : ""%>"
                       required size="20" maxlength="40"/>
            </div>

            <div class="field clearfix">
                <label for="firstname">Nome</label>
                <input type="text" id="firstname" name="firstname"
                       value="<%=(action.equals("modify")) ? user.getFirstname() : ""%>"
                       required size="20" maxlength="50"/>
            </div>

            <div class="field clearfix">
                <label for="surname">Cognome</label>
                <input type="text" id="surname" name="surname"
                       value="<%=(action.equals("modify")) ? user.getSurname() : ""%>"
                       required size="20" maxlength="50"/>
            </div>

            <div class="field clearfix">
                <label for="email">Email</label>
                <input type="email" id="email" name="email"
                       value="<%=(action.equals("modify")) ? user.getEmail() : ""%>"
                       required size="20" maxlength="100"/>
            </div>

            <div class="field clearfix">
                <label for="birthDate">Data di Nascita</label>
                <input type="date" id="birthDate" name="birthDate"
                       value="<%=(action.equals("modify")) ? user.getBirthDate() : ""%>"
                       required />
            </div>

            <div class="field clearfix">
                <label for="image">Immagine del profilo</label>
                <section class="buttonContainer">
                    <input type="button" name="uploadImageButton" id="uploadImageButton" class="button blue" value="Scegli Foto"/>
                    <input type="button" name="deleteImageButton" id="deleteImageButton" class="button red" value="Reimposta"/>
                    <input type="file" id="image" name="image" accept="image/png" class="invisible"/>
                </section>
                <img id="preview" src="<%=(loggedUser != null) ? loggedUser.getProfilePicPath() : FileSystemService.DEFAULT_PROFILE_PIC_PATH%>" alt="Immagine profilo">
            </div>

            <div class="buttonContainer large">
                <input type="submit" name="submitUserButton" id="submitUserButton" class="button blue" value="Invia"/>
                <input type="button" name="backButton" id="backButton" class="button red" value="Annulla"/>
            </div>

            <input type="hidden" id="registrationTimestamp" name="registrationTimestamp" value=""/>
            <input type="hidden" id="role" name="role" value="<%=(action.equals("modify")) ? user.getRole() : "User"%>"/>
            <input type="hidden" id="deleted" name="deleted" value="<%=(action.equals("modify")) ? user.getDeleted() : "N"%>"/>
            <input type="hidden" name="deleteImage" id="deleteImage" value="false"/>
            <input type="hidden" name="updateImage" id="updateImage" value="false"/>

            <input type="hidden" name="controllerAction"/>
        </form>
    </section>

    <form name="backForm" method="post" action="Dispatcher">
        <input type="hidden" name="controllerAction" value="UserManagement.view">
    </form>

</main>
<%@include file="/include/footer.inc"%>
</body>
</html>
