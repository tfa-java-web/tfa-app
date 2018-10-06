<?xml version='1.0' encoding='ISO-8859-1' ?>
<!DOCTYPE html>
<html lang="en">

<head>

    <title>Vues</title>
    
    <!-- europe charset -->
    <meta charset="ISO-8859-1" />
  
    <!-- webjars resources -->
    <%@ include file="../resources/webjars.html" %>  
    
    <!-- Some common app css -->
    <link rel="stylesheet" href="../resources/css/theme.css" />  
    <link rel="stylesheet" href="../resources/css/menu.css" />  
    <link rel="stylesheet" href="../resources/css/layout.css" />   

    <!-- Some common app js -->
    <script src="../resources/js/tools.js"></script>
    <script src="../resources/js/restclient.js"></script>
    <script src="../resources/js/commons.js"></script>
    <script src="../resources/js/paginate.js"></script>
    
</head>

<body>

    <!-- webpack alternative : jsp include -->
    <!-- plus display none for old browser -->
    <div style="display:none">
       <%@ include file="panels/ui-dialog.vue" %>  
       <%@ include file="panels/ui-header.vue" %>  
       <%@ include file="panels/ui-aside.vue" %>  
       <%@ include file="panels/ui-footer.vue" %>  
       <%@ include file="panels/ui-home.vue" %>  
       <%@ include file="panels/ui-list.vue" %>  
       <%@ include file="panels/ui-detail.vue" %>  
       <%@ include file="panels/ui-edit.vue" %>  
       <%@ include file="panels/ui-help.vue" %>  
       <%@ include file="panels/ui-todo.vue" %>  
       <%@ include file="panels/ui-copyright.vue" %>  
    </div>

    <!-- single page template / layout -->
    <%@ include file="app.vue" %>  
    
</body>

</html>


