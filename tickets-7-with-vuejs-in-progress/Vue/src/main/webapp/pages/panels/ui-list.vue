<script>

	Vue.component("ui-list",
	{
		template : "#ui-list",      // an html table of ticket page

		mixins: [ paginate, renderTicket ],       // inheritance from paginate data
		
		data : function()
		{
			return {
			    tickets : null,        // displayed ticket's page
			    selection : [],      // ids of selected tickets				 
				filter : null,       // current filter criteria
                groupDialog : false,  // dialog hidden
                groupOption : "Unselect"
			}
		},

		mounted : function()
		{	
			if ( this.tickets == null )
			    this.init();
		},
		
		methods :
		{
            // Override : Get data for current page with sort , return count read
            getDataPage : function(cb) 
            {
                // get tickets from server by page and page size
                var self = this;
                getTickets( this.filter, this.order, this.page, pageSize, function(data) 
                {	
                      var size = data.length;
                                          
                      // Diff get size from displayed size
                      var nb = pageSize - size;
                      if (nb > 0)
                      {
                         // Not enough : complete with empty data
                         var filled = new Array(nb).fill(dummy);
                         data = data.concat(filled);
                      }
                      // The data displayed
                      self.tickets = data;
                             
                      // significant page size
                      if ( cb ) cb(data,size);
                });
            },
            
            // Override : Get total data count
            getDataCount : function(cb) 
            {
                // get total tickets count from server
                getTicketsCount( function(data)
                {
                	if (cb) cb(data);
                });
            },  
            
            // Override : Put as current the data at index in current page
            setAsCurrent : function(idx) {
            	
                  // set as current in detail panel
                  this.setCurrent( (idx < 0 || idx >= pageSize) ? null : this.tickets[idx] );            	
            },
 
            setSelected : function(t,ev)
            {
                // empty is not selectable
                if ( t == dummy ) 
                    return;
                
                var set = ev.target.checked;
                
                // set as selected (or invert) into selection list
                if (set)
            	    this.selection.push(t);
                else
                {
                    for( var idx = 0; idx < this.selection.length; idx++ )
                        if ( this.selection[idx].id === t.id )
                            return this.selection.splice(idx, 1);
                }
            },

            isSelected : function(t)
            {
              // is selected
              for( var idx = 0; idx < this.selection.length; idx++ )
            	  if ( this.selection[idx].id === t.id )
            		  return true;
              return false;
            },
            			
			rowClick : function(idx)
			{
                // ticket under mouse click
                this.index = idx;
                
                // set as current in detail panel
                this.setCurrent( this.tickets[idx] );
			},
			
			groupAction : function(valid)
			{
				// Valid or cancelled 
				if ( valid )
				{
					// for each ticket selected
					// for ( var t : this.selection )
					{
					      // do the action on each of selected item
					      if (this.groupOption === "Close")
					      {
					      }
					      else if (this.groupOption === "Assign")
					      {
					      }
					      else if (this.groupOption === "Remove")
					      {
					      }
					      else // unselect all
					          this.selection = [ ];
					}				
				}
				// hide dialog
				this.groupDialog = false;	
				this.groupOption = "Unselect";
			}
		}
	});

//# sourceURL=http://localhost:8080/vues/pages/panels/ui-list.vue
</script>


<template id="ui-list">

<div id="list" class="list">

   <!-- table, with rowClick -->
   <table id="tickets" class="mytable">

      <thead>
         <tr class="rowClass">
            <th>
               <!-- header button, sort on id --> <i class="fa fa-hashtag" @click="sort('id')"></i> <!-- header button, action on selected group -->
               <i style="padding-left: 5px; padding-right: 5px; color: #ff00ff;" class="fa fa-ellipsis-v"
               @click="groupDialog = true"></i>
            </th>

            <th><i class="fa fa-tag" @click="sort('status')"></i></th>

            <th><i class="fa fa-user" @click="sort('user')"></i></th>

            <th><i class="fa fa-calendar" @click="sort('date')"></i></th>

            <th>
               <!-- the I of Information is the icon --> <span style="float: left"> <i class="fa fa-info"></i> nformation
            </span> <!-- buttons next/prev page  at the right --> <span style="float: right; font-weight: normal"> <i
                  class="fa fa-toggle-left" @click="prevPage"></i> &#160; {{page+1}} / {{nbPage}} &#160; <i
                  class="fa fa-toggle-right" @click="nextPage"></i> &#160; <!-- &#160 are insecable white space (margin) -->
            </span>
            </th>
         </tr>
      </thead>

      <tbody>
         <tr class="rowClass" @click="rowClick(idx)" v-for="(o,idx) in tickets" :key="o.id">

            <!-- id of ticket, with sort, action on selected group -->
            <td>
               <!-- id of tickets , associated to invisible toggles, to memorize selected state --> 
               <input type="checkbox" v-if="o.id != null"  :id="'tg-'+o.id" style="display: none;" :checked="isSelected(o)" @change="setSelected(o,$event)"/> 
               <label style="text-align: right;" :for="'tg-'+o.id">{{o.id}}</label>
            </td>

            <!-- status of ticket, with sort -->
            <td>
               <!-- display status string , with style for coloration --> 
               <span v-bind:style="{ backgroundColor : statusColor(o.status) }" v-if="o.status != null" class="label">{{statusText(o.status)}}</span>
               <span v-else>&#160;</span>
            </td>

            <!-- assigned person, with sort -->
            <td>{{o.user}}</td>

            <!-- last modification date of ticket, with sort -->
            <td v-if="o.date != null">{{formatDate(o.date)}}</td>
            <td v-else>&#160;</td>

            <!-- title , with grey truncated description -->
            <td>{{o.title}} <span class="estompe"> {{shortDesc(o.desc)}}</span></td>
         </tr>

      </tbody>

      <!-- botton of table : not used , pagination is managed at the head -->
      <tfoot name="footer">
         <tr class="rowClass"></tr>
      </tfoot>

   </table>

   <!-- dialog : actions on tickets' group selected -->
   <ui-dialog v-show="groupDialog" id="group" title="Action on tickets selection" cancel="Cancel" close="" :action="groupAction" @hide="groupDialog = false" >

   <p>{{selection.length}} selected tickets</p>

   <div style="margin: 10px">
   
       <select v-model="groupOption">                 
           <option>Unselect</option>
           <option>Close</option>
           <option>Assign</option>
           <option>Remove</option>
       </select> 
                                      
       <input id="user" value="username" style="margin-left: 10px" />
       
   </div>

   </ui-dialog>

</div>

</template>

<style scoped>
.mytable {
    width: 100%;
    height: 100%;
    table-layout: fixed;
    border-collapse: collapse;
    user-select: none;
}

.mytable th, td {
    text-align: left;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.label {
    display: inline;
    padding: .1em .5em .1em .4em;
    font-size: 76%;
    line-height: 1;
    background-color: lightgrey;
    text-align: center;
    white-space: nowrap;
    vertical-align: .1em;
    border-radius: .5em;
}

.list {
    padding: 5px;
}

.mytable {
    cursor: default;
}

.mytable thead {
    border-bottom: 1px solid #ccc;
}

.mytable tbody tr:hover {
    background-color: #eeeeff;
}

.mytable tr:nth-child(even) {
    background-color: #f8f8f8;
}

.mytable th:nth-of-type(1) {
    width: 46px;
    height: 23px;
}

.mytable th:nth-of-type(2) {
    width: 65px;
}

.mytable th:nth-of-type(3) {
    width: 85px;
}

.mytable th:nth-of-type(4) {
    width: 60px;
}

.mytable th:nth-of-type(5) {
    width: auto;
}

.mytable input:checked+label {
    color: #ff00ff;
}

.estompe {
    color: #bbbbbb;
}
</style>