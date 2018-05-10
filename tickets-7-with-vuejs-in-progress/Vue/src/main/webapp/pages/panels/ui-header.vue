<script>

   Vue.component( "ui-header", 
   { 
       template : "#ui-header",
       
       data : function()
       {
    	   // default logged user
    	   return { username : null }
       },
       mounted : function() 
       {
    	   // get logged user from server
           var self = this;
           getAuth( function(data) { self.username = data; });
       },
       methods : 
       {
    	   submit : function()
    	   {
               // Create a new ticket by cloning dummy before routing to Submit 
               setCurrent( JSON.parse(JSON.stringify(dummy)) );    		   
    	   }
       }   
   });
   

//# sourceURL=http://localhost:8080/vues/pages/panels/ui-header.vue
</script>

<template id="ui-header" >

   <header id="header">

      <div id="headband">
      
         <!-- main site logo and name -->
         <i id="logoinfo" class="fa fa-bars">
           <a href="http://tfa.onmypc.net/"> tfa.onmypc.net</a>
         </i> 
         
         <!-- header right part : authentication infos -->
         <span id="logininfo" style="float: right;"> 
  
               <a href="home">{{username}}</a>
               
               <a style="margin-left:10px">
                  <i class="fa fa-lg fa-power-off" title="Logout"></i>
               </a>
               
               <!-- <a style="margin-left:10px" href="../rest/cas/logout" > 
                  <i class="fa fa-lg fa-power-off" title="Logout"></i>
               </a>  -->
               
         </span>
         
      </div>

      <!-- pulldown horizontal menubar under headband -->
      <nav class="menu" id="menubar">
         <ul>
            <li>
                <router-link to="edit" @click.native="submit"><i class="fa fa-pencil"></i> Submit</router-link>
            </li>
            <li>
                <a href="../demo/splitted.html"><i class="fa fa-sitemap"></i> Splitters</a>
            </li>
            <li>
                <a href="../demo/tabbed.html"><i class="fa fa-clone"></i> Tabs</a>
            </li>
            <li>
                <router-link to="todo"><i class="fa fa-users"></i> User</router-link>
            </li>
            <li style="display: none">
               <a href="../admin/manager.html"><i class="fa fa-cog"></i> Settings</a>
            </li>
            <li style="float: right;">
                <router-link to="help"><i class="fa fa-question"></i> Help</router-link>
            </li>
         </ul>
      </nav>

   </header>

</template>

