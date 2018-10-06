<script>

   Vue.component( "ui-dialog",
   { 
       template : "#ui-dialog",

       props : { 
    	   title : 
    	   {
    		   required: true,   
               type: String            
    	   }, 
    	   validate :
    	   {
    		   required: false,
    		   type: String,
    		   default: "Validate"   		   
    	   }, 
    	   close :
    	   {
               required: false,
               type: String,
               default: "Cancel"             
    	   },
    	   cancel :
    	   {
               required: false,
               type: String,
               default: null             
    	   },
    	   action :
    	   {
               required: false,
               type: Function,
               default: null             
    	   }
       },
       methods :
       {
    	   hide : function() 
    	   {
    		   this.$emit( "hide" );
    	   }
       }
   });

//# sourceURL=http://localhost:8080/vues/pages/panels/ui-dialog.vue
</script>

<template id="ui-dialog" >
   
   <!--  vue js soft transition effect-->
   <transition name="modal">
   
      <!-- div for transparent grayed background , not shown by default -->
      <div class="modalDialog">
   
         <div>
   
            <!-- title bar with simple closing button -->
            <div class="dialTitle">
               <span>{{title}}</span> 
               <button @click="hide">X</button>
            </div>
   
            <!-- Dialog content (for render) -->
            <div class="dialContent">
   
               <!-- Specific content -->
               <slot></slot>
   
           </div>
   
            <!-- Button bar with up to 3 buttons maximum: Validate, Cancel, Close -->
            <div class="dialFooter">
               <hr />
               <table class="buttons" columns="3">
               <tbody><tr>
                  <td><button v-show="validate" class="button-ok" @click="action(true)">{{validate}}</button></td>
                  <td><button v-show="cancel" class="button-ko" @click="action(false)" >{{cancel}}</button></td>
                  <td><button v-show="close" type="reset" @click="hide">{{close}}</button></td>
               </tr></tbody>
               </table>
            </div>
   
         </div>
   
      </div>
      
   </transition>
   
</template>

<style scoped>

/* dark transparent layer on entire page */
.modalDialog {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.7);
    z-index: 101;
    transition: opacity .1s ease;
    /* display: none; */
}

/* center dialog on page */
.modalDialog > div {
    /* center dialog */
    width: 40%;
    padding: 6px;
    margin: auto;
    margin-top: 10%;
    background-color: white;
    border-radius: 5px;
    /* resizable */
    resize: both;
    overflow: auto;
    min-width: 200px;
    min-height: 100px;
    max-width: 1024px;
    max-height: 900px;
    /* content layout */
    display: flex;
    flex-direction: column;
}

/* X-close button of dialog box */
.dialTitle button {
    float: right;
    vertical-align: top;
}

/* content of dialog : centered */
.dialContent {
    margin: auto;
}

.dialFooter {
    margin-top: auto;
}

/* for vue.js transition of modalDialog */

.modal-enter {
  opacity: 0;
}

.modal-leave-active {
  opacity: 0;
}

</style>