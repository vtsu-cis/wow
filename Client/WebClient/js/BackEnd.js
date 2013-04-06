TARGET_SCRIPT = "query.php";
PRINT_SCRIPT = "print.php";
PRINT_ALL = 0;
PRINT_RESULTS = 1;
PRINT_DEPT = 2;

var inQuery = false;

var firstClick = true;

help_shown 		= false;
feedback_shown 	= false;
about_shown 	= false;
usage_shown 	= false;
dept_shown		= false;
general_shown	= false;

var advd_shown 	= false;
var advd_search = false;
var emergency   = "#emergencyListSubMenu";
var info        = "#infoListSubMenu";
var print_page  = "#printListSubMenu";

var TARGET_SCRIPT = "query.php";

var menu_open_flag = new Array();
menu_open_flag[emergency] = false;
menu_open_flag[info] = false;
menu_open_flag[print_page] = false;

var timeout = 500; // Milliseconds.
var closetimer = 0;
var menu = null;

var randint;
var enterPressed = 0;
var table = new Table();
SERVERLIST = new Array();
var cachedHtml = "";

/*!
    Clear the "help-output" div area.
*/
function clearHelpOutput() {
    $("#help-output").empty();
    help_shown 		= false;
    feedback_shown 	= false;
    about_shown 	= false;
	usage_shown 	= false;
	dept_shown		= false;
	general_shown	= false;
}

function search(in_str) 
{
    var parameters = "?qry=1";
    var firstName = null;
    var lastName = null;
    if (in_str.match(/,+/)) { // Test for a comma before the end of the string.
        // Delimit the fields by the comma character.
        var commaIndex = in_str.indexOf(',');
        lastName = in_str.substring(0, commaIndex).trim();
        firstName = in_str.substring(commaIndex + 1).trim();
    }
    else {
        in_str.replace(",", "");
        if (in_str.match(/\s+/)) {
            var spaceIndex = in_str.indexOf(' ');
            lastName = in_str.substring(0, spaceIndex).trim();
            firstName = in_str.substring(spaceIndex + 1).trim();
        }
        else {
            lastName = in_str.trim();
        }
    }
    
    if (firstName)
        parameters += "&fn=" + firstName;
    if (lastName)
        parameters += "&ln=" + lastName;
    
	if (randint >= SERVERLIST.length) randint = 0;
	var pref = SERVERLIST[randint];
	
    window.location = parameters + "&pref=" + pref;
}

/*!
	Perform a "@search".
	@param verb Verb to search for (e.g. dept, office).
	@param search String to search.
*/
function atSearch(verb, query)
{	
	window.location = "index.php?qry=1&ln=@" + verb + "&fn=" + query;
}

function setSearchBarEventHandlers() {
	// Event handler: Enter is pressed.
	$("#searchBar")
        .keydown(function(event) {
    		if(event.which == 13) {
    			event.preventDefault();
    			// get the input
    			var in_str = $("#searchBar").val();
			
				var isAtSearch = false;
				var qry = toPacket(in_str);
				if (in_str.length > 1 && in_str.substring(0, 1).match(/@/)) {
					// @search
					isAtSearch = true;
				}
				
				if (!qry) {
					if (!isAtSearch) {
						alert("Only letters, numbers, spaces and dashes allowed.");
					}
					else {
						alert("Invalid @search. Here is what you can use:\n" +
							"@phone\n@email\n@campus\n@role\n@dept\n@fax\n@office\n" +
							"After the @search press the Space Bar key and type what you want to search for.");
					}
					
					return;
				}
				
				if (isAtSearch) {
					var verb = in_str.substring(0, in_str.indexOf(' ')).replace("@", "");
					var query = in_str.substring(in_str.indexOf(' ') + 1);
					
					atSearch(verb, query);
				}
				else {
					search(in_str);
				}
    		}
	});
    
    // Event handler: Search bar is clicked for the first time.
    $("#searchBar")
        .click(function(event) {
			event.preventDefault();
			// if it is the first click, then clear the field and change the text color (css class)
			if(testFirstClick() || $("#searchBar").val().toLowerCase() == "enter wow query here...") {
				$("#searchBar").val("");
                $("#searchBar").removeClass("searchBarQuiet");
				$("#searchBar").addClass("searchBarActive");
			}
	});

	//clicked the wow search button
    $(".searchButton")
        .mousedown(function(event) {
            event.preventDefault();
			// get the input
			var in_str = $("#searchBar").val();
			
			var isAtSearch = false;
			var qry = toPacket(in_str);
			if (in_str.length > 1 && in_str.substring(0, 1).match(/@/)) {
				// @search
				isAtSearch = true;
			}
			
			if (!qry) {
				if (!isAtSearch) {
					alert("Only letters, numbers, spaces and dashes allowed.");
				}
				else {
					alert("Invalid @search. Here is what you can use:\n" +
						"@phone\n@email\n@campus\n@role\n@dept\n@fax\n@office\n" +
						"After the @search press the Space Bar key and type what you want to search for.");
				}
				
				return;
			}
			
			if (isAtSearch) {
				var verb = in_str.substring(0, in_str.indexOf(' ')).replace("@", "");
				var query = in_str.substring(in_str.indexOf(' ') + 1);
				
				atSearch(verb, query);
			}
			else {
				search(in_str);
			}
	});
}

$(document).ready(function() 
{
    // Create a jquery function to preload images. (Thanks: http://www.innovatingtomorrow.net)
    jQuery.preloadImages = function() {
        for (var i = 0; i < arguments.length; i++) {
            jQuery("<img>").attr("src", arguments[i]);
        }
    }
    
    // error message to display if there is an ajax error
	$(document).ajaxError(function(){
		if (window.console && window.console.error) {
			alert("An internal error has occurred: Please contact an administrator.");
		}
	});
	
	$(".advanced")
	 .click(function(event){
		event.preventDefault();  
		menuClose();	
		clearHelpOutput();
		if(!advd_shown) { // show advanced menu
			cachedHtml = $("#barbutton").html();
			$("#barbutton").empty();
			$("#barbutton").append("<br />");
			$("#barbutton").append(
				'<div id="advance">' +
					'<h3>Advanced Search</h3>' +
					'<form name="advancedSearch" method="GET">' +
                    '<input type="hidden" name="qry" value="1" />' +
                    '<table>' +
						'<tr>' +
							'<td>First Name: </td>' +
							'<td>' +
								'<input type="text" name="fn" id="fn" />' +
							'</td>' +
						'</tr>' + 
						'<tr>' + 
							'<td>Last Name: </td>' + 
							'<td>' +
								'<input type="text" name="ln" id="ln" />' +
							'</td>' + 
						'</tr>' + 
						'<tr>' + 
							'<td>Department: </td>' + 
							'<td>' +
								'<input type="text" name="de" id="de" />' +
							'</td>' + 
						'</tr>' + 
						'<tr>' + 
							'<td>Phone Number: </td>' + 
							'<td>' +
								'<input type="text" name="pn" id="pn" />' +
							'</td>' + 
						'</tr>' + 
						'<tr>' + 
							'<td>Fax Number: </td>' + 
							'<td>' + 
								'<input type="text" name="fx" id="fx" />' + 
							'</td>' + 
						'</tr>' + 
						'<tr>' +
							'<td>Office: </td>' +
							'<td>' +
								'<input type="text" name="of" id="of" />' +
							'</td>' +
						'</tr>' +
						'<tr>' + 
							'<td>Campus: </td>' + 
							'<td>' + 
								'<select name="ca">' + 
                                    '<option value="" default="default">Campus...</option>' +
									'<option value="Bennington">Bennington</option>' +
									'<option value="Brattleboro">Brattleboro</option>' +
									'<option value="Randolph">Randolph</option>' + 
									'<option value="Williston">Williston</option>' + 
									'<option value="Windsor">Windsor</option>' +
									'<option value="Other">Other</option>' +
								'</select>' + 
							'</td>' + 
						'</tr>' + 
						'<tr class="button">' + 
							'<td colspan="2">' +
								'<input type="submit" id="advancedSubmit" value="Advanced Search" />' + 
							'</td>' + 
						'</tr>' + 
					'</table>' + 
                    '</form>' +
				'</div>');
			$(".advanced").html("Normal");
			advd_shown = true;
			advd_search = true;
		}
		else {
			$("#barbutton").empty();
			$("#barbutton").append(cachedHtml);
			$(".advanced").html("Advanced");
			advd_shown = false;
			advd_search = false;
			
			setSearchBarEventHandlers();
		}
     });
	 
	 $("#deptListMenuItem")
        .click(function(event) {
			event.preventDefault();
			menuClose();
            
			if (dept_shown) {
				clearHelpOutput();
				return;
			}
			
            $("#help-output").empty();
            $("#help-output").append('<br /><img src="img/ajax-loader.gif" alt="Loading..." />');
            
			var toPost = TARGET_SCRIPT + "?cmd=DEPTLIST";
			$.post(toPost, function(result) { // needs error detection
                $("#help-output").empty();
                if (result.match(/ERROR/)) {
                    // Error occurred.
                    createTable(result);
                    return;
                }
                
                // Result data is newline-delimited. Split it by a newline and sort it by name.
                result = dosort(result.split(/\r*\n\s*/));
                
    			clearHelpOutput();
    			$("#help-output").append("<br />");
				
				dept_shown = true;
    			
    			var str  = "<span id=\"deptlist\"><h3>Department Listings</h3>" + 
                    //'<table align = "center"><tr><td>' + 
                    '<select name="list" size="10" style="width: 250px" onchange="onClickFill(this.options[this.selectedIndex].value)">';
    			
                for (var x = 0; x < result.length; x++) {
                    result[x] = result[x].trim();
                    str +="<option value='"+result[x]+"'>"+result[x]+"</option>";
                }
    			result.sort();
    			str += /*'</td></tr></table>*/'</span>';
				$("#help-output").append(str);
			});
  	});
	
	$("#generalInfoMenuItem").click(function(event) {
		event.preventDefault();
		menuClose();
		
		if (general_shown) {
			clearHelpOutput();
			return;
		}
		else {
			var str = '<style type="text/css">' +
				'img{vertical-align: middle;}' +
				'td { font-family: arial; padding: 2px }' +
				'th.header { background: #FFFFFF; font-size: 0.22in; color: #000000 }' +
				'th.emergencyheader { color: #AA0000; text-align: left; font-family: arial; padding: 4px }' +
				'th.miniheader { background: #FFFFFF; font-size: 14px; color: #000000; padding: 4px }' +
				'th.mediocreheader { font-family: arial; padding: 4px; text-align: left }' +
			'</style>' +			
			'<table cellspacing="2" cellpadding="2" frame="box" width="100%" border="1">' + 
				'<tr>' +
					'<th class="header" align="center" colspan="2">' +
						'<img src="img/wowDie.png" width="30" height="30" alt="" />' +
							'&nbsp; VTC General Information &nbsp;' +
						'<img src="img/wowDie.png" width="30" height="30" alt="" />' +
					'</th>' +
				'</tr>' +
				'<tr>' +
					'<th class="emergencyheader" colspan="2">Emergency Numbers - Randolph</th>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Security & Public Safety</td>' +
					'<td align="center">(802)728-1292</td>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Health Office</td>' +
					'<td align="center">(802)728-1270</td>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Maintenance</td>' +
					'<td align="center">(802)728-1264</td>' +
				'</tr>' +
				'<tr>' +
					'<th class="emergencyheader" colspan="2">Emergency Numbers - Williston</th>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Security & Maintenance</td>' +
					'<td align="center">(802)249-7281</td>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Dean of Williston Campus</td>' +
					'<td align="center">(802)879-2321</td>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Administrative Desk/Receptionist</td>' +
					'<td align="center">(802)879-2323</td>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Nursing Facility</td>' +
					'<td align="center">(802)879-5965</td>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Dental Facility</td>' +
					'<td align="center">(802)879-5643</td>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Williston Police</td>' +
					'<td align="center">(802)878-6611</td>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Vermont State Police, Williston</td>' +
					'<td align="center">(802)878-7111</td>' +
				'</tr>' +
				'<tr>' +
					'<th class="mediocreheader" colspan="2">General</th>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Class Cancellation Line - Randolph</td>' +
					'<td align="center">(802)728-1346</td>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Class Cancellation Line - Williston</td>' +
					'<td align="center">(802)879-2375</td>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Randolph Dean of Students</td>' +
					'<td align="center">(802)728-1212</td>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Randolph Academic Dean</td>' +
					'<td align="center">(802)728-1311</td>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Admissions</td>' +
					'<td align="center">(802)728-1444</td>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Student Accounts</td>' +
					'<td align="center">(802)728-1301</td>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Registrar</td>' +
					'<td align="center">(802)728-1302</td>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Bookstore</td>' +
					'<td align="center">(802)728-1238</td>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">Library</td>' +
					'<td align="center">(802)728-1233</td>' +
				'</tr>' +
				'<tr>' +
					'<td align="left">IT Help Desk</td>' +
					'<td align="center">(802)728-1721</td>' +
				'</tr>' +
			'</table>';
			
			clearHelpOutput();
			$("#help-output").append(str);
			
			general_shown = true;
		}
	});
	
	// Event handler -> Usage Stats menu item clicked.
	$("#usageMenuItem").click(function(event) {
		event.preventDefault();
		menuClose();
		
		if (usage_shown) {
			clearHelpOutput();
			return;
		}
		
		$("#help-output").empty();
		$("#help-output").append('<br /><img src="img/ajax-loader.gif" alt="Loading..." />');
		
		var request = TARGET_SCRIPT + "?cmd=STATS";
		$.post(request, function(result) { // needs error detection
			clearHelpOutput();
			
			usage_shown = true;
			$("#help-output").append('<br />' + result);
		});
	});
	
	setSearchBarEventHandlers();
	
	// get the list of servers and put them into an array
	$.post(TARGET_SCRIPT + "?cmd=GETLIST&arg=''", 
		function(result) { // needs error detection
			SERVERLIST = result.split(/\r*\n/);
			
			// get a random server
			randint = Math.ceil(SERVERLIST.length*Math.random())-1;
	});
	
	//******************** Menu Events ************************************************************
	$("#printListSubMenu").hide();
	$("#emergencyListSubMenu").hide();
	$("#infoListSubMenu").hide();
	
	$("#emergencyList").click(function(){menuOpen("#emergencyListSubMenu")});

	$("#printList").click(function(){menuOpen("#printListSubMenu")});
	
	$("#infoList").click(function(){menuOpen("#infoListSubMenu")});
	
	$("#helpList").click(function(){menuOpen("#helpListSubMenu")});
	//***************** End Of Menu Events ******************************************************
	
	// show the search button as inset when moused over
	$(".button")
        .mouseover(function(event){
            event.preventDefault();
            menuClose();
            $(".button").addClass("buttonInset");
	});
	
	// show the search button as ridged when the mouse is not over
	$(".button")
        .mouseout(function(event){
    		event.preventDefault();
    		menuClose();
    		$(".button").removeClass("buttonInset");
	});
    
    $(".help")
		.click(function(event){
			event.preventDefault();
			menuClose();
			
			if (help_shown) {
				clearHelpOutput();
				return;
			}
			
			clearHelpOutput();
			$("#help-output").hide();
			$("#help-output").append("<img src='img/help_pic.png' alt='Help Picture' /><br />");
			$("#help-output").fadeIn("fast");
			help_shown = true;
    });
	
    // Event handler: "Old WOW Button" is pressed.
	$("#oldWowButton")
		.click(function(event) {
			event.preventDefault();
			
			var in_str = $("#searchBar").val();
			var parameters = "?qry=1";
		    var firstName = null;
		    var lastName = null;
		    if (in_str.match(/,+/)) { // Test for a comma before the end of the string.
		        // Delimit the fields by the comma character.
		        var commaIndex = in_str.indexOf(',');
		        lastName = in_str.substring(0, commaIndex).trim();
		        firstName = in_str.substring(commaIndex + 1).trim();
		    }
		    else {
		        in_str.replace(",", "");
		        if (in_str.match(/\s+/)) {
		            var spaceIndex = in_str.indexOf(' ');
		            lastName = in_str.substring(0, spaceIndex).trim();
		            firstName = in_str.substring(spaceIndex + 1).trim();
		        }
		        else {
		            lastName = in_str.trim();
		        }
		    }
		    
		    if (firstName)
		        parameters += "&fn=" + firstName;
		    if (lastName)
		        parameters += "&ln=" + lastName;
			
			window.location = "old/" + parameters;
		});
		
	// Event handler: Print -> This page
	$("#printThisPageMenuItem")
		.click(function(event) {
			event.preventDefault();
			menuClose();
			window.print();
	});
	
	// Event handler: Print -> Departments
	$("#printDepartmentListMenuItem")
		.click(function(event) {
			event.preventDefault();
			menuClose();
			window.location = "print.php?dept";
	});
	
	// Event handler: Help -> Feedback
    $("#feedbackMenuItem")
        .click(function(event) {
            event.preventDefault();
            menuClose();
            
            if (feedback_shown) {
                clearHelpOutput();
                return;
            }
            
            var buf = '<br />' +
                '<form name="fbForm" ' +
                (inQuery ? 'class="duringQuery"' : "") + '>' +
                '<div id="content">' +
                    '<div id="showErrors"></div>' +
                        '<table>' +
                            '<tr>' +
                                '<td id="yourName">Your name:</td>' +
                                '<td><input type="text" name="fbName" id="fbName" maxlength="50" /></td>' +
                            '</tr>' +
            			'<tr>' +
            				'<td id="yourEmail">Your e-mail:</td>' +
            				'<td><input type="text" name="fbEmail" id="fbEmail" maxlength="75" /></td>' +
            			'</tr>' +
            			'<tr>' +
            				'<td id="contactType">Contact type:</td>' +
            				'<td>' +
            					'<select name="fbType" id="fbType">' +
            						'<option value="Correction">Data Correction</option>' +
            						'<option value="Suggestion">Suggestion</option>' +
            						'<option value="Comment">Comment</option>' +
            						'<option value="Bug">Bug</option>' +
            					'</select>' +
            				'</td>' +
            			'</tr>' + 
            			'<tr>' +
            				'<td id="message" colspan="2">Message:</td>' +
            			'</tr>' +
            			'<tr>' +
            				'<td colspan="2">' +
                                '<textarea maxlength="512" cols="40" rows="7" name="fbMessage" id="fbMessage" ' + 
                                'onclick="javascript:clearOnce(this);">' +
                                'Use the \'Data Correction\' contact type to inform us of incorrect information in a WOW record.</textarea>' +
                            '</td>' +
            			'</tr>' +
            			'<tr>' +
            				'<td colspan="2"><input type="button" value="Submit" name="submit" onclick="check();" /></td>' +
            			'</tr>' +
            			'<tr><td>&nbsp;</td><tr>' +
            		'</table>' +
                '</div>' +
        	'</form>';
            
            clearHelpOutput();
            $("#help-output").append(buf);
            feedback_shown = true;
    });
    
    // Event handler: Help -> About
    $("#aboutMenuItem")
        .click(function(event) {
            event.preventDefault();
            menuClose();
            
            if (about_shown) {
                clearHelpOutput();
                return;
            }
            
            var buf = '<br /><div id="about" ' +
                (inQuery ? 'class="duringQuery" ' : '') +
                'style="margin-right: auto; margin-left: auto;">' +
        		/*'<img src="img/wowDie.png" width="37" height="37" alt="" />' +*/
                '<h3>Window on the World</h3>' +
                '<table style="padding: 8px; text-align: left; width: 350px;">' +
                    '<tr> <td>For help, e-mail:</td> <td>vtcit2@vtc.edu</td> </tr>' +
        			'<tr> <td colspan="2">&nbsp;</td> </tr>' +
        			'<tr> <td>Nick Guertin</td>         <td>Programmer</td> </tr>' +
        			'<tr> <td>Boomer Ransom</td>        <td>Programmer</td> </tr>' +
        			'<tr> <td>Andy Sibley</td>          <td>Programmer</td> </tr>' +
        			'<tr> <td>Andrew Palmer</td>        <td>Programmer</td> </tr>' +
        			'<tr> <td>Trevor Willis</td>        <td>Programmer</td> </tr>' +
                    '<tr> <td>Isaac Parenteau</td>      <td>Programmer, Artist</td> </tr>' +
                    '<tr> <td>Susan Smith</td>          <td>Lead Designer</td> </tr>' +
        			'<tr> <td>Peter Chapin</td>         <td>Technical Advisor</td> </tr>' +
        			'<tr> <td>Christopher Beattie</td>  <td>Project Manager</td> </tr>' +
        			'<tr> <td colspan="2">&nbsp;</td> </tr>' +
        			'<tr> <td style="text-align: center;" colspan="2">WOW Web Client version 2.0</td> </tr>' +
        		'</table>' +
        	'</div>';
            
            clearHelpOutput();
            about_shown = true;
            $("#help-output").append(buf);
    });
    
    // Parse URL arguments.
	var query = getURLParameter("qry");
	if (query && query != "0") {
        inQuery = true;
    
		// Perform query.
		var firstName 		= unescape((getURLParameter("fn") || "").trim());
		var lastName		= unescape((getURLParameter("ln") || "").trim());
		var phoneNumber		= unescape((getURLParameter("pn") || "").trim());
		var email			= unescape((getURLParameter("em") || "").trim());
		var campus			= unescape((getURLParameter("ca") || "").trim());
		var role			= unescape((getURLParameter("ro") || "").trim());
		var department		= unescape((getURLParameter("de") || "").trim());
		var faxNumber		= unescape((getURLParameter("fx") || "").trim());
		var office			= unescape((getURLParameter("of") || "").trim());
		
		if (firstName || lastName){
			$("#searchBar").removeClass("searchBarQuiet");
			$("#searchBar").addClass("searchBarActive");
			firstClick = false;
		}
		
		var packet = null;
		var isAtSearch = false;
		if (lastName.length > 1 && lastName.substring(0, 1).match(/@/)) {
			// @search -- handle special case.
			isAtSearch = true;
			packet = toPacket(lastName + " " + firstName);
		}
		else {
			packet = toFullPacket(firstName, lastName, phoneNumber, email, campus, role, department, faxNumber, office);
		}
		
		if (!packet) {
			// Error occurred.
			if (!isAtSearch) {
				alert("Your query contains invalid characters. Please make sure there are no non-letters or non-numbers.");
			}
			else {
				alert("Invalid @search. Here is what you can use:\n" +
					"@phone\n@email\n@campus\n@role\n@dept\n@fax\n@office\n" +
					"After the @search press the Space Bar key and type what you want to search for.");
			}
		}
		else {
			var pref = getURLParameter("pref");
			var toPost = TARGET_SCRIPT + "/?cmd=QRY&arg=" + packet + "&pref=" + pref;
			
			// do the query
			$.post(toPost, function(result) { // needs error detection
				//create the table, bc we have the results
				if(result == "No results.")
				{
					alert("There are no results for your search.");
				}
				createTable(result);
				enterPressed = 0;
			});
			$("#output").show();
		}
	}
    
    // Do the image pre-loading. This makes it smoother to instantly pop pictures up if we need to. 
    $.preloadImages("img/ajax-loader.gif", "img/help_pic.png");
 });  

/*!
	A small function that performs a search on a department.
*/
function onClickFill(dept) {
	if (!dept) {
		return;
	}
	
	dept = unescape(dept);
	
	var parameters = "?qry=1&de=" + dept.trim();
	
	window.location = "index.php" + parameters;
}

/*!
	Test if the client has clicked on the search bar yet. If not, this function "unsets" the click so that next call to
	testFirstClick() will return the opposite of the first call.
	@return True if the client has clicked for the first time, false any other time.
*/
function testFirstClick()
{
	if (firstClick) {
		firstClick = false;
		return true;
	}
	return firstClick;
}

/*!
	Unsets the first click so that testFirstClick never returns true.
*/
function skipFirstClick()
{
	firstClick = false;
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

/*!
	Trim whitespace from the beginning of a string.
*/
String.prototype.ltrim = function() {
	return this.replace(/^\s+/,"");
}

/*!
	Trim whitespace from the end of a string.
*/
String.prototype.rtrim = function() {
	return this.replace(/\s+$/,"");
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
	
	if (qry.match(/[^@A-Za-z0-9 -.()&'`]/)) {
		return false;
	}
	qry = qry.replace("&", "%");
	
	if (qry.length > 0 && qry.substring(0, 1).match(/@/)) {
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
		else {
			// Invalid @search.
			return false;
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
	if (firstName && firstName.match(/[^A-Za-z0-9 -.]/)) {
		return false;
	}
    
    if (lastName && lastName.match(/[^A-Za-z0-9 -.]/)) {
        return false;
    }
	
	if (phone && phone.match(/[^0-9-()]/)) {
		return false;
	}
	
	if (email && email.match(/[^A-Za-z0-9-_@.]/)) {
		return false;
	}
	
	if (campus && campus.match(/[^A-Za-z .-]/)) {
		return false;
	}
	
	if (role && role.match(/[^A-Za-z0-9 .-_]/)) {
		return false;
	}
	
	if (dept && dept.match(/[^A-Za-z0-9 .-_&()']/)) {
		alert(dept);
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
    
    if (string.match(/ERROR/)) {
        // Print out error, instead of table.
        var error = string;
        if (error.match(/|/)) {
            // Error message is contained within a packet.
            error = error.split("|")[1];
        }
        
        $("#output").empty();
        $("#output").append('<div style="text-align: center; color: red; font-weight: bold;">' + error + '</div>');
        return;
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

function cancelTimer()
{
	if (closetimer) {
		window.clearTimeout(closetimer);
		closetimer = null;
	}
}

function menuOpen( id )
{
	if (menu == id){
		menuClose();
		menu_open_flag[id] = false;
		return;
	}
	else{
		menuClose()
	}
	
	menu = id;
	$(menu).show();
	menu_open_flag[id]= true;
}

function menuClose()
{
	if (menu) {
		$(menu).hide();
		menu = null;
	}
}

function setTimer()
{
	closetimer = setTimeout(menuClose, timeout);
}

/*!
	Parse the parameters, attempting to extract the value.
	@param name Name of the parameters (key).
	@return Value of the given key.
*/
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


function merge(array, begin, begin_right, end)
{
	for(;begin<begin_right; ++begin) {
		if(array[begin]>array[begin_right]) {
			var v=array[begin];
			array[begin]=array[begin_right];
			insert(array, begin_right, end, v);
		}
	}
}

function msort(array, begin, end)
{
	var size=end-begin;
	if(size<2) return;

	var begin_right=begin+Math.floor(size/2);

	msort(array, begin, begin_right);
	msort(array, begin_right, end);
	merge(array, begin, begin_right, end);
}

function merge_sort(array)
{
	msort(array, 0, array.length);
}

function dosort(_array)
{
	merge_sort(_array);

	return _array;
}

function insert(array, begin, end, v)
{
	while(begin+1<end && array[begin+1]<v) {
		array.swap(begin, begin+1);
		++begin;
	}
	array[begin]=v;
}

Array.prototype.swap=function(a, b)
{
	var tmp=this[a];
	this[a]=this[b];
	this[b]=tmp;
}
