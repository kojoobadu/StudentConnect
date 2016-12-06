<?php 

include_once "dbconnect.php";
include_once "query_builder.php";

//Get type (table to access)
$type = $_GET["type"];

//if there is no type, return error
if($type == null){
	echo "noTypeError";
}
else{
	//Get connection to database
	$conn = db_connect();
	//if there is no connection returned, die
	if($conn == null){
		die("Connection error");
	}
	//get the SQL Query for $type
	$sql = buildAdd($type);
	//Query the database
	$result = $conn->query($sql);
	//If a result is returned, signal a success
	if($result){
		echo "success";
	}
	//Otherwise signal a fail
	else{
		echo "fail";
	}
	//Disconnect from database
	db_disconnect($conn);
}

?>