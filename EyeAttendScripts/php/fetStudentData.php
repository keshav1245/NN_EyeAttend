<?php

include('credentials.php');
header('Content-Type:application/json');

$jsonArray = array();
$conn = mysqli_connect(DB_HOST,DB_USERNAME,DB_PASSWORD,DB_NAME);

if(!$conn){
	$jsonArray['error'] = "Cannot connect to Database! : ".htmlspecialchars($conn->error);
	die(json_encode($jsonArray));
}else{

	if(isset($_POST['table_name']) && isset($_POST['roll_number'])){

		$table_name = $_POST['table_name'];
		$table_name = str_replace('"', '', $table_name);
		$roll_number = mysqli_real_escape_string($conn,$_POST['roll_number']);

		if($stmt = $conn->prepare("SELECT `roll_no`, `name`, `branch`, `batch`, `email`, `ccet_email` FROM `{$table_name}` WHERE roll_no = ?")){

			if($stmt->bind_param("s",$roll_number)){

				if($stmt->execute()){

					$stmt_result = $stmt->get_result();
					if($stmt_result->num_rows == 1){

						$row = $stmt_result->fetch_assoc();

						$jsonArray['success'] = $row;
						die(json_encode($jsonArray));


					}else{
						$jsonArray['error'] = "Roll Number doesn't Exists!";
						die(json_encode($jsonArray));
					}

				}else{
					$jsonArray['error'] = "stmt Execute failed ! : ".htmlspecialchars($conn->error);
					die(json_encode($jsonArray));
				}

			}else{
				$jsonArray['error'] = "Bind param Failed ! : ".htmlspecialchars($conn->error);
				die(json_encode($jsonArray));
			}

		}else{
			$jsonArray['error'] = "stmt prepare failed ! : ".htmlspecialchars($conn->error);
			die(json_encode($jsonArray));
		}


	}else{
		$jsonArray['error'] = "Not all parameters recieved!";
		die(json_encode($jsonArray));
	}

}



?>