/* ---------------------------------------------------------------------------- */
/* JSF datatable full row click, usage : 
 * 
 * <!-- a click event on all table, and row identified by a class name -->
 * <h:dataTable rowClasses="rowClass" onclick="return rowClick(event);">
 *   <!-- on anyone column -->
 *	 <h:column> 
 *		<!-- an hidden(empty) link by row, to convert js click to jsf action -->
 *		<h:commandLink id="rowAction" action="#{bean.click()}" immediate="true" /> 
 */
function rowClick(e) 
{   
	/* The table's element under mouse */
   	e = e.target; 
   	
    /* ignore click on hidden element */
    if ( e.id && e.id.endsWith(":rowAction") ) return true;
    if ( e.id && e.id.endsWith(":tg") ) return true;
    
    /* Find parent row, on 3 levels up */
    for ( var i=0; i < 2; i++ )
    {
	    e = e.parentNode;                 
	    if (e.className && e.className.indexOf('rowClass') != -1) 
	    {
	    	/* Row found : throw jsf action into this row */
	    	e.querySelector('[id$=rowAction]').click();
	    	return true;
	    }
    }
    /* Not found */
    return true;
}

/* ---------------------------------------------------------------------------- */
/* Usefull functions for dialog management, ... */

function show(elt)
{
	document.getElementById(elt).style.display = 'block';
	return true;
}

function hide(elt)
{
	document.getElementById(elt).style.display = 'none';
	return true;
}

/* ---------------------------------------------------------------------------- */

