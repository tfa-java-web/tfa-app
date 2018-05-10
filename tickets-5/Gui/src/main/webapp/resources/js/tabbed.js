/* -------------------------------------------------------------------------- */
/*      Some functions to implement Tabs switching for tfa gui management     */
/* -------------------------------------------------------------------------- */

/*
 * Tabbed panels management show and hide 
 * tabA for All tabs display, 
 * tab1,tab2,tab3... for single tab display
 * 
 */
var oldtab = '1'; // default : display first tab

function tabChange(numtab)
{
	// switch tab header from old to num 
	document.getElementById('tab' + oldtab).className = 'tabUnset tab';
	document.getElementById('tab' + numtab).className = 'tabSet tab';
	if (oldtab == 'A')  // all was displayed
	{
		for (var i = 1; i <= 20; i++)
		{
			// Hide all tabs contents			
			elt = document.getElementById('tabContent' + i);
			if (elt == null) break;
			elt.style.display = 'none';
		}
	}
	else
	{
		// Hide old tab content			
		document.getElementById('tabContent' + oldtab).style.display = 'none';
	}
	if (numtab == 'A')  // all to be displayed
	{
		for (var i = 1; i <= 20; i++)
		{
			// Show all tabs contents
			elt = document.getElementById('tabContent' + i);
			if (elt == null) break;
			elt.style.display = 'block';
		}
	}
	else
	{
		// Show tab content
		document.getElementById('tabContent' + numtab).style.display = 'block';
	}
	// Store actual tab displayed
	oldtab = numtab;
}

/* ---------------------------------------------------------------------------- */
tabChange(oldtab); // init

/* ---------------------------------------------------------------------------- */

