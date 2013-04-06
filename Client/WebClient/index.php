<?php
    global $validQuery;
	$validQuery = false;
	// Test if a query was sent to this page.
    if (isset($_GET['qry']) && !empty($_GET['qry']) && $_GET['qry'] != 0) {
        $validQuery = true;
    }
	
	/**
	*	Echo the first parameter if $validQuery is true. Echo the second parameter otherwise.
	*/
	function echoIf($str1, $str2) 
	{
		global $validQuery;
		if ($validQuery)
			echo ($str1);
		else 
			echo ($str2);
	}
	
	function fillSearchBar()
	{	
		if(isset($_GET["ln"]) || isset($_GET["fn"]))
		{			
			if (isset($_GET["ln"]))
			{
				echo $_GET["ln"];
			}
		
			if (isset($_GET["fn"]))
			{
				if (isset($_GET["ln"]) && !empty($_GET["ln"]) && substr($_GET["ln"], 0, 1) != "@") {
					echo ",";
				}
				echo " ";
				echo $_GET["fn"];
			}
		}
		else
		{
			echo "Enter WOW Query here...";
		}
	}

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>WOW Web Client</title>
	<link rel="stylesheet" type="text/css" href="css/general.css" />
	<script type="text/javascript" src="js/jquery.js"></script>
	<script type="text/javascript" src="js/Table.js"></script>
    <script type="text/javascript" src="js/BackEnd.js"></script>
	<script type="text/javascript" src="js/Feedback.js"></script>
</head>

<body>
	<div id="container">
	    <div id="top"> <!--for everything above the line -->
	            <div id="dielogo">
	                <a href="index.php">
                        <img src="img/wowDie.png" alt="Graphical WOW logo" />
                    </a>
	            </div>
	            <div id="links">
					<div id="menu">
						<li class="menuitem" id="menuitem1">
							<a href="#" id="emergencyList">Emergency List <img src="img/blue_down_arrow.png" alt="" /></a>
							<div class="subMenu" id="emergencyListSubMenu">
								<span class="bigMenuItem" style="color:red"><b>Williston Police</b>:<br />(802)878-7111</span>
								<span class="bigMenuItem" style="color:red"><b>Randolph Police</b>:<br />(802)728-3737</span>
							</div>
						</li>
						<li class="menuitem" id="menuitem2">
							<a href="#" id="infoList">Info <img src="img/blue_down_arrow.png" alt="" /></a>
							<div class="subMenu" id="infoListSubMenu">
                            	<a href="#" class="general menuItem" id="deptListMenuItem">Dept. List</a>
								<a href="#" class="menuItem" id="generalInfoMenuItem">General Info</a>
								<a href="#" class="menuItem" id="usageMenuItem">Usage Stats</a>
							</div>
						</li>
						<li class="menuitem" id="menuitem3">
							<a href="#" id="printList">Print <img src="img/blue_down_arrow.png" alt="" /></a>
							<div class="subMenu" id="printListSubMenu">
								<a href="#" class="menuItem" id="printThisPageMenuItem">This page</a>
								<a href="#" class="menuItem" id="printDepartmentListMenuItem">Departments</a>
							</div>
						</li>
                        <li class="menuitem">
                            <a href="#" class="advanced" id="advancedItem">Advanced</a>
                        </li>
						<li class="menuitem" id="menuitem4">
							<a href="#" id="helpList">Help <img src="img/blue_down_arrow.png" alt="" /></a>
							<div class="subMenu" id="helpListSubMenu">
								<a href="#" class="help menuItem" id="helpItem">How To</a>
								<a href="#" class="menuItem" id="feedbackMenuItem">Feedback</a>
								<a href="#" class="menuItem" id="aboutMenuItem">About</a>
							</div>
						</li>
					</div>
	            </div>
	            <div id="VTClogo">
	                <a href="http://www.vtc.edu">
                        <img src="img/vtc.png" alt="VTC Logo" />
                    </a>
	            </div>
	    </div>
	    <hr class="topBorder" />
	    
	    <div id="searcharea" class="<?php echoIf("clearBoth fullWidth", "fixedwidth center");?>"> 
            <!--Includes the WOW logo, the search box and the search button-->
            <div id="wowlogo" class="<?php echoIf("floatLeft smallPadding", "center bigPadding"); ?>">
                <a href="index.php"><img src="img/wow_logo.png" alt="WOW Logo" /></a>
            </div>
            <div id="barbutton" class="searchBox <?php echoIf("floatLeft spaceUp", "center"); ?>">
                <input type="text" name="searchBar" id="searchBar" 
                    class="searchBarQuiet <?php echoIf("floatLeft", "fullWidth center"); ?>" 
                    value="<?php fillSearchBar(); ?>" />
                <input type="submit" value="WOW Search" name="searchButton" class="searchButton" id="searchButton" />
                <input type="submit" value="The Old WOW" name="oldWowButton"  id="oldWowButton" />
            </div>
	    </div>
		
		<div id="help-output" <?php echoIf("class=\"floatLeft\"", ""); ?>>
			<noscript>
				<div style="background: red; border: 1px yellow solid; color: white; font-size: large;">
					You must have JavaScript enabled to use this site properly.<br />
					<a style="color: gold;" href="https://www.google.com/adsense/support/bin/answer.py?hl=en&answer=12654">
						How do I enable JavaScript?
					</a>
				</div>
			</noscript>
		</div>
        <div id="output" style="padding-top: 25px;">
        <?php
            echoIf('<img style="margin-left: 5px;" src="img/ajax-loader.gif" alt="Loading..." />', "");
        ?>
        </div>
		
		<div class="push"></div>
	</div>
	
	<div id="footer"> 
		<p>WOW was designed by VTC faculty, students and staff. <br />
		For more information about getting involved <a href="http://vtank.summerofsoftware.org/?p=44">click here</a>.</p>
	</div>	
</body>
</html>
