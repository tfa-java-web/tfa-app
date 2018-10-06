<script>

   var uiLogout = Vue.component( "ui-logout", 
   { 
       template : "#ui-logout" 
   });

//# sourceURL=http://localhost:8080/vues/auth/ui-logout.vue
</script>

<template id="ui-logout" >
   
      <div style="padding: 30px">

         <div class="centerPanel" style="text-align: center">

             <h2 style="color: red">logged out !</h2>
             <br />
             <h5>Bye bye...</h5>
             <br />
             <hr />
             <!-- Button to return to login page, or to go out ... to duckduck go -->
             <table class="buttons"><tbody>
                 <tr><td><router-link to="/login">
                    <button>Log in</button>
                 </router-link></td>
                 <td><a href="https://duckduckgo.com">
                    <img src="../resources/img/duckduckgo.png" />
                 </a></td></tr>
             </tbody></table>
         </div>
     </div>

</template>
