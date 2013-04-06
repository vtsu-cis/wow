// WOW.js
TARGET_SCRIPT = "query.php";
PRINT_SCRIPT = "print.php";
PRINT_ALL = 0;
PRINT_RESULTS = 1;
PRINT_DEPT = 2;
SERVERLIST = new Array();

var firstClick = false;
var table = new Table();
var randint = 0;
var enterPressed = 0;

/**
* previously function getList(), this code requests a list of servers from query.php so that  we can cycle through them in round-robin style to spread out the load.
**/
    var out = new Request(TARGET_SCRIPT + "?cmd=GETLIST&arg=''");
    out.execute();
    
    if (out.isDone()) {
        var result = out.getResponse();
        SERVERLIST = result.split("\n");
        randint = Math.ceil(SERVERLIST.length*Math.random())-1;
    }
    else {
        interval = setInterval(tryListAgain, 5);
    }

    function tryListAgain() 
    {
        if (out.isDone()) {
            var result = out.getResponse();
            SERVERLIST = result.split("\n");
            randint = Math.ceil(SERVERLIST.length*Math.random())-1;
            clearInterval(interval);
        }
    }
/*
*	end getList()
**/	

// Parse URL arguments. and do a query if needed.
function checkUrlForQuery() 
{
	var query = getURLParameter("qry");
	if (query && query != "0") 
	{

		var firstName 		= (getURLParameter("fn") || "");
		var lastName		= (getURLParameter("ln") || "");
		var phoneNumber		= (getURLParameter("pn") || "");
		var email			= (getURLParameter("em") || "");
		var campus			= (getURLParameter("ca") || "");
		var role			= (getURLParameter("ro") || "");
		var department		= (getURLParameter("de") || "");
		var faxNumber		= (getURLParameter("fx") || "");
		var office			= (getURLParameter("of") || "");
		
		var packet = toFullPacket(firstName, lastName, phoneNumber, email, campus, role, department, faxNumber, office);
		if (!packet) {
		// Error occurred.
			alert("Your query contains invalid characters. Please make sure there are no non-letters or non-numbers.");
			return;
		}
		prepacketedQuery(packet);
	}
}

function getURLParameter( name )
{
	name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
	var regexS = "[\\?&]" + name + "=([^&#]*)";
	var regex = new RegExp(regexS);
	var results = regex.exec(window.location.href);
	if (results == null)
		return "";
	else
		return results[1];
}

function testFirstClick( element )
{
	if (!firstClick) {
		element.value = "";
		firstClick = true;
	}
}

/**
*	Add trim() implementation that will remove leading and trailing white space.
*/
String.prototype.trim = function() {
	var	str = this.replace(/^\s\s*/, ''),
		ws = /\s/,
		i = this.length;
	while (ws.test(str.charAt(--i)));
	return str.slice(0, i + 1);
}

String.prototype.ltrim = function() {
	return this.replace(/^\s+/,"");
}

String.prototype.rtrim = function() {
	return this.replace(/\s+$/,"");
}

/**
*	Send a query to the server with a Request object.  The Request library must be included before this script for this to work.
*	qry:  String (search bar) containing the query.
*/
function query(qry)
{	
	saved = qry;
	var outputElement = document.getElementById('output');
	var interval = "";
	qry = toPacket(qry);
	if (!qry) {
		alert("Only letters, numbers, spaces and dashes allowed.");
		return;
	}
    
    if (enterPressed != 0) {
        return;
    }
    enterPressed = 1;
	
	var imgElement = document.getElementById('searchImg');
	imgElement.innerHTML = "<img src=\"img/ajax-loader.gif\" alt=\"Searching...\" />";
	
	randint += 1;
	if (randint >= SERVERLIST.length)
	{
		randint = 0;
	}
	
	var pref = SERVERLIST[randint];
	var out = new Request(TARGET_SCRIPT + "?cmd=QRY&arg=" + qry + "&pref=" + pref);
	out.execute();
    
	if (out.isDone()) {
		var result = out.getResponse();
		createTable(result);
		
		imgElement.innerHTML = "<input 	type=\"button\" value=\"Search\" name=\"searchButton\" class=\"button\" id=\"searchButton\" onClick=\"javascript:query( document.getElementById('searchBar').value );\" onMouseOver=\"javascript:searchButton_onMouseOver();\" onMouseOut=\"javascript:searchButton_onMouseOut();\" />";
        
        d = function() {
            enterPressed = 0;
        }
        setTimeout(d, 1000);
	}
	else {
		// Poll the script every few milliseconds to see if it can get the data and display it yet.
		interval = setInterval(tryAgain, 5);
	}

	function tryAgain() 
	{
		if (out.isDone()) {
			var result = out.getResponse();
			createTable(result);
			
			imgElement.innerHTML = "<input 	type=\"button\" value=\"Search\" name=\"searchButton\" class=\"button\" id=\"searchButton\" onClick=\"javascript:query( document.getElementById('searchBar').value );\" onMouseOver=\"javascript:searchButton_onMouseOver();\" onMouseOut=\"javascript:searchButton_onMouseOut();\" />";
					
			clearInterval(interval);
            
            d = function() {
                enterPressed = 0;
            }
            setTimeout(d, 1000);
		}
	}
}

function prepacketedQuery(qry)
{	
	saved = qry;
	var outputElement = document.getElementById('output');
	var interval = "";
	
	if (!qry) {
		alert("Only letters, numbers, spaces and dashes allowed.");
		return;
	}
    
    if (enterPressed != 0) {
        return;
    }
    enterPressed = 1;
	
	var imgElement = document.getElementById('searchImg');
	imgElement.innerHTML = "<img src=\"img/ajax-loader.gif\" alt=\"Searching...\" />";
	
	randint += 1;
	if (randint >= SERVERLIST.length)
	{
		randint = 0;
	}
	
	var pref = SERVERLIST[randint];
	var out = new Request(TARGET_SCRIPT + "?cmd=QRY&arg=" + qry + "&pref=" + pref);
	out.execute();
    
	if (out.isDone()) {
		var result = out.getResponse();
		createTable(result);
		
		imgElement.innerHTML = "<input 	type=\"button\" value=\"Search\" name=\"searchButton\" class=\"button\" id=\"searchButton\" onClick=\"javascript:query( document.getElementById('searchBar').value );\" onMouseOver=\"javascript:searchButton_onMouseOver();\" onMouseOut=\"javascript:searchButton_onMouseOut();\" />";
        
        d = function() {
            enterPressed = 0;
        }
        setTimeout(d, 1000);
	}
	else {
		// Poll the script every few milliseconds to see if it can get the data and display it yet.
		interval = setInterval(tryAgain, 5);
	}

	function tryAgain() 
	{
		if (out.isDone()) {
			var result = out.getResponse();
			createTable(result);
			
			imgElement.innerHTML = "<input 	type=\"button\" value=\"Search\" name=\"searchButton\" class=\"button\" id=\"searchButton\" onClick=\"javascript:query( document.getElementById('searchBar').value );\" onMouseOver=\"javascript:searchButton_onMouseOver();\" onMouseOut=\"javascript:searchButton_onMouseOut();\" />";
					
			clearInterval(interval);
            
            d = function() {
                enterPressed = 0;
            }
            setTimeout(d, 1000);
		}
	}
}

function queryIfEnter(evt, qry) 
{
	var keyCode = -1;
	
	if ( evt.which ) {
		keyCode = evt.which;
	}
	else {
		keyCode = evt.keyCode;
	}
	
	if (keyCode == 13) {
		// enter
		query(qry);
	}
}

/**
*	Does basic error checking.  Returns false on error.  Converts a string into a send-able query string.
*	qry: String to convert.
*/
function toPacket(qry)
{
	qry = qry.rtrim();
	var firstName = "";
	var lastName = "";
	var phoneNumber = "";
	var email = "";
	var campus = "";
	var role = "";
	var department = "";
	var fax = "";
	var office = "";
	
	qry = qry.replace(",", "");
	
	if (qry.match(/[^@A-Za-z0-9 -.()&]/)) {
		return false;
	}
	qry = qry.replace("&", "%");
	
	if (qry.match(/^@{1}/)) {
		// an @search is a search targeted at a specific field i.e. @dept Williston Work-Study Student
		var index = qry.indexOf(' ');
		if (index < 0) {
			return false;
		}
		
		var verb = qry.substring(0, index);
		var search = qry.substring(index + 1);
		
		if (verb.match(/dept/) || verb.match(/department/)) {
			department = search;
		}
		else if (verb.match(/office/)) {
			office = search;
		}
		else if (verb.match(/phone/)) {
			phoneNumber = search;
		}
		else if (verb.match(/email/)) {
			email = search;
		}
		else if (verb.match(/fax/)) {
			fax = search;
		}
		else if (verb.match(/campus/)) {
			campus = search;
		}
		else if (verb.match(/role/)) {
			role = search;
		}
	}
	else {
		var index = qry.indexOf(' ');
		if (index != -1) {
			lastName = qry.substring(0, index);
			firstName = qry.substring(index);
		}
		else {
			lastName = qry;
		}
	}
	
	qry = 	firstName.trim() + "|" + 
			lastName.trim() + "|" + 
			phoneNumber.trim() + "|" + 
			email.trim() + "|" + 
			campus.trim() + "|" + 
			role.trim() + "|" + 
			department.trim() + "|" + 
			fax.trim() + "|" + 
			office.trim();
	
	return qry;
}

/*!
	This version of toPacket is functionally equivalent to the one that takes a single argument. This creates a packet
	with every possible field filled in by given values. Does basic error checking.
	Returns false if an error occurred (invalid query) or the packet once it's done.
*/
function toFullPacket(firstName, lastName, phone, email, campus, role, dept, fax, office)
{
	if (firstName && firstName.match(/[^A-Za-z0-9 -\.]/)) {
		return false;
	}
    
    if (lastName && lastName.match(/[^A-Za-z0-9 -\.]/)) {
        return false;
    }
	
	if (phone && phone.match(/[^0-9-()]/)) {
		return false;
	}
	
	if (email && email.match(/[^A-Za-z0-9-_@\.]/)) {
		return false;
	}
	
	if (campus && campus.match(/[^A-Za-z \.-]/)) {
		return false;
	}
	
	if (role && role.match(/[^A-Za-z0-9 \.-_]/)) {
		return false;
	}
	
	if (dept && dept.match(/[^A-Za-z0-9 \.-_&()]/)) {
		return false;
	}
	
	if (fax && fax.match(/[^0-9-()]/)) {
		return false;
	}
	
	if (office && office.match(/[^A-Za-z0-9 -_()&]/)) {
		return false;
	}
	
	// Packet is OK!
	var packet = firstName + "|" + lastName + "|" + phone + "|" + email + "|" + 
		campus + "|" + role + "|" + dept + "|" + fax + "|" + office;
		
	return packet;
}

function createTable(string, delimiter)
{
	if (!delimiter) {
		delimiter = "|";
	}
	
	if (table) {
		table.clear();
	}
	else {
		table = new Table();
	}
	
	if (string == "") {
		var rows = new Array();
	}
	else {
		var rows = string.split(/\r*\n/);
	}
	
	for (var i = 0; i < rows.length; i++) {
		table.addRow(rows[i]);
	}
	
	table.generateHTML(document.getElementById('output'));
}

function doSort( columnName )
{
	if (table) {
		table.setForSort( columnName );
	}
}

function sendPrintData( type, data )
{
	switch ( type ) {
		case PRINT_ALL:
			window.open(PRINT_SCRIPT + "?all");
			break;
		case PRINT_RESULTS:
			data = toPacket(document.getElementById('searchBar').value);
			if (!data) {
				alert("Your search contains illegal characters.");
				return false;
			}
			
			window.open(PRINT_SCRIPT + "?results&data=" + data);
			break;
		case PRINT_DEPT:
			window.open(PRINT_SCRIPT + "?dept");
			break;
	}
}

/**
	Start event handlers.
*/
function searchButton_onMouseOver() 
{
	var bar = document.getElementById('searchButton');
	bar.style.borderStyle = "inset";
}

function searchButton_onMouseOut()
{
	var bar = document.getElementById('searchButton');
	bar.style.borderStyle = "ridge";
}

function row_onMouseOver(element, bgtext)
{
	if (bgtext == "red") {
		element.style.background = "#FFEEEE";
	}
	else if (bgtext == "green") {
		element.style.background = "#EEFFEE";
	}
	else {
		element.style.background = "#EEEEFF";
	}
}

function row_onMouseOut(element, bgtext)
{
	if (bgtext == "red") {
		element.style.background = "#FFCCCC";
	}
	else if (bgtext == "green") {
		element.style.background = "#CCFFCC";
	}
	else {
		element.style.background = "#FFFFFF";
	}
}
