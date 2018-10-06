<script>

   var uiLogin = Vue.component( "ui-login", 
   { 
       template : "#ui-login" ,
       
       data : function() { return {
    	
    	    username: "",          // Login id
    	    secret : "",           // clear password
    	    captchaImage : "",     // inline captcha image (base64 encoded) 
    	    captcha : "",          // captcha recognized
    	    messages : ""          // error messages
       }},

       mounted : function()
       {
    	   // ask server to generate a new captcha image 
    	   var self = this;
           getNextCaptcha( function (data) {
               self.captchaImage = data;
           });
       },
       
       methods : 
       {          
           loginAction : function(valid) 
           {
        	   // Validate login form
        	   if ( valid )
        	   {
        		   this.messages = "";
        		   if ( this.username === "" )
        			  this.messages += "<li>Username empty</li>";   
        		   if ( this.secret === "" )
                       this.messages += "<li>Pasword empty</li>";   
                   if ( this.captcha === "" )
                       this.messages += "<li>Captcha empty</li>";   
        		   
                   if ( this.messages === "" )
                   {
                      var self = this;
                      // simple auth data encrypt (not very secure, but minimal solution if https is not used !
                      var password = encrypt( this.captcha+this.secret, "µAv²#5\tp|Xp_àùMr@hjüéZ/vRt!k7+ -\fHgAç" );                 
                      doLogin( this.username, password, function(data,status) 
                      { 
           			      if ( status === "success" )
           				     window.location.href = '../pages/app.jsp#/home';
           			      else
           			    	 self.$parent.$router.push('/logerr');
           		       });
                   }
        	   }   
        	   else // Cancel login form
        	   {
        		   this.$parent.$router.push('/logout');
        	   }
           }
        }
   });
   
//# sourceURL=http://localhost:8080/vues/auth/ui-login.vue
</script>
       
<template id="ui-login">

   <div class="full">   
   
        <!-- background -->
        <div class="full">
            <img src="../resources/img/fond.jpg" class="full" />
        </div>
        
        <ui-dialog title="Authentication" validate="Log in" cancel="Out" close="" :action="loginAction" @hide="loginAction(false)" >
 
              <!-- form field : user -->
              <div>
                  <i class="fa fa-user" style="width: 20px;"></i>
                  <input type="text" v-model="username" required="true" placeholder="username" />
              </div>
                            
              <!-- form fields : user, paswword and a simple captcha to avoid robots -->
              <div style="margin-top: 8px">
              
                  <!-- a captcha image : push at server side : a tfa custom component -->
                  <img :src="captchaImage" style="float:right"></img>
                  <i class="fa fa-lock" style="width: 20px;"></i>
                  
                  <!--  true password entered (without name to not send it on the net!) -->
                  <input type="password" v-model="secret" required="true" placeholder="password" />               
              
              </div>

              <!-- form field : captcha text entered  -->
              <div style="margin-top: 10px; margin-bottom: 10px">
                  <i class="fa fa-tag" style="width: 20px;"></i>
                  <input type="text" v-model="captcha" required="true" placeholder="image's number" />
              </div>

              <!-- a link to another page to manage forgotten password --> 
              <div style="margin-top: 10px; margin-bottom: 10px">
                  <router-link to="/forgot">Forgot password : click here!</router-link>
                  <ul style="color:red; margin-top: 5px" v-html="messages"></ul>
              </div>

         </ui-dialog>
      
   </div>
   
 </template>

