<!--  VUE JS CODE   PART  -->  <script> 


   var uiAside = Vue.component( "ui-aside", 
   { 
        template : "#ui-aside"   
   });

//# sourceURL=http://localhost:8080/vues/pages/panels/ui-aside.vue
</script>

<!--  HTML  PART  -->

<template id="ui-aside" >

    <aside id="aside">

        <!-- accordion left vertical menu -->
        <nav id="accor" class="accor">
        
            <div>
                <!--  invisible check -->
                <input id="ac0" type="radio" name="choix" checked="checked" />
                <!-- 1st level label : icon + string   with direct link -->
                <label for="ac0"><router-link to="home"><i class="fa fa-home"></i>Home</router-link></label>
            </div>

            <div>
                <!--  invisible check -->
                <input id="ac3" type="radio" name="choix" />
                <!-- 1st level label : icon + string -->
                <label for="ac3"><i class="fa fa-flag"></i> Status</label>
                <div>
                    <!--  submenus -->
                    <a href="#">Not closed</a> 
                    <a href="#">Closed</a>
                </div>
            </div>
            <div>
                <input id="ac1" type="radio" name="choix" />
                <label for="ac1"><i class="fa fa-hand-paper-o"></i> Assigned</label>
                <div>
                    <a href="#">To me</a>
                    <a href="#">The guest</a>
                    <a href="#">Someone</a>
                    <a href="#">The french author</a>
                </div>
            </div>
            <div>
                <input id="ac2" type="radio" name="choix" />
                <label for="ac2"><i class="fa fa-filter"></i> Last date</label>
                <div>
                    <a href="#">Today</a> 
                    <a href="#">This week</a> 
                    <a href="#">This month</a>
                </div>
            </div>

        </nav>

    </aside>
  
</template>

<!--  CSS  PART  -->

<style scoped>
.accor input {
    display: none;
}

.accor label {
    display: block;
}

.accor input+label:hover {
    cursor: pointer;
}

.accor input[type='radio']:checked+label:hover {
    cursor: default;
}

.accor input ~ div a {
    display: block;
    height: 0;
    transition: all 0.2s ease-in-out;
    overflow: hidden;
}

.accor input:checked ~ div a {
    height: 1em;
    transition: all 0.4s ease-in-out;
}
</style>

