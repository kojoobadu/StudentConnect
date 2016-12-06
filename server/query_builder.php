<?php

include_once "password_decode.php";

//Builds an SQL SELECT query for the type specified by $type, using associated get variables
//Check existence of get variables to determine SQL query if there are variants
function buildGet($type){
	switch($type){
		case "account":
			//decode the password in the URL before using in query
			$password = password_decode($_GET["password"]);
			//Get all information about User in Users Table except for UserEmail and Password (used to find User)
			return "SELECT FirstName, LastName, Description, EventName FROM Users 
			WHERE UserEmail='" . $_GET["userName"] . "' and Password='" . $password . "'";
		case "event":
			//Gets all Events in Events table
			$sql = "SELECT * FROM Events";
			//If an id is specified, get all Events id is the owner of
			if($_GET["id"]){
				$sql = $sql . " WHERE OwnerEmail='" . $_GET["id"] . "'";
			}
			return $sql;
		case "book":
			//Gets all Books in Books table
			$sql = "SELECT * FROM Books";
			//If an id is specified, get all Books id is the owner of
			if($_GET["id"]){
				$sql = $sql . " WHERE Owner='" . $_GET["id"] . "'";
			}
			//If an isbn is specified, get all Books with that ISBN
			else if ($_GET["isbn"]){
				$sql = $sql . " WHERE ISBN='" . $_GET["isbn"] . "'";
			}
			return $sql;
		case "notification":
			//Get all Notifications for user id in order with most recent first
			return "SELECT * FROM Notifications WHERE Recipient='" . $_GET["id"] . "' ORDER BY TimeSent DESC";
		case "group":
			//Get all Groups in Groups table
			$sql = "SELECT * FROM Groups";
			//If a tag is specified, get all Groups with that Tag
			if($_GET["tag"]){
				$sql = $sql . " WHERE Tag='" . $_GET["tag"] . "'";
			}
			return $sql;
		case "bookcomment":
			//Get all BookComments on Book with specified id ordered with most recent first
			return "SELECT * FROM BookComments WHERE BookID='" . $_GET["id"] . "' ORDER BY TimePosted DESC";
		case "groupcomment":
			//Get all GroupComments on Group with specified id ordered with most recent first
			return "SELECT * FROM GroupComments WHERE GroupID='" . $_GET["id"] . "' ORDER BY TimePosted DESC";
		default:
			return "";
	}
}
//Builds an SQL INSERT query for the type specified by $type, using associated get variables
function buildAdd($type){
	switch($type){
		case "account":
			//decode the password in the URL before using in query
			$password = password_decode($_GET["password"]);
			//Add a new User in the Users table with the specified variables in get
			return "INSERT INTO Users(FirstName, LastName, UserEmail, Description, EventName, Password) 
					VALUES('" . $_GET["firstname"] . "', '" . $_GET["lastname"] . "', '" . $_GET["id"] . "', '" 
					. $_GET["desc"] . "', 'none', '" . $password . "')";
		case "event":
			//Add a new Event in the Events table with the specified variables in get
			return "INSERT INTO Events(EventName, EventDescription, OwnerEmail, Ranking) 
					VALUES('" . $_GET["name"] . "', '" . $_GET["desc"] . "', '" . $_GET["owner"] . "', " . $_GET["voting"] . ")";
		case "book":
			//Add a new Book in the Books table with the specified variables in get
			return "INSERT INTO Books(Description, BookName, ISBN, OwnerEmail) 
					VALUES('" . $_GET["desc"] . "', '" . 
					$_GET["name"] . "', '" . $_GET["isbn"] . "', '" . $_GET["owner"] . "')";
		case "notification":
			//Add a new Notification in the Notifications table with the specified variables in get and TimeSent as the current time
			return "INSERT INTO Notifications(NotificationText, Recipient, TimeSent, Tag, Identifier, Sender) 
					VALUES('" . $_GET["text"] . "', '" . $_GET["id"] . "', " . time() . ", '" . $_GET["tag"] . "', '" . $_GET["identifier"] . "', '" . $_GET["sender"] . "')";
		case "group":
			//Add a new Group in the Groups table with the specified variables in get
			return "INSERT INTO Groups(GroupName, GroupOwnerEmail, Description, Tag) 
					VALUES('" . $_GET["name"] . "', '" . $_GET["owner"] . "', '" . $_GET["desc"] . "', '" . $_GET["tag"] . "')";
		case "bookcomment":
			//Add a new BookComment in the BookComments table with the specified variables in get and TimePosted as the current time
			return "INSERT INTO BookComments(CommentText, Poster, BookID, TimePosted) 
					VALUES('" . $_GET["text"] . "', '" . $_GET["poster"] . "', '" . $_GET["id"] . "', " . time() . ")";
		case "groupcomment":
			//Add a new GroupComment in the GroupComments table with the specified variables in get and TimePosted as the current time
			return "INSERT INTO GroupComments(CommentText, Poster, GroupID, TimePosted) 
					VALUES('" . $_GET["text"] . "', '" . $_GET["poster"] . "', '" . $_GET["id"] . "', " . time() . ")";
		default:
			return "";
	}
}
//Builds an SQL UPDATE query for the type specified by $type, using associated get variables
//Check existence of get variables to determine SQL query if there are variants
function buildEdit($type){
	switch($type){
		case "account":
			//decode the password in the URL before using in query
			$password = password_decode($_GET["password"]);
			//Update the User in Users table with OwnerEmail id using the specified get variables
			return "UPDATE Users SET FirstName='" . $_GET["firstname"] . "', LastName='" . $_GET["lastname"] . "', Description='" . $_GET["desc"] . 
				"', EventName='" . $_GET["event"] . "', Password='" . $password . "' WHERE UserEmail='" . $_GET["id"] . "'";
		case "event":
			//If voting is specified, update only the Ranking field in Events with EventName name and OwnerEmail owner
			if($_GET["voting"]){
				return "UPDATE Events SET Ranking=" . $_GET["voting"] . " WHERE EventName='" . $_GET["name"] . "' and OwnerEmail='" . $_GET["owner"] . "'";
			}
			//If voting not specified, update EventName and EventDescription fields in Events with EventName oldname and OwnerEmail owner
			else{
				//update event name in Users table before updating it in Events table
				return "UPDATE Users SET EventName='" . $_GET["name"] . "' WHERE EventName='" . $_GET["oldname"] . "';
						UPDATE Events SET EventName='" . $_GET["name"] . "', EventDescription='" . $_GET["desc"] . "' 
						WHERE EventName='" . $_GET["oldname"] . "' and OwnerEmail='" . $_GET["owner"] . "'";
			}
		case "book":
			//Update the BookName, Description, and ISBN of Book in Books table with BookID id
			return "UPDATE Books SET BookName='" . $_GET["name"] . "', Description='" . $_GET["desc"] . "', ISBN='" . $_GET["isbn"] . 
					"' WHERE BookID='" . $_GET["id"] . "'";
		case "group":
			//Update the GroupName, Description, and Tag of Group in Groups table with GroupId id
			return "UPDATE Groups SET GroupName='" . $_GET["name"] . "', Description='" . $_GET["desc"] . "', Tag='" . $_GET["tag"] . "' 
					WHERE GroupID='" . $_GET["id"] . "'";
		default:
			return "";
	}
}
//Builds an SQL DELETE query for the type specified by $type, using associated get variables
function buildDelete($type){
	switch($type){
		case "event":
			//If a User is in the Event being deleted, set their EventName to null, then delete Event in Events table with EventName name and OwnerEmail owner
			return "UPDATE Users SET EventName=null WHERE EventName='" . $_GET["name"] . "';
					DELETE FROM Events WHERE EventName='" . $_GET["name"] . "' and OwnerEmail='" . $_GET["owner"] . "'";
		case "book":
			//Delete all BookComments associated with a Book and then delete the Book with BookID id
			return "DELETE FROM BookComments WHERE BookID='" . $_GET["id"] . "'; 
					DELETE FROM Books WHERE BookID='" . $_GET["id"] . "'";
		case "notification":
			//Delete a Notification in Notifications table with NotificationText text, Recipient id, and TimeSent time
			return "DELETE FROM Notifications WHERE NotificationText='" . $_GET["text"] . "' and Recipient='" . $_GET["id"] . "' and TimeSent=" . $_GET["time"];
		case "group":
			//Delete all GroupComments and GroupMembers associated with a Group and then delete the Group with GroupID id
			return "DELETE FROM GroupMembers WHERE id='" . $_GET["id"] . "'; 
					DELETE FROM GroupComments WHERE GroupID='" . $_GET["id"] . "'; 
					DELETE FROM Groups WHERE GroupID='" . $_GET["id"] . "'";
		case "comment":
			//check which type of comment to delete with commenttype
			switch($_GET["commenttype"]){
				case "book":
					//Delete BookComment with BookId id, CommentText text, and Poster poster
					return "DELETE FROM BookComments WHERE BookID='" . $_GET["id"] . "' and CommentText='" . $_GET["text"] . "' and Poster='" . $_GET["poster"] . "'";
				case "group":
					//Delete GroupComment with GroupId id, CommentText text, and Poster poster
					return "DELETE FROM GroupComments WHERE GroupID='" . $_GET["id"] . "' and CommentText='" . $_GET["text"] . "' and Poster='" . $_GET["poster"] . "'";
			}
		default:
			return "";
	}
}

?>
