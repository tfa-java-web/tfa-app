<script>

   var uiTodo = Vue.component( "ui-todo", 
   { 
       template : "#ui-todo",
       
       data : function() { return {
    	   message : "select a file ..."
       }},
  
       methods : 
       {
    	   checkFile : function(file)
    	   {
    		    var error = null;
    		    if ( file == null ) error = 'select one file';
    		    if ( error == null && file.size > 10*1024000 ) error = 'file too large';
    		    if ( error == null && ! file.name.endsWith('.txt') ) error = 'not a .txt file';
    		    if ( error == null && ! file.type.endsWith('plain') ) error = 'not a plain text file';
    		    if ( error != null ) 
    		    	 this.message = error;  
    		    else
    		    	 this.message = "";
    	   },
    	   
    	   uploadFile : function(file)
    	   {
    	        // Form data
    	        var fdata = new FormData(file);
    	        
                $.ajax({
                    url: "/rest/upload",
                    type: "POST",
                    data: fdata,
                    processData: false,
                    contentType: false,
                    cache : false,
                    
                    xhr: function() {
                        var progress = $('.progress');
                        var xhr = $.ajaxSettings.xhr();

                        progress.show();

                        xhr.upload.onprogress = function(ev) 
                        {
                            if (ev.lengthComputable) {
                                var percentComplete = parseInt((ev.loaded / ev.total) * 100);
                                progress.val(percentComplete);
                                if (percentComplete === 100) {
                                    progress.hide().val(0);
                                }
                            }
                        };

                        return xhr;
                    },
                    
                    success: function (data, status, xhr) 
                    {
                        this.message = "File successfully uploaded";              
                    },
                    
                    error: function(xhr, status, error) {
                        // ...
                    }                
               });  		   
    	   }
       }
   });

//# sourceURL=http://localhost:8080/vues/pages/panels/ui-todo.vue
</script>

<template id="ui-todo">

   <div class="simple" style="background-color: #F4F4F4">

      <h1>TODO page</h1>
      <br />
      <p>Example of upload file...</p>
      <br /> 
      <form id="form" enctype="multipart/form-data">
         <input type='file' id="file" value="file" @change="checkFile($event.target.files[0])" />
         <input type='button' v-if="message==''" value="Upload" @click="uploadFile($event.target.parentElement[0])"/>
         <progress class="progress" value="0" max="100"></progress>
      </form>
      <br/>
      <h4 style="color:red">{{message}}</h4>
      
   </div>
   
</template>

<style scoped>
.progress { display: none; }
</style>