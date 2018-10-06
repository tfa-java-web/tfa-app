<script>

   var uiForgotten = Vue.component( "ui-forgotten", 
   { 
       template : "#ui-forgotten" ,
       methods : 
       {
    	   forgotAction : function(valid)
    	   {
               this.$parent.$router.push( valid ? '/login' : '/logout' );
    	   }
       }
   });

//# sourceURL=http://localhost:8080/vues/auth/ui-forgotten.vue
</script>

<template id="ui-forgotten" >
  
     <ui-dialog title="Authentication" validate="Validate" cancel="Cancel" close="" :action="forgotAction" @hide="forgotAction(false)" >
     
          <!-- TODO : management of login/password forgotten by any user -->
          <h2>Forgotten password : Send back To do !</h2>
          <br />
          <p>
              For now, you can use <b>guest / guest</b> account and password.
          </p>
          <br />
     </ui-dialog>
  
</template>
