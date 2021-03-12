 <?php

header("Content-Type: application/json");

include('credentials.php');

$jsonArray = array();
$conn = mysqli_connect(DB_HOST,DB_USERNAME,DB_PASSWORD,DB_NAME);

if(!$conn){
	$jsonArray['error'] = "Cannot connect with Database !".htmlspecialchars(mysqli_connect_errno());
	die(json_encode($jsonArray));
}else{

	if(!isset($_POST['govt_id'])){
		$jsonArray['error'] = "No Data from Front End!";
		die(json_encode($jsonArray));
	}else{

		$govt_id = mysqli_real_escape_string($conn,$_POST['govt_id']);

		if($stmt = $conn->prepare("SELECT subjects.* FROM `professor_subjects` INNER JOIN subjects ON professor_subjects.subject_id = subjects.course_code WHERE professor_subjects.prof_id = ?")){

			if($stmt->bind_param("s",$govt_id)){

				if($stmt->execute()){

					$stmt_result = $stmt->get_result();


					if(mysqli_num_rows($stmt_result) >= 1){
						$jsonArrayData = array();
						foreach ($stmt_result as $row) {
							$jsonArrayData [] = $row;
						}

						$jsonArray['success'] = $jsonArrayData;

						die(json_encode($jsonArray));

					}else{
						$jsonArray['error'] = "Currently you have no Subjects...!!";
						die(json_encode($jsonArray));
					}


				}else{
					$jsonArray['error'] = "execution failed : ".htmlspecialchars($conn->error);
					die(json_encode($jsonArray));
				}


			}else{
				$jsonArray['error'] = "stmt bind failed !".htmlspecialchars($conn->error);
				die(json_encode($jsonArray));
			}

		}else{
			$jsonArray['error'] = "stmt prepare failed !".htmlspecialchars($conn->error);
			die(json_encode($jsonArray));
		}


	}

}

$conn->close();

?>