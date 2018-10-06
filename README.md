# tfa-app

TFA - Java Sources of a full example of Java Web app
**JSF Application**, well comments, quality sources 

# Vue.js version 

A ***vue.js*** version of GUI is also implemented in step 7.
To help about choice   JSF server-side   vs     VUE-JS client-side , 
by comparaison of sources code.

### Java Sources

Sources : the 6 steps of developpement 
    
- Step 1 - Structure : Maven modules | Dependencies | Servers 
         | Logging | Running | Configuration | Embed Jetty 
		 | Exec war | Others | Deployment
		 
- Step 2 - Unit & Web Tests : Unit & IT | Data-test control 
         | Mock tests | Web tests | REST api tests | Launch tests 
		 | Code coverage | Code quality
		 
- Step 3 - Persistence : JPA Hibernate | Generic DAO 
         | H2 Config | Api, Entity | Entity in Json 
		 | Entity Manager
		 
- Step 4 - Core & JSF Beans : Configuration | Validation | Cookies 
         | Dialog boxes | Table pagination | Ticket Edition | REST API 
		 | REST Exceptions | Tickets REST API Doc
		 
- Step 5 - Design Html & Css : Design | Structure of webapp | Html-Css 
         | Less | Layout | Menus | Tabs | Notes, tricks
		 
- Step 6 - Complete application : Complete the application 
         | JAAS | Authentication | Security roles 
		 | Service mail | Pdf generation
		 
- Step 7 - Vue.js 2 version : Refactoring | Single Page 
	 | Debugging Sources Map | Component.vue Packaging | Elcipse configuration
	 | Asynchronism | Comparison with JSF

### Docs 
   see https://tfa.onmypc.net   
   
### Build

   into each tickets-x dir :   mvn clean install , mvn install -P embed
      
### Configuration 

   see sources /etc

### Deploy 

   - war on jetty, tomcat or wilfly servlet only
   - java -jar embedded.war   (jetty embedded) 
		 
> Note : Login for the tickets application at step 6 : admin/admin  or  guest/guest
                    
Thanks.
