<?php
	// WINDOW ON THE WORLD
	// query.php
	// This page is where requests are sent to the server and delivered to the client.
	
    // To view or modify the server list, please see SERVER_LIST.php in the same directory.
	require("SERVER_LIST.php");
    
    error_reporting(1);
    
    set_time_limit(10);
    
    session_start();
    
    /**
            *   Get the last socket error that occurred as a user friendly string.
            */
    function getSocketError()
    {
        return socket_strerror(socket_last_error());
    }
    
	/**
	*	The wowQuery function does the work when the web client requests a query.
	*	$line will be the fully formatted message being sent to the server ( QRY : |||||||| ) 
	*	$pref is the server that the Javascript is requesting the query is sent to.
	*	In the case that the $pref server is unadvailible, a loop through $SERVER_LIST is performed until something goes through.
	**/
	function wowQuery($line, $pref)
	{
		if (!preg_match("/\n+$/", $line)) {
			$line .= "\n";
		}
		
		global $SERVER_LIST;
        
        $keys = array_keys($SERVER_LIST);
        if (!isset($SERVER_LIST[$pref])) {
            srand(time());
            if (sizeof($keys) == 0) {
                die('|ERROR: No available servers.  Try again later.|||||||\n');
            }
            
            $index = rand() % (sizeof($SERVER_LIST) - 1);
            $pref = $keys[$index];
            unset($keys[$index]);
        }
        
        $connectionTimeout = 0.5;
        $timeout = 1;
        $errmsg = "";
        $errno = 0;
        $reply = '';
        $tries = sizeof($keys);
        $index = 0;
        while (true) {
            $socket = fsockopen('tcp://' . $SERVER_LIST[$pref][0], $SERVER_LIST[$pref][1], $errno, $errmsg, $connectionTimeout);
            if (!$socket) {
                if ($tries <= 0) {
                    die('|ERROR: There are no online servers. Please report this and try again later.|||||||\n');
                }
                $tries = $tries - 1;
                
                $pref = $keys[$index++];
                
                continue;
            }
            
            fwrite($socket, $line);
            stream_set_timeout($socket, $timeout);
            $info = stream_get_meta_data($socket);
            
            while (!feof($socket)) {
                $reply .= fread($socket, 8092);
            }
            
            fclose($socket);
            
            break;
        }
		
		return trim($reply);
	}
	
	/**
	*	send_to provides a connection method for the getAllHits() function - providing checks for a \n at the end of the message,  setup of the socket, and reciept of the reply
	**/
	function send_to($server, $port, $msg) 
	{
		if (!preg_match("/\n+$/", $msg)) {
			$msg .= "\n";
		}
        
        $errno = 0;
        $errmsg = '';
        $connectionTimeout = 0.25;
        $timeout = 1;
		$socket = fsockopen('tcp://' . $server, $port, $errno, $errmsg, $connectionTimeout);
        if (!$socket) {
            return false;
        }
        
        fwrite($socket, $msg);
        stream_set_timeout($socket, $timeout);
        $info = stream_get_meta_data($socket);
        
        // Get the server's response to the QRY
        
        while (!feof($socket)) {
            $line = fgets($socket, 8092);
            if ($line == NULL) {
                break;
            }
            
            $reply .= $line;
        }
        
        fclose($socket);
	
		return $reply;
	}

	/**
	*	getAllHits asks each server in $SERVER_LIST how many hits it has, using the send_to() function, and formats the output for the Stats window.
	**/
    function getAllHits()
    {
        global $SERVER_LIST;
        $t_str = "";
        $t_tHits = 0;
        foreach($SERVER_LIST as $shortName => $server)
        {
            $server1 = send_to($server[0], $server[1], "HIT\n");
            if (!$server1) {
                $server1 = '<span style="color: red; font-weight: bold;">OFFLINE</span>';
            }
            
            $t_str .= "<b>" . ucwords($shortName) . "</b><br />";
            $t_tHits = $t_tHits + $server1;
            $t_str .= "Hits: " . $server1 . "<br /><br />";
        }
        $t_str .= "--------------------<br /><b>Total hits</b>: " . $t_tHits;
        return $t_str;
    }
	/**
	*	When the javascript sends a command to this script, three things are needed ->
	*	cmd  which will be QRY, DEPTLIST, STATS, GETLIST, or MAIL
	*	arg which is basically the input from the user, formatted in proper WoW format (last|first|||||||)
	*	pref which is the server they are requesting the CMD to be sent to.
	*
	*	QRY calls the wowQuery() above - and is explained in the comments.
	*	DEPTLIST sends a wowQuery for the list of departments - this is used for the @DEPT window under Search+
	*	STATS checks each server for the number of hits, the formatting anf results of this are processed and explained above.
	*	GETLIST returns the shortName of each server in $SERVER_LIST to the javascript so it can choose where to send the query
	*	MAIL sends a sendmail command to the server when an error arises.
	**/
    
    /*$time = time();
    
    $enabled    = (isset($_SESSION['enabled']) ? $_SESSION['enabled'] : True);
    $last_query = (isset($_SESSION['last_query']) ? $_SESSION['last_query'] : 0);
    $spam_level = (isset($_SESSION['spam_level']) ? $_SESSION['spam_level'] : 0);
    
    if ($enabled === False) {
        // Unban after 5 minutes.
        $elapsed = $time - $last_query;
        $unban_time = 300 - $elapsed;
        if ($elapsed > 300) {
            $spam_level = 0;
            $enabled = True;
        }
        else {
            die('|Please wait ' . $unban_time . ' seconds.|||||||');
        }
    }
    
    if ($time - $last_query < 0.2) {
        // Too soon.
        $spam_level++;
        
        if ($spam_level >= 10) {
            $enabled = False;
        }
    }
    
    $_SESSION['enabled'] = $enabled;
    $_SESSION['last_query'] = $time;
    $_SESSION['spam_level'] = $spam_level;
    
    if (!$enabled) die("Please wait 5 minutes.");*/
	
	$cmd = $_GET['cmd'];
	$arg = $_GET['arg'];
	$pref = $_GET['pref'];
    
    if (empty($cmd)) {
        die('');
    }
	
	switch ($cmd) {
		case "QRY":
			$problem = "\\";
			$arg = str_replace($problem, "", $arg);
			$arg = str_replace("%", "&", $arg);
			$result = wowQuery("QRY: $arg", $pref);
			echo $result;
			break;
		case "DEPTLIST":
			$result = wowQuery("DEPTLIST");
			echo $result;
			break;
		case "STATS":
			$result = getAllHits();
			echo $result;
			break;
		case "GETLIST":
			if (sizeof($SERVER_LIST) == 0) {
                echo "any\n";
                break;
            }
            
            foreach($SERVER_LIST as $server=>$info)
			{
				$srvlst .= "$server\n";
			}
			echo trim($srvlst);
			break;
		case "MAIL":
			$mailResponse = wowQuery("$cmd: $arg", "");
			echo $mailResponse;
			break;
		default:
			echo "Invalid command.";
	}
?>
