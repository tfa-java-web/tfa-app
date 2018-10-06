<script>

   var uiEdit = Vue.component( "ui-edit", 
   { 
       template : "#ui-edit", 
       
       props : [ "current" ],
       
       data: function () { return {
            statusList : statusList,
    	    status : "",
    	    user : ""
       }},
       methods :
       {
   	       validate : function()
    	   {
   	    	  this.current.date = new Date();
   	    	  if ( this.current.status == null ) this.current.status = statusList[0].id;
              if ( this.current.user == null ) this.current.user = "";
   	    	  var id = this.current.id;
   	    	  if( id == null )
   	    	  {
   	    	      id = postTickets( this.current, null );
   	    	  }
   	    	  else
   	          {
   	    		  putTickets( this.current, null );
   	          }
   	    	  app.$router.push("home");
    	   }
       }    
   });

   
//# sourceURL=http://localhost:8080/vues/pages/panels/ui-edit.vue
</script>

<style scoped>

#editForm {
    padding: 20px;
}

#editForm>div {
    margin-bottom: 20px;
}

#editForm>div>div {
    font-weight: bold;
    margin-bottom: 3px;
}

#editForm>div>div>span {
    padding-left: 40px;
}

#desc {
    width: calc(100% - 12px);
    max-width: calc(100% - 12px);
    max-height: 550px;
    margin-bottom: 20px;
}

#title {
    width: calc(100% - 8px);
    margin-bottom: 20px;
}

</style>

<template  id="ui-edit" >

   <form id="editForm">
      <div>
          <div>Title : </div>
          <input id="title" v-model="current.title" required="required" label="Title"/>
          <div>Description : </div>
          <textarea id="desc" rows="10" v-model="current.desc" required="required" label="Description"></textarea>
          <div>
              Assign to : <input v-model="current.user" type="text">
              <span></span>
              Status : <select size="1" v-model="current.status"> 
                          <option v-for="s in statusList" :value="s.id" :key="s.id">
                            {{s.name}}
                          </option>
                       </select> 
          </div>
      </div>
   
      <!-- bar of buttons -->
      <div class="dialFooter">         
          <hr />
          <table class="buttons" columns="2">
          <tbody><tr>
                <td><button @click="validate" class="button-ok">{{current.id == null ? 'Create':'Update'}}</button></td>
                <td><router-link to="home" tag="button" class="button-ko">Cancel</router-link></td>
          </tr></tbody>
          </table>
      </div>
   </form>

</template>

