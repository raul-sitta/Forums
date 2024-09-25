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
      <li <%=menuActiveLink.equals("Logon")?"class=\"active\"":""%>>
        <a href="Dispatcher?controllerAction=HomeManagement.logonView">Logon</a>
      </li>
      <%}%>
      <%if (loggedOn) {%>
        <li <%=menuActiveLink.equals("Account")?"class=\"active\"":""%>>
            <a href="Dispatcher?controllerAction=UserManagement.view">Account</a>
        </li>
        <li <%=menuActiveLink.equals("Topics")?"class=\"active\"":""%>>
            <a href="Dispatcher?controllerAction=TopicManagement.view">Topics</a>
        </li>
        <li <%=menuActiveLink.equals("FAQ")?"class=\"active\"":""%>>
            <a href="Dispatcher?controllerAction=FAQManagement.view">FAQ</a>
        </li>
        <li><a href="javascript:logoutForm.submit()">Logout</a></li>
      <%}%>
    </ul>
  </nav>

</header>