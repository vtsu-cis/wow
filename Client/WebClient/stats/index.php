<?
// Disable error reporting.
error_reporting(0);

// The following list defines which servers are processed by this page. Please see the index.php in the WOW/Client/WebClient folder for more help.
global $SERVER_LIST;
$SERVER_LIST = array(    
	array( "porkbarrel.cis.vtc.edu", 5280, "Porkbarrel" ), // Porkbarrel is the primary server.
	array( "silica.cis.vtc.edu", 5280, "Silica" ),    // Silica is the backup primary server.
	array( "atlantis.cis.vtc.edu", 5280, "Atlantis" ),
	array( "frolic.cis.vtc.edu", 5280, "Frolic" )
);

/*!
	Convert a given RGB value to it's HTML equivalent.
	Thanks to: http://www.anyexample.com/programming/php/php_convert_rgb_from_to_html_hex_color.xml for the source.
*/
function rgb2html($r, $g=-1, $b=-1) 
{
    if (is_array($r) && sizeof($r) == 3)
        list($r, $g, $b) = $r;

    $r = intval($r); 
	$g = intval($g);
    $b = intval($b);

    $r = dechex($r<0?0:($r>255?255:$r));
    $g = dechex($g<0?0:($g>255?255:$g));
    $b = dechex($b<0?0:($b>255?255:$b));

    $color = (strlen($r) < 2?'0':'').$r;
    $color .= (strlen($g) < 2?'0':'').$g;
    $color .= (strlen($b) < 2?'0':'').$b;
    return '#'.$color;
}

/*!
	Send a message to the server. Appends a new line to the end of a message if necessary.
	Returns the output read from the server if successful, or boolean false if unsuccessful.
*/
function send_to($server, $port, $msg) 
{
	// Append a new line to the end of the string if one is not already present.
	if (!preg_match("/\n+$/", $msg)) {
		$msg .= "\n";
	}
	
	// Create a TCP socket.
	$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
	if (!$socket) {
		return false;
	}

	if (!socket_connect($socket, $server, $port)) {
		echo "Warning: Cannot connect to $server on port $port.";
		return false;
	}

	socket_write($socket, $msg, strlen($msg));
	
	// Read from the server until the server closes the connection.
	$reply = '';
	while (true) {
		$line = socket_read($socket, 4096);
		if ($line == NULL)
			break;
		$reply .= $line;
	}
	
	socket_close($socket);

	return $reply;
}

/*!
	Reads in an array of month:year data and converts it to a timestamp. The timestamp is used to sort the array
	in order from earliest month (and year) to the most recent month (and year). This makes it display in a way
	that makes sense to the user.
	
	Returns an array formatted in the same way as the one passed in, but sorted.
*/
function sortByMonth($monthArray) 
{
	$tempArray = array();
	foreach ($monthArray as $monthYear => $hits) {
		$monyr = explode(":", $monthYear);
		$strtime = strtotime($monyr[0] . " " . $monyr[1]);
		$tempArray[$strtime] = $hits;
	}
	
	// Sort by key.
	krsort($tempArray);
	
	// Clear the array.
	unset($monthArray);
	$monthArray = array();
	
	foreach ($tempArray as $monthYear => $hits) {
		$strtime = date('F:Y', $monthYear);
		$monthArray[$strtime] = $hits;
	}
	
	return $monthArray;
}

/*!
	Convenience method for polling all 
*/
function getAllHits()
{       
	global $SERVER_LIST;
	$QRY_TOTAL 	= 0;
	$QRY_DAYS 	= array("MON"=>0, "TUE"=>0, "WED"=>0, "THU"=>0, "FRI"=>0, "SAT"=>0, "SUN"=>0);
	$QRY_HOURS 	= array();
	$QRY_MONYR 	= array();
	$ADD_TOTAL 	= 0;
	$DEL_TOTAL 	= 0;
	$UPD_TOTAL 	= 0;
	$MOST_DAY 	= array(0,"");
	$MOST_MONTH = array(0, "");
	$MOST_HOUR 	= array(0, "");
	
	$header = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">
			 <html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">
			 <head><title>Window on the World Statistics</title><link rel=\"stylesheet\" href=\"wow.css\" type=\"text/css\" /></head><body>";
	
	$v2stats = "<span id=\"v2stats\">";
	$t_str = "<table border=\"0\" cellpadding=\"5\" class=\"one\"><tr><th>Server</th><th>Status</th><th>H</th><th>D</th><th>U</th>";
	
	$index = 0;
	foreach($SERVER_LIST as $server)
	{
		$this_total 	= 0;
		$this_delete 	= 0;
		$this_update 	= 0;
		$this_status 	= "<div style=\"color: #00AA00;\">OK</div>";
		
		// Get the version of the server.
		$version		= trim(send_to(gethostbyname($server[0]), $server[1], "VER\n"));
		$is_main 		= send_to(gethostbyname($server[0]), $server[1], "MAIN\n");
		if (!empty($version) && $version == "1.00") {
			// Get all relevant hits from the server.
			$qry_result = send_to(gethostbyname($server[0]), $server[1], "HITALL\n");
		}
		else if (substr($version, 0, 1) == "2") {
			// Gather statistics from the server.
			$stats = "<p><h3>" . $server[0] . "</h3>" . send_to(gethostbyname($server[0]), $server[1], "STATS\n") . "</p>";
			$stats = str_replace("\n", "<br />", $stats);
			$v2stats .= $stats;
			unset($SERVER_LIST[$index]);
		}
		else {
			// Unknown version.
			$qry_result = "Unrecognized version: $version.";
		}
		
		$serverName = $server[2];
		if ($qry_result == "") {
			
			$this_status = "<div style=\"color: red;\">OFF</div>";
		}
		else if (substr($version, 0, 1) != "2") {
			if (substr($is_main, 0, 1) == "1") {
				$this_status = "<div style=\"color: #005500;\">MAIN</div>";
			}
			
			$lines = explode("\n", $qry_result);
			foreach($lines as $line)
			{	
				if($line != "" && substr($line,0,1)=="Q")
				{
						$nxtime = str_replace( "QRY ", "", $line);
						$QRY_DAYS[date('D', $nxtime)] += 1;
						$QRY_HOURS[date('G', $nxtime)] +=1;
						$monthAndYear = date('F:Y', $nxtime);
						$QRY_MONYR[$monthAndYear] +=1;
						$QRY_TOTAL += 1;
						$this_total +=1;
				}
				elseif($line != "" && substr($line,0,1)=="D")
				{
					$this_delete +=1;
					$DEL_TOTAL +=1;
				}
				elseif($line != "" && substr($line,0,1)=="U")
				{
					$this_update +=1;
					$UPD_TOTAL +=1;
				}
			}
		}
		$t_str .= "<tr><td>$serverName</td><td>$this_status</td><td>$this_total</td><td>$this_delete</td><td>$this_update</td></tr>";
		
		$index++;
	}
	
	$v2stats .= "</span>";
	$t_str .= "<tr><td colspan=2><hr></td></tr><tr></td><td>Total</td><td></td><td>$QRY_TOTAL</td><td>$DEL_TOTAL</td><td>$UPD_TOTAL</td></tr></table>";
	$t_str .= "<table cellpadding=5 class='one'><tr><th>Day</th><th>Value</th><th>Percent</th><th></th>";
	foreach($QRY_DAYS as $day=>$value)
	{
		if($value > $MOST_DAY[0]){
			$MOST_DAY[0] = $value;
			$MOST_DAY[1] = $day;
		}
		$percent = round($value/($QRY_TOTAL == 0 ? 1 : $QRY_TOTAL)*100);
		$bar = "";
		for($x=0;$x<=$percent/2;$x++)
			$bar .= "|";
		$bpercent = round($value/($QRY_TOTAL == 0 ? 1 : $QRY_TOTAL)*510);
		$bg = rgb2html($bpercent, 255-$bpercent,0);
		$t_str .= "<tr><td><center>$day</center></td><td><center>$value</center></td><td bgcolor=$bg><center>$percent%</center></td><td>$bar</td></tr>";
	}
		
	$t_str .= "</table><table cellpadding=5 class='one'><tr><th>Hour</th><th>Hits</th><th>Percent</th><th></th>";
	ksort($QRY_HOURS);
	foreach($QRY_HOURS as $hour=>$value)
	{
		if($value > $MOST_HOUR[0]){
			$MOST_HOUR[0] = $value;
			$MOST_HOUR[1] = $hour;
		}
		$percent = round($value/($QRY_TOTAL == 0 ? 1 : $QRY_TOTAL)*100);
		$bar = "";
		for($x=0;$x<=$percent/2;$x++)
			$bar .= "|";
		$bpercent = round($value/($QRY_TOTAL == 0 ? 1 : $QRY_TOTAL)*510);
		$bg = rgb2html($bpercent, 255-$bpercent,0);
		$t_str .= "<tr><td><center>$hour</center></td><td><center>$value</center></td><td bgcolor=$bg><center>$percent%</center></td><td color=$bg>$bar</td></tr>";
	}
	$t_str .= "</table>";
	
	$prevMon = 0;
	$t_str .= "<table cellpadding=5 class='one'><tr><th>Month</th><th>Total Hits</th><th>Change</th></tr>";
	$QRY_MONYR = sortByMonth($QRY_MONYR);
	$hitCount = array();
	foreach ($QRY_MONYR as $key => $hits) {
		$hitCount[] = $hits;
	}
	
	$index = 0;
	foreach($QRY_MONYR as $monyr=>$value)
	{
		$nextValue = -1;
		if ($index < sizeof($QRY_MONYR)) {
			$nextValue = $hitCount[$index + 1];
		}
		
		if($value > $MOST_MONTH[0]){
			$MOST_MONTH[0] = $value;
			$MOST_MONTH[1] = $monyr;
		}
		$monyr = explode(":", $monyr);
		if($value > $nextValue)
			$change = "+";
		elseif ($value < $nextValue)
			$change = "-";
		else $change = "=";
		$t_str .= "<tr><td><center>$monyr[0] $monyr[1]</center></td><td><center>$value</center></td><td><center>$change</center></td></tr>";
		$prevMon = $value;
		$index++;
	}
	$t_str .= "</table>";
	$t_str2 = "<table class='sum'><tr><th>Summary</th></tr>";
	$t_str2 .= "<tr><td>Based on the following data, <i>$MOST_MONTH[1]</i> has been the most active for the WoW Server, with a total of <i>$MOST_MONTH[0]</i> Hits.<br>
						<i>$MOST_DAY[1]</i> is the day WoW is used the most, with <i>$MOST_DAY[0]</i> Hits<br>
						The most active hour for WoW is <i>$MOST_HOUR[1]</i> with <i>$MOST_HOUR[0]</i> Hits</td></tr>";
	$t_str2 .= "</table>";
	
	echo $header;
	echo $t_str2;
	echo $v2stats;
	return $t_str . "</body></html>";
}

echo getAllHits();