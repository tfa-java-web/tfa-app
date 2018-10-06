/* ---------------------------------------------------------------------------- */
/*       Some javascript functions for tfa gui splitter-layout management       */
/* ---------------------------------------------------------------------------- */
/*
 * To move with mouse a vertical or horizontal line 
 * to split panels composing a classic re-sizable layout
 * of 3 panels ( main 100% :  aside | list - detail ) 
 * separated by   vsplitter and hsplitter   lines
 */
var splitpos = 0;
var vspliton = false;
var hspliton = false;
var vsplitter = document.getElementById("vsplitter");
var hsplitter = document.getElementById("hsplitter");

// Minimum size of border panel
var splitmin = 100; // px

// Maximum size of border panel
function splitmax()
{
	var m = document.body.clientWidth - splitmin;
	if (m > 800) m = 800; // px
	return m;
}

// Begin move of the vertical splitter : change cursor
if ( vsplitter != null )
vsplitter.addEventListener("mousedown", function(ev)
{
	splitpos = ev.pageX;
	vspliton = true;
	ev.preventDefault();
	var list = document.getElementById('list');
	var detail = document.getElementById('detail');
	var aside = document.getElementById('aside');
	var main = document.getElementById('main');
	if ( list != null ) list.style.cursor = "col-resize";
	if ( detail != null ) detail.style.cursor = "col-resize";
	aside.style.cursor = "col-resize";
	main.style.cursor = "col-resize";
});

// Begin move of horizontal splitter : change cursor
if ( hsplitter != null )
hsplitter.addEventListener("mousedown", function(ev)
{
	splitpos = ev.pageY;
	hspliton = true;
	ev.preventDefault();
	var list = document.getElementById('list');
	var detail = document.getElementById('detail');
	list.style.cursor = "row-resize";
	detail.style.cursor = "row-resize";
});

// Mouse moved and pressed : splitter move to position
document.addEventListener("mousemove", function(ev)
{
	if (vspliton)
	{
		if (ev.pageX > splitmin && ev.pageX < splitmax())
		{
			vsplitter.style.left = "" + ev.pageX - splitpos + "px";
		}
		ev.preventDefault();
	}
	else if (hspliton)
	{
		if (ev.pageY > splitmin && ev.pageY < splitmax())
		{
			hsplitter.style.top = "" + ev.pageY - splitpos + "px";
		}
		ev.preventDefault();
	}
});

// End move : do the job (resize layout components)
document.addEventListener("mouseup", function(ev)
{
	if (vspliton)
	{
		var list = document.getElementById('list');
		var detail = document.getElementById('detail');
		var main = document.getElementById('main');
		var aside = document.getElementById('aside');
		var swidth = vsplitter.clientWidth;
			
		// No move (simple click) : hide aside
		if (splitpos == ev.pageX && aside.clientWidth > 0)
		{
			aside.style.display = 'none';
			aside.style.width = "0px";
			main.style.width = "calc(100% - " + swidth + "px)";
		}
		else // show or resize aside
		{
			splitpos = ev.pageX - swidth/2;
			if (aside.clientWidth == 0) splitpos = 200; // default size
			if (splitpos < splitmin) splitpos = splitmin;
			if (splitpos > splitmax()) splitpos = splitmax();

			aside.style.display = '';
			aside.style.width = splitpos + "px";
			main.style.width = "calc(100% - " + (splitpos+swidth) + "px)";
		}
		ev.preventDefault();
		if ( list != null ) list.style.cursor = "default";
		if ( detail != null ) detail.style.cursor = "default";
		aside.style.cursor = "default";
		main.style.cursor = "default";
		vspliton = false;
	}
	else if (hspliton)
	{
		var list = document.getElementById('list');
		var detail = document.getElementById('detail');
		var offset = document.getElementById('header').clientHeight;
		var sheight = hsplitter.clientHeight;
		
		// No move ( simple click) : hide list
		if (splitpos == ev.pageY && list.clientHeight > 0) // top of list
		{
			list.style.display = 'none';
			list.style.height = "0px";
			detail.style.height = "calc(100% - " + sheight + "px)";
		}
		else // show or resize list
		{
			splitpos = ev.pageY - offset - sheight/2;
			if (splitpos < splitmin) splitpos = splitmin;
			if (splitpos > splitmax()) splitpos = splitmax();

			list.style.display = '';
			list.style.height = splitpos + "px"; // ?
			detail.style.height = "calc(100% - " + (splitpos+sheight) + "px)";
		}
		ev.preventDefault();
		list.style.cursor = "default";
		detail.style.cursor = "default";
		hspliton = false;
	}
});

/* ---------------------------------------------------------------------------- */
