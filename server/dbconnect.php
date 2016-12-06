<?php

include_once "dbconst.php";

//function to connect to database
function db_connect(){
	$conn = new mysqli(SERVER, USERNAME, PASSWORD, DBNAME);
	// Check connection
	if (!$conn) {
		die("Connection failed: " . mysqli_connect_error());
	}

	return $conn;
}
//function to disconnect from database
function db_disconnect($conn){
	$conn->close();
}

?>
