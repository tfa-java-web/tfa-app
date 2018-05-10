<script> 

    console.log("reloaded");
    
    // Page contents
  	const mainRouter = new VueRouter({ routes :  
  	    [
  	       { path: '/', redirect : '/home' },
           { path: '/home', name : "Home", component: uiHome },
           { path: '/edit', name : "Edit", component: uiEdit },
           { path: '/help', name : "Help", component: uiHelp },
           { path: '/todo', name : "Todo", component: uiTodo },
           { path: '/copyright', name : "Copyright", component: uiCopyright }
        ]
    });

    // Prerequisities : get status list from server
    getStatus( function(data) 
    {   	
    	statusList = data;

    	// build single page
        app = new Vue(
        { 
            el : '#app',
            
            router : mainRouter,
            
            data : { 
                title : document.title, 
                current : dummy 
            },
            
            mounted : function()
            { 
            }
        }); 
    	   	
        // Set the current ticket selected for sons (home-detail, edit)
        app.$on("current", function(what) { 
            this.current = what;
        });  
        
    });
    
//# sourceURL=http://localhost:8080/vues/pages/app.vue
</script>
   
<div  id="app">
         
    <!-- Hide it at vuejs begins.. -->
    <div v-if="false" id="loading" style="padding: 30px;">
        Tickets management : loading...
    </div>

    <!-- LAYOUT of Single Page -->

    <ui-header></ui-header>

    <div id="wrapper">

        <ui-aside></ui-aside>

        <section id="main">
            <keep-alive>        
                <router-view :current="current"></router-view>
            </keep-alive>     
        </section>

    </div>

    <ui-footer></ui-footer>
    
</div>
