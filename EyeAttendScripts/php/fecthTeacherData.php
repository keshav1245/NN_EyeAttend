<?php

header("Content-Type: application/json");
include('credentials.php');

$jsonArray = array();
$conn = mysqli_connect(DB_HOST,DB_USERNAME,DB_PASSWORD,DB_NAME);

if(!$conn){
	$jsonArray['error'] = "Connection to DB Unsuccessful : ".htmlspecialchars($conn->error);
	die(json_encode($jsonArray));
}else{

	if(isset($_POST['govt_id'])){

		$govt_id = mysqli_real_escape_string($conn,$_POST['govt_id']);

		if($stmt = $conn->prepare('SELECT * FROM `professor` WHERE `prof_id` = ?')){

			if($stmt->bind_param("s",$govt_id)){

				if($stmt->execute()){

					$stmt_result = $stmt->get_result();

					$row = $stmt_result->fetch_assoc();
					
					$jsonArray['success'] = $row;
					die(json_encode($jsonArray));
				



				}else{
					$jsonArray['error'] = "Query execution failed : ".htmlspecialchars($conn->error);
					die(json_encode($jsonArray));
				}


			}else{
				$jsonArray['error'] = "stmt bind param failed : ".htmlspecialchars($conn->error);
				die(json_encode($jsonArray));
			}


		}else{

			$jsonArray['error'] = "stmt prepare failed : ".htmlspecialchars($conn->error);
			die(json_encode($jsonArray));

		}


	}else{
		$jsonArray['error'] = "No data from FrontEnd !";
		die(json_encode($jsonArray)); 
	}


}

$conn->close();

?>