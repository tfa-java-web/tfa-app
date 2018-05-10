<script>

   var uiHome = Vue.component( "ui-home", 
   { 
       template : "#ui-home",
       
       props : [ "current" ]
   
   });
      
//# sourceURL=http://localhost:8080/vues/pages/panels/ui-home.vue
</script>

<style>
</style>

<template id="ui-home">
  
   <div>
        <div id="tf" class="full_h">
            <ui-list ref="list"></ui-list>
            <ui-detail :current="current"></ui-detail>
        </div>
        
    </div>
</template>