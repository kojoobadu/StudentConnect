<?php 

include_once "dbconnect.php";

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
	//Start with blank SQL Query
	$sql = "";
	switch($type){
		case "members":
			//If getting info that relies on GroupMembers
			if($_GET["action"] == "get"){
				//If there is an email specified
				if($_GET["email"]){
					//Get all id's in GroupMembers (ID's of Groups) where email is a member
					$sql = "SELECT id FROM GroupMembers WHERE MemberEmail='" . $_GET["email"] . "'";
					//Query the database
					$result = $conn->query($sql);
					//array for storing Groups email is in
					$arr = array();
					while($row = $result->fetch_assoc()){
						//Get Group info where GroupId is id in $result
						$sql = "SELECT * FROM Groups WHERE GroupId='" . $row["id"] . "'";
						//Query database
						$result2 = $conn->query($sql);
						//Add Group to $arr
						array_push($arr, $result2->fetch_assoc());
					}
					//Encode Group array as JSON and return
					echo json_encode($arr);
				}
				//If an id is specified
				else if($_GET["id"]){
					//Get Members in Group with GroupId id
					$sql = "SELECT MemberEmail FROM GroupMembers WHERE id='" . $_GET["id"] . "'";
					//Query database
					$result = $conn->query($sql);
					//Array for storing $result
					$arr = array();
					//Populate array with results
					while($row = $result->fetch_assoc()){
						array_push($arr, $row);
					}
					//Encode $arr as JSON and return
					echo json_encode($arr);
				}
			}
			//If adding a GroupMember
			else if($_GET["action"] == "add"){
				//Add GroupMember in GroupMembers table with specified values in get
				$sql = "INSERT INTO GroupMembers(id, MemberEmail) VALUES('" . $_GET["id"] . "', '" . $_GET["email"] . "')";
				//Query database
				$result = $conn->query($sql);
				//If result returned, signal a success
				if($result){
					echo "success";
				}
				//Otherwise, signal a fail
				else{
					echo "fail";
				}
			}
			break;
	}
	//Disconnect from database
	db_disconnect($conn);
}

?>