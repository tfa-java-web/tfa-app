// Client to call server REST api

// Simulation code can be removed by minifier optimizer
var simulate = false;

// callback parameters:
//    data - contains the resulting data from the request
//    status - contains the status of the request ("success", "notmodified", "error", "timeout", or "parsererror")
    
function restManage( callback  )
{
  return function( data, status, xhr )
  {
	  if ( status !== "success" ) 
	  {
	  	  console.log("rest call in " + status);
	  	  throw "Server returns " + status;
	  }
	  if ( callback != null )
		  callback ( data );
  }
}

function restSimulate( data, callback )
{
    // async server response simulation
	if ( callback != null )
		setTimeout( function() { callback(data,"success"); } , 50 );
}

// ---------------- server REST API --------------------------

function getAuth( callback )
{
	if ( simulate ) return restSimulate( "sim-user", callback );
	
    $.ajax({ type : 'GET', url:"../rest/auth", data:undefined,  success:restManage(callback), dataType:"text" }); 
}

function getStatus( callback )
{
	if ( simulate )
	{
		var status = '['+
		    '{"id":10, "name":"open",       "color":"lightblue"},' +
			'{"id":20, "name":"feedback",   "color":"orange"},' +
			'{"id":30, "name":"accepted",   "color":"rose"},' +
			'{"id":40, "name":"in_progress","color":"red"},' +
			'{"id":50, "name":"suspended",  "color":"grey"},' +
			'{"id":60, "name":"resolved",   "color":"yellow"},' +
			'{"id":70, "name":"validated",  "color":"green"},' +
			'{"id":80, "name":"delivered",  "color":"blue"},' +
			'{"id":90, "name":"closed",     "color":"lightgrey"}]';	
		
		return restSimulate(  JSON.parse(status), callback );
	}
    $.ajax({ type : 'GET', url:"../rest/status", data:undefined, success:restManage(callback), dataType:"json" }); 
}


function getTickets( filter, order, idxPage, pageSize, callback )
{
	if ( simulate ) 
	{
		var tickets = '[' +
			'{"id":1,"title":"A title","desc":"Description...1","status":"10:open","user":"tfa","date":"2016-01-20"},' +
			'{"id":2,"title":"2nd title","desc":"Description...2","status":"90:closed","user":"tfa","date":"2016-03-21"},' +
			'{"id":3,"title":"2nd title","desc":"Description...2","status":"10:open","user":"tfa","date":"2016-01-21"},' +
			'{"id":4,"title":"2nd title","desc":"Description...2","status":"90:closed","user":"guest","date":"2016-01-11"},' +
			'{"id":5,"title":"2nd title","desc":"Description...2","status":"60:resolved","user":"admin","date":"2016-01-21"},' +
			'{"id":7,"title":"2nd title","desc":"Description...2","status":"90:closed","user":"guest","date":"2016-01-11"},' +
			'{"id":9,"title":"2nd title","desc":"Description...2","status":"60:resolved","user":"admin","date":"2016-01-21"},' +
			'{"id":22,"title":"2nd title","desc":"Description...2","status":"70:validated","user":"guest","date":"2016-01-11"},' +
			'{"id":54,"title":"2nd title","desc":"Description...2","status":"90:closed","user":"tfa","date":"2017-01-05"},' +
			'{"id":333,"title":"The title","desc":"Description...3","status":"40:in_progress","user":"other","date":"2017-01-21"},' +
			'{"id":11,"title":"A title","desc":"Description...1","status":"10:open","user":"tfa","date":"2016-01-20"},' +
			'{"id":21,"title":"2nd title","desc":"Description...2","status":"90:closed","user":"tfa","date":"2016-03-21"},' +
			'{"id":31,"title":"2nd title","desc":"Description...2","status":"10:open","user":"tfa","date":"2016-01-21"},' +
			'{"id":41,"title":"2nd title","desc":"Description...2","status":"90:closed","user":"guest","date":"2016-01-11"},' +
			'{"id":55,"title":"2nd title","desc":"Description...2","status":"60:resolved","user":"admin","date":"2016-01-21"},' +
			'{"id":20,"title":"2nd title","desc":"Description...2","status":"70:validated","user":"guest","date":"2016-01-11"},' +
			'{"id":14,"title":"2nd title","desc":"Description...2","status":"90:closed","user":"tfa","date":"2017-01-05"},' +
			'{"id":573,"title":"The title","desc":"Description...3","status":"40:in_progress","user":"other","date":"2017-01-21"}' +
			']';
	
		var tab = JSON.parse( tickets );
		
		var sortAsc = order.indexOf('-') < 0 ;
		var sortOpt = order.split(',')[0].replace('+','').replace('-','');
		
		tab.sort( function(a,b) { 
			if ( sortOpt === "id") 
			{
				var ret = a["id"] - b["id"];
				if ( !sortAsc ) ret = -ret;
				return ret;
			}
			var ret = a[sortOpt].localeCompare( b[sortOpt] );
			if ( !sortAsc ) ret = -ret;
			if ( ret == 0 ) ret = a["id"] - b["id"];
			return ret;
		}); 
		
		if( idxPage == 0 ) tab = tab.slice(0,10); else tab = tab.slice(10);
		
		return restSimulate( tab, callback );
	}

	//Param: spec 
	//Param:	id=123 or id=100,200 to select range
	//Param:	title=The to select Title beginning with 'The', desc=Some for Description beginning with 'Some'
	//Param:	status=suspended or status=in-progress,close to select range of status
	//Param:	user=guest to select only tickets assigned to user name
	//Param:	date=2016-12-25 or date=2016-12-01,2016-12-31 to select date range
	//Param:	order=date-,id to ordering result by date descendant, then by id ascendant
	//Param:	page=1&size=5 to select page 1 (1-based), with page size of 5
	
	var params = { page: idxPage, size: pageSize, order : order };
		
	$.ajax({ type : 'GET', url:"../rest/tickets", data:params, success:restManage(callback), dataType:"json" }); 
}

function getTicketsCount( callback )
{
	//Param:	The same parameters as list, excluding order, page and size
	var params = null;

	if ( simulate ) return restSimulate( 18, callback );
	
	$.ajax({ type : 'GET', url:"../rest/tickets/count", data:params,  success:restManage(callback), dataType:"json" }); 
}

function postTickets( ticket, callback )
{
	if ( simulate ) return restSimulate( 99, callback );
	
	$.ajax({ type : 'POST', url:"../rest/tickets", data:JSON.stringify(ticket),  success:restManage(callback), contentType: "application/json" });
}

function putTickets( ticket, callback )
{
	if ( simulate ) return restSimulate( ticket.id, callback );
	
	$.ajax({ type : 'PUT', url:"../rest/tickets", data:JSON.stringify(ticket),  success:restManage(callback), contentType: "application/json" });
}

//---------------- server REST API --------------------------
