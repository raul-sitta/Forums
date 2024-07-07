<script>
  function headerOnLoadHandler() {
    var usernameTextField = document.querySelector("#usernameLogin");
    var usernameTextFieldMsg = "Lo username \xE8 obbligatorio.";
    var passwordTextField = document.querySelector("#passwordLogin");
    var passwordTextFieldMsg = "La password \xE8 obbligatoria.";

    if (usernameTextField != undefined && passwordTextField != undefined ) {
      usernameTextField.setCustomValidity(usernameTextFieldMsg);
      usernameTextField.addEventListener("change", function () {
        this.setCustomValidity(this.validity.valueMissing ? usernameTextFieldMsg : "");
      });
      passwordTextField.setCustomValidity(passwordTextFieldMsg);
      passwordTextField.addEventListener("change", function () {
       this.setCustomValidity(this.validity.valueMissing ? passwordTextFieldMsg : "");
      });
    }
  }
</script>

<header class="clearfix"><!-- Defining the header section of the page -->

  <h1 class="logo"><!-- Defining the logo element -->
    Forums
  </h1>

  <form name="logoutForm" action="Dispatcher" method="post">
    <input type="hidden" name="controllerAction" value="HomeManagement.logout"/>
  </form>

  <nav><!-- Defining the navigation menu -->
    <ul>
      <li <%=menuActiveLink.equals("Home")?"class=\"active\"":""%>>
        <a href="Dispatcher?controllerAction=HomeManagement.view">Home</a>
      </li>
      <%if (!loggedOn) {%>
      <li <%=menuActiveLink.equals("Registrati")?"class=\"active\"":""%>>
        <a href="Dispatcher?controllerAction=UserManagement.insertView">Registrati</a>
      </li>
      <%}%>
      <%if (loggedOn) {%>
        <li <%=menuActiveLink.equals("Account")?"class=\"active\"":""%>>
          <a href="Dispatcher?controllerAction=UserManagement.view">Account</a>
        </li>
        <li><a href="javascript:logoutForm.submit()">Logout</a></li>
      <%}%>
    </ul>
  </nav>

  <%if (!loggedOn) {%>
    <section id="login" class="clearfix">
      <form name="logonForm" action="Dispatcher" method="post">
        <label for="usernameLogin">Utente</label>
        <input type="text" id="usernameLogin"  name="username" maxlength="40" required>
        <label for="passwordLogin">Password</label>
        <input type="password" id="passwordLogin" name="password" maxlength="40" required>
        <input type="hidden" name="controllerAction" value="HomeManagement.logon"/>
        <input type="submit" value="Ok">
      </form>
    </section>
  <%}%>

</header>