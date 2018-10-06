/* ---------------------------------------------------------------------------- */
/*         Some minimalist javascript functions for tfa gui management          */
/* ---------------------------------------------------------------------------- */

/*
 * JSF datatable full row click, usage :
 * 
 * <!-- a click event on all table, and rows identified by a class name --> 
 * <h:dataTable rowClasses="rowClass" onclick="return rowClick(event);"> 
 * 		<!-- on anyone column --> 
 * 		<h:column> 
 * 		<!-- an hidden(empty) link by row, to convert js click to jsf action -->
 * 		<h:commandLink id="rowAction" action="#{bean.click()}" immediate="true" />
 */
function rowClick(e)
{
	/* The table's element under the mouse click */
	e = e.target;

	/* ignore click on my hidden elements */
	if (e.id && e.id.endsWith(":rowAction")) return true;
	if (e.id && e.id.endsWith(":tg")) return true;

	/* Find parent row, up to 3 levels */
	for (var i = 0; i < 2; i++)
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
/* Useful functions for dialog management, ... */

function show(elt)
{

	o = document.getElementById(elt);
	if (o != null) o.style.display = 'block';

	return true;
}

function hide(elt)
{

	o = document.getElementById(elt);
	if (o != null) o.style.display = 'none';

	return true;
}

function click(elt)
{

	o = document.getElementById(elt);
	if (o != null) return o.click();
}

/* ---------------------------------------------------------------------------- */
/* Useful functions for basic base64 and Xor encryptation */

var Base64 =
{

	_keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

	encode : function(input)
	{
		var output = "";
		var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
		var i = 0;

		while (i < input.length)
		{

			chr1 = input.charCodeAt(i++);
			chr2 = input.charCodeAt(i++);
			chr3 = input.charCodeAt(i++);

			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;

			if (isNaN(chr2))
			{
				enc3 = enc4 = 64;
			}
			else if (isNaN(chr3))
			{
				enc4 = 64;
			}

			output = output + this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) + this._keyStr.charAt(enc3)
						+ this._keyStr.charAt(enc4);

		}

		return output;
	},

	decode : function(input)
	{
		var output = "";
		var chr1, chr2, chr3;
		var enc1, enc2, enc3, enc4;
		var i = 0;

		input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

		while (i < input.length)
		{

			enc1 = this._keyStr.indexOf(input.charAt(i++));
			enc2 = this._keyStr.indexOf(input.charAt(i++));
			enc3 = this._keyStr.indexOf(input.charAt(i++));
			enc4 = this._keyStr.indexOf(input.charAt(i++));

			chr1 = (enc1 << 2) | (enc2 >> 4);
			chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
			chr3 = ((enc3 & 3) << 6) | enc4;

			output = output + String.fromCharCode(chr1);

			if (enc3 != 64)
			{
				output = output + String.fromCharCode(chr2);
			}
			if (enc4 != 64)
			{
				output = output + String.fromCharCode(chr3);
			}

		}

		return output;

	},

	utf8_encode : function(string)
	{
		string = string.replace(/\r\n/g, "\n");
		var utftext = "";

		for (var n = 0; n < string.length; n++)
		{

			var c = string.charCodeAt(n);

			if (c < 128)
			{
				utftext += String.fromCharCode(c);
			}
			else if ((c > 127) && (c < 2048))
			{
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			}
			else
			{
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}

		}

		return utftext;
	},

	utf8_decode : function(utftext)
	{
		var string = "";
		var i = 0;
		var c = c1 = c2 = 0;

		while (i < utftext.length)
		{

			c = utftext.charCodeAt(i);

			if (c < 128)
			{
				string += String.fromCharCode(c);
				i++;
			}
			else if ((c > 191) && (c < 224))
			{
				c2 = utftext.charCodeAt(i + 1);
				string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
				i += 2;
			}
			else
			{
				c2 = utftext.charCodeAt(i + 1);
				c3 = utftext.charCodeAt(i + 2);
				string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
				i += 3;
			}

		}

		return string;
	}

}

function encrypt(text, key)
{
	return Base64.encode(xorBytes(Base64.utf8_encode(text), key));
}

function decrypt(hash, key)
{
	return Base64.utf8_decode(xorBytes(Base64.decode(hash), key));
}

// A very simple xor cryptage, variing according first char
// just to mask password on transport, if no https usage ; 
// Warning : this is NOT a robust cryptage at all !
function xorBytes(input, secret)
{
	var output = "";
	output += String.fromCharCode(input.charCodeAt(0));
	var spos = input.charCodeAt(0) % 10;
	for (var pos = 1; pos < input.length; ++pos)
	{
		var xr = input.charCodeAt(pos) ^ secret.charCodeAt(spos);
		output += String.fromCharCode(xr);
		spos += 1;
		if (spos >= secret.length) spos = 0;
	}
	return output;
}

/* ---------------------------------------------------------------------------- */

