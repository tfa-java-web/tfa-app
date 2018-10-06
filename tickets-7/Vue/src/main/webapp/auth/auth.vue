<script> 
    
    // Single Page contents
    var authRouter = new VueRouter({ routes :  
        [
           { path: '/', redirect : 'login' },
           { path: '/login', name : "Login", component: uiLogin },
           { path: '/logout', name : "Logout", component: uiLogout },
           { path: '/logerr', name : "Logerr", component: uiLogerr },
           { path: '/forgot', name : "Forgotten", component: uiForgotten },
           { path: '/register', name : "Register", component: uiRegister }
        ]
    });

    $().ready( function() 
    {
      var auth = new Vue(
        { 
            el : '#auth',
            router : authRouter,
        }); 
    });    
    
//# sourceURL=http://localhost:8080/vues/auth/auth.vue
</script>
   
<div id="auth" class="full">
         
    <!-- Simple 100% view LAYOUT -->

    <div id="headband">
        <i id="logoinfo" class="fa fa-bars"><a href="http://tfa.onmypc.net/"> tfa.onmypc.net</a></i>
    </div>

    <div style="background-color: #eee; height: calc(100% - 46px)">
                      
        <!-- boxes to login/logout -->
        <router-view></router-view>
   
    </div>

    <a id="foottext" href="#">Version 1.0 - March 2016,2017 @ The French Author</a>
    
</div>
