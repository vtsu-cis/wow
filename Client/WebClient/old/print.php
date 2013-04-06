<?php
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
	
	function wowQuery($line)
	{
		if (!preg_match("/\n+$/", $line)) {
			$line .= "\n";
		}
		
		$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
		if (!$socket) {
			return false;
		}
	
		socket_set_option($socket, SOL_SOCKET, SO_SNDTIMEO, 
			array(	"sec"=>10, // Timeout in seconds
					"usec"=>0  // I assume timeout in microseconds
				)
		);
	
		if (!socket_connect($socket, "porkbarrel.cis.vtc.edu", 5280)) {
			if (!socket_connect($socket, "silica.cis.vtc.edu", 5280)) {
				return false;
			}
		}
		
		if (!socket_write($socket, $line, strlen($line))) {
			return false;
		}
		
		$reply = '';
		while (true) {
			$line = socket_read($socket, 4096);
			if ($line == NULL)
				break;
			
			$reply .= $line;
		}
		
		socket_close($socket);
		
		return trim($reply);
	}
	
	function compareRecords($record1, $record2) {
		return strcasecmp($record1->lastName, $record2->lastName);
	}
	
	function getRecords($qry="||||||||") {
		$people = wowQuery("QRY: $qry");
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
			$result .= "</tr>";
		}
	}
	
	else if (isset($_GET['dept'])) {
		// First query a list of departments.  Then query a list of people.  Match those people with those departments, then sort the departments alphabetically -- then sort the people to be displayed alphabetically.
		$people = getRecords();
		$departments = wowQuery("DEPTLIST");
		if (!$departments) {
			die("Unable to contact server.  Please try again later.");
		}
		
		$departments = explode("\n", $departments);
		
		sort($departments);
		
		// Here we go...
		$overallList = array();
		foreach ($departments as $department) {
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
									<td width=\"20%\">
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