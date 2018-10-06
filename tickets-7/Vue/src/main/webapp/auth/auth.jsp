<?xml version='1.0' encoding='ISO-8859-1' ?>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:h="http://java.sun.com/jsf/html">

<!-- template of Authentication pages -->

<head>

    <title>Tickets</title>

    <meta charset="ISO-8859-1" />
  
    <%@ include file="../resources/webjars.html" %>  
    
    <link rel="stylesheet" href="../resources/css/theme.css" />  
    <link rel="stylesheet" href="../resources/css/layout.css" />   

    <script src="../resources/js/tools.js"></script>
    <script src="../resources/js/restclient.js"></script>
    <script src="../resources/js/commons.js"></script>
      
</head>

<body>

    <div style="display:none">
       <%@ include file="../pages/panels/ui-dialog.vue" %>  
       <%@ include file="ui-login.vue" %>  
       <%@ include file="ui-logout.vue" %>  
       <%@ include file="ui-logerr.vue" %>  
       <%@ include file="ui-register.vue" %>  
       <%@ include file="ui-forgotten.vue" %>  
    </div>

    <%@ include file="auth.vue" %>  
      
</body>

</html>

