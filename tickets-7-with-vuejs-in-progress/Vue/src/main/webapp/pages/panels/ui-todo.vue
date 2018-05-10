<script>

   var uiTodo = Vue.component( "ui-todo", 
   { 
       template : "#ui-todo" 
   });

/* Validate selected file BEFORE sending it : must be the same as java validateFile */
function validate( input, event )
{
   var file = null;
   var error = null;
   if ( input.files.length == 1 ) file = input.files[0]; else error = 'select one file';
   if ( file != null && file.size > 10*1024000 ) error = 'file too large';
   if ( file != null && ! file.name.endsWith('.txt') ) error = 'not a .txt file';
   if ( file != null && ! file.type.endsWith('PLAIN') ) file = 'not a plain text file';
    if ( error != null ) {
       input.value = null; 
        document.getElementById('msg').innerHTML = error;
        event.preventDefault();
        return false;                                  
   }
    document.getElementById('msg').innerHTML = "";
    return true;
}

//# sourceURL=http://localhost:8080/vues/pages/panels/ui-todo.vue
</script>

<template id="ui-todo">

   <div class="simple" style="background-color: #F4F4F4">

      <h1>TODO page</h1>
      <br />
      <p>Example of upload file...</p>
      
      <form id="form" enctype="multipart/form-data">
         <input type='file' id="file" value="file"  />
         <input type='button' value="Upload" />
      </form>
  
   </div>
   
</template>

