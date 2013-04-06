// Menu.js

document.onclick = menuClose;
var timeout = 500; // Milliseconds.
var menu = 0;
var closetimer = 0;

var faxWindow = null;
var deptWindow = null;
var infoWindow = null;
var helpWindow = null;
var feedbackWindow = null;
var statsWindow = null;
var tutorialWindow = null;
var aboutWindow = null;
var quickWindow = null;

var TARGET_SCRIPT = "query.php";

var win = null;

function display( element )
{
	element.style.visibility = 'visible';
}

function hide( element )
{
	element.style.visibility = 'hidden';
}

function menuOpen( id )
{
	cancelTimer();
	
	if (menu) {
		hide(menu);
	}
	
	menu = document.getElementById( id );
	display(menu);
}

function menuClose()
{
	if (menu) {
		hide(menu);
	}
}

function setTimer()
{
	closetimer = window.setTimeout(menuClose, timeout);
}

function cancelTimer()
{
	if (closetimer) {
		window.clearTimeout(closetimer);
		closetimer = null;
	}
}

function select( name )
{
	// Define name structure.
	switch ( name ) {
		case "fax":
			if (faxWindow) {
				faxWindow.close();
			}
			
			faxWindow = new Window({className: "spread", title: "Fax Numbers", width:600, height:500, url:"faxNumbers.html", destroyOnClose: true, recenterAuto:false});
			//faxWindow.getContent().update(getFaxHTML());
			faxWindow.showCenter();
			break;
			
		case "dept":
			var out = new Request(TARGET_SCRIPT + "?cmd=DEPTLIST");
			out.execute();
	
			interval = setInterval(tryAgain, 5);
			
			break;
			
		case "phone":
			var element = document.getElementById('searchBar');
			element.blur();
			element.focus();
			element.value = "@phone ";
			break;
			
		case "office":
			var element = document.getElementById('searchBar');
			element.blur();
			element.focus();
			element.value = "@office ";
			break;
			
		case "info":
			if (infoWindow) {
				infoWindow.close();
			}
			
			infoWindow = new Window({className: "spread", title: "Information", width:600, height:500, url:"information.html", destroyOnClose: true, recenterAuto:false});
			infoWindow.showCenter();
			break;
		
		case "stats":
			var out = new Request(TARGET_SCRIPT + "?cmd=STATS");
			out.execute();
	
			interval = setInterval(tryStatsAgain, 5);
			
			break;
			
		case "help":
			// Spawn help.html window
			if (helpWindow) {
				helpWindow.close();
			}
			
			helpWindow = new Window({className: "spread", title: "Help", width:600, height:500, url:"help.html", destroyOnClose: true, recenterAuto:false});
			helpWindow.showCenter();
			
			break;
			
		case "tutorial":
			// Spawn tutorial.html window
			if (tutorialWindow) {
				tutorialWindow.close();
			}
			
			tutorialWindow = new Window({className: "spread", title: "Tutorial", width:600, height:500, url:"tutorial.html", destroyOnClose: true, recenterAuto:false});
			tutorialWindow.showCenter();
			
			break;
			
		case "quick":
			// Spawn quick.html window
			if (quickWindow) {
				quickWindow.close();
			}
			quickWindow = new Window({className: "spread", title: "Fast Help", width:510, height:365, url:"quick.html", destroyOnClose: true, recenterAuto:false});
			quickWindow.showCenter();
			
			break;
			
		case "about":
			// Spawn about.html window
			if (aboutWindow) {
				aboutWindow.close();
			}
			
			aboutWindow = new Window({className: "spread", title: "About", width:410, height:340, url:"about.html", destroyOnClose: true, recenterAuto:false});
			aboutWindow.showCenter();
			
			break;
			
		case "feedback":
			// Spawn feedback.html window
			if (feedbackWindow) {
				feedbackWindow.close();
			}
			
			feedbackWindow = new Window({className: "spread", title: "Feedback", width:400, height:400, url:"feedback.html", destroyOnClose: true, recenterAuto:false});
			feedbackWindow.showCenter();
			
			break;
	}
	
	function tryAgain() 
	{
		if (out.isDone()) {
			var result = out.getResponse();
			result = result.split("\n");
			
			if (deptWindow) {
				deptWindow.close();
			}
			
			deptWindow = new Window({className: "spread", title: "Dept List", width:300, height:200, destroyOnClose: true, recenterAuto:false});
			
			var str = "<center><SELECT NAME='list' SIZE='10' onchange=\"onClickFill(this.options[this.selectedIndex].value)\">\n";
			
			result = dosort(result);
			
			for (var x = 0; x < result.length; x++) {
				result[x] = result[x].replace("'", "`");
				str +="<option value='"+result[x]+"'>"+result[x]+"</option>";
			}
			str +="</SELECT></center>\n";
			
			deptWindow.getContent().update(str);
			deptWindow.showCenter();
			
			clearInterval(interval)
		}
	}
	
	function tryStatsAgain() 
	{
		if (out.isDone()) {
			var result = out.getResponse();
			
			if (statsWindow) {
				statsWindow.close();
			}
			
			statsWindow = new Window({className: "spread", title: "WOW Usage Statistics", width:300, height:200, destroyOnClose: true, recenterAuto:false});
			
			var str = "<center><span style=\"font-weight: bold; font-size: 18px;\"></span><br>\n";
			str +=result;
			str +="</center>\n";
			
			statsWindow.getContent().update(str);
			statsWindow.showCenter();
			
			clearInterval(interval)
		}
	}
}

function onClickFill(_dept)
{
	var element = document.getElementById('searchBar');
	_dept = _dept.replace("`", "'");
	element.value = "@dept "+_dept;
	query("@dept "+_dept);
	deptWindow.close();
}


//*************** MERGE SORT **************************
//   Basic idea how it works:
//    1. Divide the unsorted list into two sublists of about half the size
//   2. Divide each of the two sublists recursively until we have list sizes of length 1, in which case the list itself is returned
//   3. Merge the two sublists back into one sorted list.
    
Array.prototype.swap=function(a, b)
{
	var tmp=this[a];
	this[a]=this[b];
	this[b]=tmp;
}

function insert(array, begin, end, v)
{
	while(begin+1<end && array[begin+1]<v) {
		array.swap(begin, begin+1);
		++begin;
	}
	array[begin]=v;
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
//******************* END OF MERGE SORT *************************