<script>

   Vue.component( "ui-detail", 
   { 
       template : "#ui-detail", 
       
       mixins: [ renderTicket ],   // inheritance 

       props : [ "current" ],
       
       data : function() { return {
    	   statusList : statusList,
           removeDialog : false    // not shown
       }},
       methods : {   	   
    	   prev : function()
    	   {
    		   // throws to parent-child : "list" 
    		   this.$parent.$refs.list.prev();
    	   },
    	   next : function()
    	   {
    		   // throws to parent-child : "list"
               this.$parent.$refs.list.next();
    	   }
       }
   });
 
//# sourceURL=http://localhost:8080/vues/pages/panels/ui-detail.vue
</script>

<template id="ui-detail" >

    <div id="detail" class="detail">

        <!-- title rounded rectangle -->
        <div id="rtitle" class="rtitle">

            <!--  prev - next buttons at right -->
            <span style="float: right; user-select:none"> 
                    <i class="fa fa-toggle-left" @click="prev">&#160;</i>

                    <i class="fa fa-toggle-right" @click="next">&#160;</i>
            </span>

            <!-- id + title -->
            <span v-if="current">
                <span id="leid" class="leid" v-if="current.id"> {{current.id}} </span>
                <span id="letitre" class="letitre" v-if="current.id"> {{current.title}} </span>
            </span>

        </div>

        <!-- content of not dummy ticket -->
        <div v-if="current && (current.id != null)">

            <!-- buttons bar -->
            <nav id="actions" class="actions menu">
                <ul>
                    <li>
                        <span>Assign to : {{current.user}}</span> 
                    </li>
                    <li>
                        <span>Status : <select size="1" v-model="current.status"> 
                          <option v-for="s in statusList" :value="s.id" :key="s.id">
                            {{s.name}}
                          </option>
                        </select> </span> 
                    </li>
                    <li>
                        <span>Date :</span> <span>{{formatDate(current.date,true)}}</span>
                    </li>
                    <li>
                        <router-link tag="button" to="edit">Modify</router-link>
                    </li>
                    <li>
                        <button @click="removeDialog = true">Remove</button>
                     </li>
                </ul>
            </nav>

            <!-- description panel , scrollable -->
            <div class="scroll">

                <div>
                    <!-- html (no escape) content -->
                    <pre v-html="current.desc"></pre>
                </div>

            </div>

        </div>
        
        <!-- 2nd dialog : warning before removing of current ticket -->
        <ui-dialog v-if="current" v-show="removeDialog" id="remove" title="Removal" close="Cancel" @hide="removeDialog = false">

            <p>Do you really want to remove the ticket {{current.id}} ?</p>
            <br />
            <p>{{current.title}}</p>
            <br />

        </ui-dialog>
        
    </div>
    
</template>

<style scoped>

.rtitle {
    width: calc(100% - 15px);
}

.leid {
    vertical-align: top;
    margin-right: 8px;
}

.letitre {
    display: inline-block;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    width: calc(100% - 106px);
}

.actions {
    float: right;
    margin-right: 5px;
    margin-top: 8px;
    margin-bottom: 5px;
}

.tabbed {
    float: right;
    margin-top: 8px
}

.scroll {
    clear: both;
    overflow-y: scroll;
    height: calc(100% - 60px);
}

.scroll pre {
    width: 99%;
    line-height: 1.4em;
}

</style>
