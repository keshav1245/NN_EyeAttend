<?php

include('credentials.php');
header("Content-Type:application/json");

$jsonArray = array();

$conn = mysqli_connect(DB_HOST,DB_USERNAME,DB_PASSWORD,DB_NAME);

if(!$conn){

	$jsonArray['error'] = "Cannot connect to database ! : ".htmlspecialchars($conn->error);
	die(json_encode($jsonArray));

}else{

	$roll = $_POST['roll'];
	$result = $conn->query("SELECT * FROM batch_2017 WHERE roll_no='".$roll."'");

	$row = $result->fetch_array();
	echo $row['name'];



}




?>