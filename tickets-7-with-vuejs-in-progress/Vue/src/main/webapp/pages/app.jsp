<?xml version='1.0' encoding='ISO-8859-1' ?>
<!DOCTYPE html>
<html lang="en">

<head>

    <title>Vues</title>
    
    <link rel="shortcut icon" href="../favicon.png" />

    <!-- europe charset -->
    <meta charset="ISO-8859-1" />
    
    <!--  for best indexation -->
    <meta name="identifier-url" content="http://tfa.onmypc.net/" />
    <meta name="title" content="The French Author Site" />
    <meta name="description" content="The French Author Site" />
    <meta name="abstract" content="The French Author Site" />
    <meta name="keywords" content="java, webapp, jsf, jaas" />
    <meta name="author" content="The French Author" />
    <meta name="revisit-after" content="15" />
    <meta name="language" content="EN" />
    <meta name="copyright" content="© 2016 tfa" />
    <meta name="robots" content="All" />

    <!-- webjars resources -->
    <%@ include file="../resources/webjars.html" %>  
    
    <!-- Some common app css -->
    <link rel="stylesheet" href="../resources/css/theme.css" />  
    <link rel="stylesheet" href="../resources/css/layout.css" />  
    <link rel="stylesheet" href="../resources/css/menu.css" />  

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


