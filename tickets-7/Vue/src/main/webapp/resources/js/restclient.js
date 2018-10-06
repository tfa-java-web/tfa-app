// Client to call server REST api

// Simulation code can be removed by minifier optimizer
var simulate = false;
var debug = true;

// callback parameters:
//    data - contains the resulting data from the request
//    status - contains the status of the request ("success", "notmodified", "error", "timeout", or "parsererror")
    
function restManage( callback  )
{
  return function( data, status, xhr )
  {
	  if ( callback != null )
		  callback ( data, status );
  }
}

function restError( callback  )
{
  return function( xhr, status, excp )
  {
	  if ( debug )
	  {
		  console.log("rest call status " + status );
		  
		  var msg = xhr.responseText;
		  if ( msg.startsWith('<html>') )
		  {
			  var w = window.open();
			  $(w.document.body).html(xhr.responseText);
		  }
		  else if ( msg.length > 0 )
			  alert( msg );
	  }
	  if ( callback != null )
		 callback ( null, status );
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
	
    $.ajax({ type : 'GET', url:"../rest/auth", data:undefined,  success:restManage(callback), error:restError(callback), dataType:"text" }); 
}

function getNextCaptcha( callback )
{
	if ( simulate )
	{
		// captcha "447" image 
        var captchaImage =  "data:image/png;base64,/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDABALDA4MChAODQ4SERATGCgaGBYWGDEj"
        + "JR0oOjM9PDkzODdASFxOQERXRTc4UG1RV19iZ2hnPk1xeXBkeFxlZ2P/2wBDARESEhgVGC8aGi9jQjhCY2NjY2NjY2NjY2NjY2NjY2Nj"
        + "Y2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2P/wAARCABGAMgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQF"
        + "BgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0"
        + "NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXG"
        + "x8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQD"
        + "BAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RV"
        + "VldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk"
        + "5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD0CiiigAoopKAFpKgubyC2XMrge1YN5rk0zFLcbV9e5rSFKU9jalQnU22Nq71K3tOJ"
        + "Hy3oKqHxBa7SQrE+mK5yZZAwMpJZueajrrjh4W1PQhgqdtdTrLDVPt0hVIWAHUmtKszQ7Q29puYDc/NadcdTlUrRPOrKKm1DYKKKKgyC"
        + "iimsyqMsQPrQA6io1mic4V1P40+gbVhaKKKBBRRRQAUUUUAFFFFABRRRQAUUlY+p60sGY7chpB1PYVUYObsjSnTlUdompPPHbxl5GCqK"
        + "w7zX2bKWq+241kzXU1y376UkfpWhZNpluvmSMZHHYiupUVBXkrs744aNJXkrsittOu7+XfLuCk8s1bkWm2tlG0gQMyjq1V21+1VPkVif"
        + "TFU9Q1oXNsY4gUJ60mqs3a1kTJV6rStZGXeTm4uXkPc8fSptLthc3qIc7Ryap1o6ZqMdiCTEWc966ZpqNondUTVO0EdYqhVCgYAGBRWE"
        + "3iNdvywnPvVN7zUNQf8AdBgPRa4lQl10PKjhKj1lodHLdwQjMkqj8az59ft48iMFz7VQi0K6mO6d9v1OTWhBoNrFzIS5HrT5aUd3cvkw"
        + "8PidzOl127lOIlCj86z57u5lJ8yVj7ZxWlql3BATBZoqn+JgM1j8sfUmuqnFWulY76MI25lGwqSOhyjsp9jXT6K93LHvnb93/DxyaraR"
        + "pC7BNcrkn7qmt1VCqAowB2rCvVi/dRx4qvGXuxXzFpKWkIBGD0Nch55Uu9St7VMs4J7AVXs9aguWKv8Auz2z3qveaD5jl4ZOT2NYt1Zz"
        + "Wr7ZUIrrhTpyVr6noUqFGpGyep2iurjKkEe1Orj7LVJ7PgfMvoTW7Y6xDdcP8jehrKdCUfQxq4WdPVao06KarBhlSCKdWJyhRRRQAyUM"
        + "YmCfeI4rnDoV1JIzOVGTnrXTUVpCpKGxtSrSpX5Tmk8PXBb53VR608eHHzzMuK6Kir+sTNHjKvcwE8OfN88w2+wrJ1CBLa5aKNiwXvXa"
        + "Vy99p13NdyOsJ2k8VrRquT95nRhsRKUnzsygMnAqb7JPt3eU2PpV+DRbtZI3ZQBnP0rp1XaoX0GKupXUfh1Na2LULcupwpRkPKkY9RVq"
        + "HU7mAARlVA7AV1sltDIMPEh/Cof7NtP+eC1H1iL+JGTxkJL3omLF4hnXAkQN7ilu9eaaApEhRj1JqTWLOytIsqpEjdBmsGtIQpz95I2p"
        + "UqNT31ECSTk9a2tE09JCJ5iMD7q1m29lPcqWhQsBSvFdWp+bemPfFXP3lypmtX304RlZnaDpxS1zllqy20f76R5XPbsKdN4iY8Qxfia4"
        + "vYTvZHmPCVL2SOhpCQOprk21q8Zs7wPbFQSX93OcGVjnstWsNLqzRYGfVnZFlHVh+dRSG3lUiQow965MC+mIX96fTmphpeoEY2Pg9s0/"
        + "YJbyH9VjHeZevtLtpSXt5kT2JrEmiMMhUkEjuK0P7CvcAcYPvUn/AAj1xtB3rn0raM4x0crnTTqwho53Kllqk9ocA719Ca3rHWYbn5X+"
        + "RvQ96zz4dlwMSqT3q3a6DDFhpmLsD26VnUdKSv1Ma0sPNX6muCCMjpRQoCgADAFFcZ5otFFFABRRRQAUUUUAFFFFABSHODjrS0UAc5qO"
        + "nX9zKZGAYdlB6VQTTbkzrG0bDJ64rsqSuiOIklax2Rxk4q1iG0tY7SERxj6n1qjqOlyXZLLOf909K1aKyU5J8xzxqyjLmT1OPuNJurfk"
        + "puHqKbaabcXT4VCo7k9q7GgADoAK2+sytsdX16drW1Mm30C3Rf3pLtV6CwtoB8kS59TzVmisZVJS3ZzSrTluxAAOgFLRRUGQUUUUAFFF"
        + "FABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAf/Z";
        
		return restSimulate( captchaImage, callback );
	}
    $.ajax({ type : 'PUT', url:"../rest/auth", data:undefined,  success:restManage(callback), error:restError(callback), dataType:"text" }); 
}

function doLogin( username, token, callback )
{
	if ( simulate )
	{
		return restSimulate(  JSON.parse("true"), callback );
	}
	var data = username + ':' + token;
    $.ajax({ type : 'POST', url:"../rest/auth", data:data, success:restManage(callback), error:restError(callback), contentType: "application/json" }); 
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
    $.ajax({ type : 'GET', url:"../rest/status", data:undefined, success:restManage(callback), error:restError(callback), dataType:"json" }); 
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
		
	$.ajax({ type : 'GET', url:"../rest/tickets", data:params, success:restManage(callback), error:restError(callback), dataType:"json" }); 
}

function getTicketsCount( callback )
{
	//Param:	The same parameters as list, excluding order, page and size
	var params = null;

	if ( simulate ) return restSimulate( 18, callback );
	
	$.ajax({ type : 'GET', url:"../rest/tickets/count", data:params,  success:restManage(callback), error:restError(callback), dataType:"json" }); 
}

function postTickets( ticket, callback )
{
	if ( simulate ) return restSimulate( 99, callback );
	
	$.ajax({ type : 'POST', url:"../rest/tickets", data:JSON.stringify(ticket),  success:restManage(callback), error:restError(callback), contentType: "application/json" });
}

function putTickets( ticket, callback )
{
	if ( simulate ) return restSimulate( ticket.id, callback );
	
	$.ajax({ type : 'PUT', url:"../rest/tickets", data:JSON.stringify(ticket),  success:restManage(callback), error:restError(callback), contentType: "application/json" });
}

//---------------- server REST API --------------------------
