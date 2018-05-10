// empty ticket
const dummy = { id: null, title: null, desc: null, status: null, user: null, date: null };

//max to truncate desc of ticket
const MAXSHORTDESC = 160;       

//possible status, get from server
var statusList = [];            

// global vue of application 
var app = null;

var renderTicket = 
{
	methods : 
	{	
		// Current ticket selected 
		setCurrent : function(what) 
		{ 
		    app.$emit("current", what); 
		},
		
		formatDate : function(strDate, withYY)
		{
			// format string date from server to ex: "31 Janv." 
			var options = {	day : 'numeric', month : 'short' };
			if ( withYY ) options.year = 'numeric';
			return new Date(Date.parse(strDate)).toLocaleDateString("fr-FR", options).replace('.','');
		},
		
		statusText : function(s)
		{
		    if ( statusList == null ) return s;    
		    var sf = statusList.find( function(e) { return e.name === s || e.id == s; } );
		    if ( sf == null ) return s;
		   return sf.name; 
		},
		
		statusColor : function(s)
		{
			if ( statusList == null ) return "lightgrey";   	
		    var sf = statusList.find( function(e) { return e.name === s || e.id == s; } );
		    if ( sf == null ) return "lightgrey";
			return sf.color; 
		},
		
		shortDesc : function(desc)
		{
			// format string date from server to ex: "31 Janv."
			if (desc == null) return null;
		
			// Truncate description
			var result = desc;
			if (result.length > MAXSHORTDESC - 3) result = result.substr(0, MAXSHORTDESC - 3) + "...";
		
			// Change to Single line (no eol, neither form feed, tab)
			result = result.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replace('\f', ' ');
			if ( result.length > 0 ) result = " - " + result;
			return result;
		}	
	}
};

