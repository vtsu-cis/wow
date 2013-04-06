MAIL_TARGET_SCRIPT = "query.php"; //script to use
CLEARED = false;

String.prototype.trim = function() {
	var	str = this.replace(/^\s\s*/, ''),
		ws = /\s/,
		i = this.length;
	while (ws.test(str.charAt(--i)));
		return str.slice(0, i + 1);
}

function sendMail()
{ 
	$("#help-output").val('<img src="img/ajax-loader.gif" alt="Loading..." />');

	var name = document.getElementById('fbName').value;
	var email = document.getElementById('fbEmail').value;
	var message = document.getElementById('fbMessage').value;
	
	name = name.trim();
	email = email.trim();
	message = message.trim();
	
	var type = document.getElementById('fbType').value;

	var mailPacket = "";
	mailPacket = MAIL_TARGET_SCRIPT + "?cmd=MAIL&arg=" + name + "|" + email + "|" + type + "|" + message;
	
	// do the query
	$.post(mailPacket, function(result) { // needs error detection
		$("#help-output").empty();
		$("#help-output").append(result);
	});
}

function check()
{

	var markForError = new Array(3);
	for (var i = 0; i < 3; i++) {
		markForError[i] = "";
	}
	
	if (document.getElementById('fbName').value == null || document.getElementById('fbName').value == "") {
		markForError[0] = "Enter your name.";
	}
	else {
		if (document.getElementById('fbName').value.match(/[^A-Za-z -_]/)){
			markForError[0] = "Please only use letters and spaces for your name.";
		}
	}
	
	if (document.getElementById('fbEmail').value == null || document.getElementById('fbEmail').value == "") {
		markForError[1] = "Enter your e-mail.";
	}
	else {
		if (!document.getElementById('fbEmail').value.match(/@{1}/) || !document.getElementById('fbEmail').value.match(/\.+/) || document.getElementById('fbEmail').value.match(/\|/)) {
			markForError[1] = "Invalid e-mail address.";
		}
	}
	
	if (document.getElementById('fbMessage').value == null || document.getElementById('fbMessage').value == "") {
		markForError[2] = "Enter a message.";
		//for some reason the above line sets the value, not the html
	}
	else {
		if (document.getElementById('fbMessage').value.match(/\|/)) {
			document.getElementById.value.replace("|", "");
		}
	}
	
	var hasError = false;
	if (markForError[0]) {
		document.getElementById('yourName').style.color = "red";
		hasError = true;
	}
	else {
		document.getElementById('yourName').style.color = "black";
	}
		
	if (markForError[1]) {
		document.getElementById('yourEmail').style.color = "red";
		hasError = true;
	}
	else {
		document.getElementById('yourEmail').style.color = "black";
	}
	
	if (markForError[2]) {
		document.getElementById('message').style.color = "red";
		hasError = true;
	}
	else {
		document.getElementById('message').style.color = "black";
	}
	
	if(!hasError){ //we have a valid message to send
	   var result = sendMail();       //send the message
	}
	else{                //or dont send and mark the errors
		var errors = "";
		for (var i = 0; i < 3; i++) {
			if (markForError[i]) {
				errors += markForError[i] + "<br />";
			}
		}
		
		document.getElementById('showErrors').innerHTML = errors;
	}
	
	return false;
}

function clearOnce(element) 
{
	if (!CLEARED) {
		CLEARED = true;
		element.value = '';
	}
}