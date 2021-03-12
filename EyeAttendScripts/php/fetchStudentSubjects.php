<?php

include('credentials.php');
header("Content-Type:application/json");

$jsonArray = array();
$conn = mysqli_connect(DB_HOST,DB_USERNAME,DB_PASSWORD,DB_NAME);

if(!$conn){
	$jsonArray['error'] = "Cannot Connect to Database : ".htmlspecialchars($conn->error);
	die(json_encode($jsonArray)); 
}else{


	if(isset($_POST['semester']) && $_POST['branch']){

		$sem = (int) mysqli_real_escape_string($conn,$_POST['semester']);
		$branch = mysqli_real_escape_string($conn,$_POST['branch']);

		if($stmt = $conn->prepare("SELECT subjects.course_code,subjects.course_title,subjects.branch,subjects.semester, professor_subjects.prof_id, professor.prof_name FROM `subjects` INNER JOIN professor_subjects INNER JOIN professor ON professor_subjects.prof_id = professor.prof_id WHERE subjects.semester = ? and subjects.branch = ? AND subjects.course_code = professor_subjects.subject_id  ORDER BY subjects.course_code ASC")){

			if($stmt->bind_param("is",$sem,$branch)){
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
					$jsonArray['error'] = "Execute Failed : ".htmlspecialchars($conn->error);
					die(json_encode($jsonArray));
				}
			}else{
				$jsonArray['error'] = "Bind param failed ! : ".htmlspecialchars($conn->error);
				die(json_encode($jsonArray));
			}

		}else{
			$jsonArray['error'] = "stmt prepare failed : ".htmlspecialchars($conn->error);
			die(json_encode($jsonArray));
		}

	}else{
		$jsonArray['error'] = "Not all Params recieved ! : ";
		die(json_encode($jsonArray));
	}


}

?>