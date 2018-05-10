<script>

   var uiHelp = Vue.component( "ui-help", 
   { 
       template : "#ui-help" 
   });
   
//# sourceURL=http://localhost:8080/vues/pages/panels/ui-help.vue
</script>

<!-- style to apply only in that div (scoped) ! (firefox only) -->
<style scoped ="scoped">
   #restApi th, #restApi td {
       padding: 0px 15px 0px 15px;
   }
   
   #baseUrl, #restApi a {
       color: blue;
   }
</style>

<template id="ui-help">

     <div class="simple" style="background-color: #ffe">

         <h1>REST API</h1>

         <br />
         <p>
             Base URL of Tickets's application REST API : <i id="baseUrl">http:// hostname[:port] / tickets / rest</i> ...
         </p>
         <br />
         <table id="restApi">
             <thead style="text-align: left">
                 <tr>
                     <th>Path</th>
                     <th>Verb</th>
                     <th>Return</th>
                     <th>Comment</th>
                 </tr>
             </thead>
             <tbody>
                 <tr>
                     <td><a href="../rest/auth">/auth</a></td>
                     <td>GET</td>
                     <td>plaintext</td>
                     <td>name of logged user or "not logged"</td>
                 </tr>
                 <tr>
                     <td><a href="#">/auth</a></td>
                     <td>POST</td>
                     <td>plaintext</td>
                     <td>login a user with "name:token"</td>
                 </tr>
                 <tr>
                     <td></td>
                     <td>Param:</td>
                     <td colspan="2"><b>plaintext</b>=username:token (token is crypted password:salt)</td>
                 </tr>
                 <tr>
                     <td><a href="#">/auth</a></td>
                     <td>DELETE</td>
                     <td>plaintext</td>
                     <td>logout the user</td>
                 </tr>
                 <tr>
                     <td><a href="../rest/hitCount">/hitCount</a></td>
                     <td>GET</td>
                     <td>plaintext</td>
                     <td>total number of sessions on application</td>
                 </tr>
                 <tr>
                     <td><a href="../rest/health">/health</a></td>
                     <td>GET</td>
                     <td>plaintext</td>
                     <td>a resume of health of application</td>
                 </tr>
                 <tr>
                     <td><a href="../rest/status">/status</a></td>
                     <td>GET</td>
                     <td>json</td>
                     <td>list of status string</td>
                 </tr>
                 <tr>
                     <td><a href="../rest/users">/users</a></td>
                     <td>GET</td>
                     <td>json</td>
                     <td>list of user-names</td>
                 </tr>
                 <tr>
                     <td><a href="#">/tickets</a></td>
                     <td>POST</td>
                     <td>plaintext</td>
                     <td>create a new ticket, return new id, consume json ticket data, with no id</td>
                 </tr>
                 <tr>
                     <td><a href="../rest/tickets/1">/tickets/1</a></td>
                     <td>GET</td>
                     <td>json</td>
                     <td>read a ticket with this id</td>
                 </tr>
                 <tr>
                     <td><a href="#">/tickets</a></td>
                     <td>PUT</td>
                     <td>plaintext</td>
                     <td>update an existing ticket, consume json ticket data, with existing id</td>
                 </tr>
                 <tr>
                     <td><a href="#">/tickets/456</a></td>
                     <td>DELETE</td>
                     <td>plaintext</td>
                     <td>delete the ticket with this id</td>
                 </tr>
                 <tr>
                     <td><a href="../rest/tickets?size=5">/tickets</a></td>
                     <td>GET</td>
                     <td>json</td>
                     <td>list of tickets (with optional filtering and order)</td>
                 </tr>
                 <tr>
                     <td></td>
                     <td>Param:</td>
                     <td colspan="2"><b>id</b>=123 or id=100,200 to select range</td>
                 </tr>
                 <tr>
                     <td></td>
                     <td>Param:</td>
                     <td colspan="2"><b>title</b>=The to select Title beginning with 'The', <b>desc</b>=Some for Description
                         beginning with 'Some'</td>
                 </tr>
                 <tr>
                     <td></td>
                     <td>Param:</td>
                     <td colspan="2"><b>status</b>=suspended or status=in-progress,close to select range of status</td>
                 </tr>
                 <tr>
                     <td></td>
                     <td>Param:</td>
                     <td colspan="2"><b>user</b>=guest to select only tickets assigned to user name</td>
                 </tr>
                 <tr>
                     <td></td>
                     <td>Param:</td>
                     <td colspan="2"><b>date</b>=2016-12-25 or date=2016-12-01,2016-12-31 to select date range</td>
                 </tr>
                 <tr>
                     <td></td>
                     <td>Param:</td>
                     <td colspan="2"><b>order</b>=date-,id to ordering result by date descendant, then by id ascendant</td>
                 </tr>
                 <tr>
                     <td></td>
                     <td>Param:</td>
                     <td colspan="2"><b>page</b>=1&amp;<b>size</b>=5 to select page 1 (1-based), with page size of 5</td>
                 </tr>
                 <tr>
                     <td><a href="../rest/tickets/count">/tickets/count</a></td>
                     <td>GET</td>
                     <td>json</td>
                     <td>total count of tickets (with optional filtering)</td>
                 </tr>
                 <tr>
                     <td></td>
                     <td>Param:</td>
                     <td colspan="2">The same parameters as list, excluding order, page and size</td>
                 </tr>
             </tbody>
         </table>
         <div style="padding: 20px">
                <p>
                    <b>JSON ticket sample :</b>
                </p>
<pre>{
"id":3,
"title":"The title",
"desc":"Description...3x\r\njlujluigl\r\nvvxx",
"status":"closed",
"user":"admin",
"date":"2017-03-11"
}</pre>
         </div>
     </div>
</template>

