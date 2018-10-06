/**
 * Paginate a table loaded page by page, with sort management. 
 * Note that the table can change at every moment. use cookie to
 * memorise the table sort
 */

const pageSize = 10; // list 10 tickets by page
const doubleClickDelay = 220; // ms

function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

var paginate = 
{
	data : function()
	{

		return {
			nbPage : 0, // total page count
			page : 0, // current page index
			wanted : 0, // wanted page
			index : -1, // current index in page
			count : 0, // tickets count in base
			lastPageSize : 0, // data count in last age
			sortField : "id", // sort criteria
			sortAsc : true, // sort order
			order : null, // build order
			lastTime : 0,  // double click detection
			upPage : false, // Update page in progress
			upCount : false  // Update count in progress
		}
	},

	methods : {

		init : function()
		{
		    // first call
		    if (this.order == null)
		    {
		    	// Load sort options from cookies
		    	this.loadOptions();
		    }
		    
			// Build order criteria
			this.order = this.sortField + (this.sortAsc ? "+" : "-");
			if (this.sortField !== "id") this.order += ",id";

			// initialise data : first page, first index
			this.updatePage(0, 0);

			// no current, just before zero index
			this.index = -1;
		},

		loadOptions : function()
		{
			// Sort criteria: fieldname read from cookie
			var sf = getCookie("sortField");
			this.sortField = (!sf) ? "id" : sf;				

			// Sort way: boolean read from cookie
			var sa = getCookie("sortAsc");
			this.sortAsc = (!sa) ? true : sa === "true";
		},

		// Abstract function : Get data for current page with sort , return count read
		getDataPage : null,

		// Abstract function : Get total data count
		getDataCount : null,

		// Abstract function : Put as current the data at index in current page
		setAsCurrent : null,

		updateCount : function(psize)
		{
			if ( this.upCount ) return;
			this.upCount = true;
			
			// update total count from base
			var self = this;
			this.getDataCount( function(count)
			{
				if (count < 0) count = 0;
				self.count =  count;
	
				// adjust the total page number
				self.nbPage = Math.floor((count + pageSize - 1) / pageSize);
				
				// adjust the last page size
				self.lastPageSize = Math.floor(count % pageSize);
				if (self.lastPageSize == 0 && count >= pageSize) self.lastPageSize = pageSize;
				
				self.upCount = false;
				if ( self.upPage )
					self.updatePage(self.page,self.index);

				self.coherence2(psize);
			});
		},

		updatePage : function(p, i)
		{
			if ( this.upPage ) return;
			this.upPage = true;
			
			// correction in case of empty table
			if (p < 0) p = 0;
			if (i < 0) i = 0;

			// wanted page and index
			this.page = p;
			this.index = i;

			// read data of this page from database ( with order )
			var self = this;
			this.getDataPage( function(data,psize)
			{
				self.upPage = false;
				if ( self.upCount )
					self.updateCount(psize);
				
				// check, then adjust index and page, reload if necessary
				self.coherence1(psize);
				
				self.setAsCurrent(self.index);
			});
		},

		coherence1 : function(psize)
		{
			// wanted page
			this.wanted = this.page;

			// not the last page
			if (this.page < (this.nbPage - 1))
			{
				// check : list has shrunk into database
				if (psize < pageSize)
					return this.updateCount(psize);

				// force refresh at limit : to get possible change
				else if (this.page == 0 && this.index == 0) 
					return this.updateCount(psize);
			}
			else
			// last page
			{
				// force refresh at limit : to get possible change
				if (this.index >= (this.lastPageSize - 1)) 
					return this.updateCount(psize);
			}
			
			this.coherence2(psize);
		},
		
		coherence2 : function(psize)
		{
			// coherence page & index / nbPage, pageSize & count

			if (this.page >= this.nbPage) this.page = this.nbPage - 1;

			if (this.page < 0) this.page = 0;

			if (this.index >= this.count) this.index = this.count - 1;

			if (this.index >= pageSize) this.index = pageSize - 1;

			if (this.index < 0) this.index = 0;

			// reload correct page if necessary (not wanted page, or incomplete page)
			if ((this.page != this.wanted) || ((this.page == (this.nbPage - 1)) && (psize < this.lastPageSize))
			|| ((this.page < (this.nbPage - 1)) && (psize < pageSize))) 
				this.updatePage(this.page, this.index);

			// in theory :
			// recursive calls can loop in case of continuous database changes,
			// but there's a very little probability : not check here.
		},

		prev : function()
		{
			// first of page
			if (this.index <= 0)
			{
				if (this.page <= 0)
					return this.updatePage(0, 0); // force refresh
				else
					return this.updatePage(this.page - 1, pageSize - 1); // last of previous page
			}
			// previous
			else
				this.index--;

			// set as current
			this.setAsCurrent(this.index);
		},

		next : function()
		{
			// last of page
			if (this.index >= pageSize - 1)
			{
				if (this.page >= this.nbPage - 1)
					return this.updatePage(this.nbPage - 1, this.lastPageSize - 1); // force refresh
				else
					return this.updatePage(this.page + 1, 0); // first of next page
			}
			// last of last page
			else if ((this.page >= (this.nbPage - 1)) && (this.index >= (this.lastPageSize - 1)))
			{
				// force refresh
				return this.updatePage(this.nbPage - 1, this.lastPageSize - 1);
			}
			// next
			else
				this.index++;

			// set as current
			this.setAsCurrent(this.index);
		},

		prevPage : function()
		{
			// is normal click
			var current = new Date().getTime();
			if (current - this.lastTime > doubleClickDelay)
			{
				// decrement page
				if (this.page > 0)
					return this.updatePage(this.page - 1, pageSize - 1);
				// refresh first page
				else
					return this.updatePage(this.page, 0);
			}
			// is double click
			else
			{
				// goto first page
				return this.updatePage(0, 0);
			}
			this.lastTime = current;

			// set as current
			this.setAsCurrent(this.index);
		},

		nextPage : function()
		{
			// is normal click
			var currentTime = new Date().getTime();
			if (currentTime - this.lastTime > doubleClickDelay)
			{
				// increment page
				if (this.page < (this.nbPage - 1))
					return this.updatePage(this.page + 1, 0);
				// refresh last page
				else
					return this.updatePage(this.page, this.lastPageSize - 1);
			}
			// is double click
			else
			{
				// goto last page
				return this.updatePage(this.nbPage - 1, this.lastPageSize - 1);
			}
			this.lastTime = currentTime;

			// set as current
			this.setAsCurrent(this.index);
		},

		sort : function(field)
		{
			// same : inverse sort way
			if (this.sortField === field)
			{
				this.sortAsc = !this.sortAsc;
			}
			// change sort criteria
			else
			{
				this.sortField = field;
				this.sortAsc = true;
			}
			// reload page (refresh all count if change)
			this.init();

			// save new sort criteria
			setCookie( "sortField", field, 365 );
			setCookie( "sortAsc", this.sortAsc, 365 );
		}
	}
};
