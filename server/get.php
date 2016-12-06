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
	$sql = buildGet($type);
	//Query the database
	$result = $conn->query($sql);
	//Array to store results of query
	$arr = array();
	//populate $arr with results of query
	while($row = $result->fetch_assoc()){
		array_push($arr, $row);
	}
	//Encode result array as JSON and return
	echo json_encode($arr);
	//Disconnect from database
	db_disconnect($conn);
}

?>