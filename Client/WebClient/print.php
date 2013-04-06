<?php
    error_reporting(0);

    // Load the server list. To view or modify it, go to SERVER_LIST.php.
    require('SERVER_LIST.php');

	class Record {
		var $firstName, $lastName, $phoneNumber, $email, $campus, $role, $department, $fax, $office;
		
		function __construct($f, $l, $p, $e, $c, $r, $d, $fa, $o) {
			$this->firstName = $f;
			$this->lastName = $l;
			$this->phoneNumber = $p;
			$this->email = $e;
			$this->campus = $c;
			$this->role = $r;
			$this->department = $d;
			$this->fax = $fa;
			$this->office = $o;
		}
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
                die('|No available servers.  Try again later.|||||||\n');
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
	
	function compareRecords($record1, $record2) {
		return strcasecmp($record1->lastName, $record2->lastName);
	}
    
    function compareDepts($dept1, $dept2) {
        return strcasecmp(trim($dept1), trim($dept2));
    }
	
	function getRecords($qry="||||||||") {
		$people = wowQuery("QRY: $qry", "");
		if (!$people) {
			echo "Error: socket connection failed.  Try again later.";
		}
		
		$records = array();
		
		$people = split("\r*\n+", $people);
		
		foreach ($people as $person) {
			$person = split('\\|', $person);
			
			$records[] = new Record($person[0], $person[1], $person[2], $person[3], $person[4], $person[5], $person[6], $person[7], $person[8]);
		}
		
		usort($records, "compareRecords");
		
		return $records;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	$result = "";
	
	if (isset($_GET['all'])) {
		// Query everything in the server and format it for printing.
		$people = getRecords();
		
		foreach ($people as $person) {
			$result .= "<tr>";
			$result .= "<td>" . $person->lastName . ", " . $person->firstName . "</td>";
			$result .= "<td>" . $person->role . "</td>";
			$result .= "<td>" . $person->phoneNumber . "</td>";
            $result .= "<td>" . $person->office . "</td>";
			//$result .= "<td>" . $person->email . "</td>";
            
			$result .= "</tr>";
		}
	}
	
	else if (isset($_GET['dept'])) {
		// First query a list of departments.  Then query a list of people.  Match those people with those departments, then sort the departments alphabetically -- then sort the people to be displayed alphabetically.
		$people = getRecords();
		$departments = wowQuery("DEPTLIST", "");
		if (!$departments) {
			die("Unable to contact server.  Please try again later.");
		}
		
		$departments = split("\r*\n+\s*", $departments);
        
		usort($departments, "compareDepts");
		
		// Here we go...
		$overallList = array();
		foreach ($departments as $department) {
            $department = trim($department);
			$tempList = array();
			foreach ($people as $person) {
				if ($person->department == $department) {
					$tempList[] = $person;
				}
			}
			
			usort($tempList, "compareRecords");
			
			$overallList[$department] = $tempList;
			
			$normal = "not";
		}
	}
	
	else if (isset($_GET['results'])) {
		$qry = $_GET['data'];
		
		if (empty($qry)) {
			die("No results found.");
		}
		else {
			$people = getRecords($qry);
			if (!$people) {
				die("The server currently can't be contacted.  Please try again later.");
			}
			else if (empty($people)) {
				die("Your query returned no results.");
			}
		}
		
		foreach ($people as $person) {
			$result .= "<tr>";
			$result .= "<td>" . $person->lastName . ", " . $person->firstName . "</td>";
			$result .= "<td>" . $person->role . "</td>";
			$result .= "<td>" . $person->phoneNumber . "</td>";
            $result .= "<td>" . $person->office . "</td>";
			//$result .= "<td>" . $person->email . "</td>";
            
			$result .= "</tr>";
		}
	}
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
	<head>
		<title>WOW List</title>
		<style>
		table {
			text-align: left;
		}
		
		td {
			padding-left: 20px;
		}
		</style>
	</head>
	<body>
		<center>
			<?php
			if ($normal == "") {
			?>
			<img src="img/wowContactListHeader.JPG" alt="Window on the World" />
		
			<table border="2" cellspacing="4" width="100%">
				<tr>
					<th>Name</th>
					<th>Role</th>
					<th>Phone Number</th>
					<th>Office</th>
				</tr>
				<?php
				echo $result;
				?>
			</table>
			<?php
			} else {
			?>
			<h3>VTC Full Department List</h3>
			<table cellspacing="4" width="100%" align="left">
				<?php
				foreach ($overallList as $key => $value) {
                    if (empty($value))
                        continue;
				?>
				<tr>
					<th><?php echo $key; ?></th>
				</tr>
				<tr>
					<td>
						<table align="left" width="100%">
						<?php
							foreach ($value as $person) {
								echo "
								<tr>
									<td width=\"20%\">
										" . $person->lastName . ", " . $person->firstName . 
									"</td>
									<td width=\"15%\">
										" . $person->role . "
									</td>
									<td width=\"20%\">
										" . $person->phoneNumber . "
									</td>
                                    <td width=\"30%\">
										" . $person->office . "
									</td>
								</tr>
								";
							}
						?>
						</table>
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<?php
				}
				?>
			</table>
			<?php
			}
			?>
		</center>
	</body>
</html>