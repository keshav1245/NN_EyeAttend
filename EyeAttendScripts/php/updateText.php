<?php

include('credentials.php');
header("Content-Type:application/json");

$conn = mysqli_connect(DB_HOST,DB_USERNAME,DB_PASSWORD,DB_NAME);
$jsonArray = array();

if(!$conn){
	$jsonArray['error'] = "Unable to connect to Database : ".htmlspecialchars($conn->error);
	die(json_encode($jsonArray));
}else{
	if(isset($_POST['table_name']) && isset($_POST['attendence']) && isset($_POST['date']) && isset($_POST['students'])){

		$table_name = $_POST['table_name'];
		$table_name = str_replace('"', '', $table_name);
		$attendence = mysqli_real_escape_string($conn,$_POST['attendence']);
		$date = $_POST['date'];
		$date = str_replace('"', '', $date);
		$students = mysqli_real_escape_string($conn,$_POST['students']);


		$rolls = explode(",",$students);
		for ($i = 0; $i < count($rolls);$i=$i+1) {
			
			if($stmt = $conn->prepare("UPDATE `{$table_name}` SET `{$date}` = '{$attendence}' WHERE roll_no = ? ")){
				if($stmt->bind_param("s",$rolls[$i])){

					if($stmt->execute()){

						if($i == count($rolls)-1){
							$jsonArray['success'] = "Updation Successful !";
							die(json_encode($jsonArray));
						}


					}else{
						$jsonArray['error'] = "Execution failed ! : ".htmlspecialchars($conn->error);
						die(json_encode($jsonArray));
					}

				}else{
					$jsonArray['error'] = "Bind Param failed ! : ".htmlspecialchars($conn->error);
					die(json_encode($jsonArray));
				}
			}else{
				$jsonArray['error'] = "stmt prepare failed ! : ".htmlspecialchars($conn->error);
				die(json_encode($jsonArray));
			}
		}


	}else{
		$jsonArray['error'] = "Not all Params are available !";
		die(json_encode($jsonArray));
	}	
}




?>