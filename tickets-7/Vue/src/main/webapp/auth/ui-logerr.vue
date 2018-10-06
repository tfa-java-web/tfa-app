<script>

   var uiLogerr = Vue.component( "ui-logerr", 
   { 
       template : "#ui-logerr", 
       methods : 
       {          
           logerrAction : function(valid) 
           {
               this.$parent.$router.push( valid ? '/logout' : '/login' );
           }
       }
   });

//# sourceURL=http://localhost:8080/vues/auth/ui-logerr.vue
</script>

<template id="ui-logerr" >

     <ui-dialog title="Authentication" cancel="Try again" validate="" close="" :action="logerrAction" @hide="logerrAction(true)" >

         <h2 style="color: red">Authentication failed !</h2>
         
    </ui-dialog>
  
</template>
