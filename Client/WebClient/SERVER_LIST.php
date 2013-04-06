<?php
    /************************************** SERVER LIST INSTRUCTIONS ***********************************************
	*	$SERVER_LIST contains an array-list of servers to send queries to.  The list is not prioritized in any way -- the server chosen is selected by the client's JavaScript.
	*	To insert a server, look at the examples below.  If you wish to add one, use the following format:
	*	"Short Name" => array ( "SERVER NAME" , SERVER PORT )
	*	Important things to know:
	*		- The server's address must be enclosed in quotation marks.
	*		- The server's port must NOT have quotation marks.
	*		- The array() keywords are comma-delimited.  If it's not the last item in the list, it needs a comma at the end.
	*		- If you append a server to the end, add a comma to the end of the previous entry!
	*		- If you want to add a comment to your server entry, place two forward slashes (//) at the end of the server entry and place the comment after that.
	*******************************************************************************************************************/
	global $SERVER_LIST;
	$SERVER_LIST = array(
	    "Porkbarrel"    => array( "porkbarrel.cis.vtc.edu"  , 5280 ),   //Porkbarrel is the primary server; Chris changed this on 10/2/08.
		"Silica"        => array( "silica.cis.vtc.edu"      , 5280 ),	// Silica is the backup primary server.
        "Atlantis"      => array( "atlantis.cis.vtc.edu"    , 5280 ),	// Atlantis is a semi-reliable WOW server.
		"Frolic"		=> array( "frolic.cis.vtc.edu"		, 5280 )	// Frolic is the Windows Server WOW server -- usually used for testing. (Andy 01/19/09)
	);
?>