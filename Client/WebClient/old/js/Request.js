// Request.js
// User-friendly Request interface.
function Request(url, requestType)
{
	this.url = url;
	this.timeout = 1000;
	this.request = createXMLHttpRequest();
	this.response = "";
	this.done = false;
	if (!requestType) {
		this.requestType = "GET";
	}
	else {
		this.requestType = requestType;
	}
}

// Call after init.
Request.prototype.execute = function () {
	with (this) {
		try {
			done = false;
			request.open(requestType, url, true);
			prepareResponse();
			request.send(null);
		}
		catch (e) {
			alert(e);
			request.abort();
		}
	}
}

Request.prototype.setFormHeader = function () {
	with (this) {
		if (request) {
			request.setRequestHeader(
				'Content-Type',
				'application/x-www-form-urlencoded; charset=UTF-8'
			);
            
		}
	}
}

Request.prototype.send = function (msg) {
	with (this) {
		if (request) {
			request.send(msg);
		}
	}
}

Request.prototype.prepareResponse = function () {
	with (this) {
		request.onreadystatechange = function () {
			if (request.readyState == 4) {
				if (request.status == 200 || request.status == 304) {
					response = request.responseText;
				} 
				else {
                    var status = '';
                    var code = 0;
                    try {
                        status = request.statusText;
                    }
                    catch (e) {
                        status = "|The server unexpectedly closed the connection.|||||||";
                    }
                    
                    try {
                        code = request.status;
                    }
                    catch (e) {
                        code = 401;
                    }
                    
					response = "|ERROR: " + status + " (code " + code + ")|||||||";
				}
				
				done = true;
			}		
		}
	}
}

Request.prototype.getResponse = function() {
	with (this) return response;
}

Request.prototype.isDone = function() {
	with (this) return done;
}

function createXMLHttpRequest() {
	try { return new XMLHttpRequest(); } catch(e) {}
	try { return new ActiveXObject("Msxml2.XMLHTTP"); } catch (e) {}
	try { return new ActiveXObject("Microsoft.XMLHTTP"); } catch (e) {}
	alert("Your browser does not support AJAX's XMLHttpRequest.  Please update your browser.");
	return null;
}