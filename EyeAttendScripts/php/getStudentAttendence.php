<?php

include('credentials.php');
header("Content-Type:application/json");

$jsonArray = array();

$conn = mysqli_connect(DB_HOST,DB_USERNAME,DB_PASSWORD,DB_NAME);

if(!$conn){

	$jsonArray['error'] = "Cannot connect to database ! : ".htmlspecialchars($conn->error);
	die(json_encode($jsonArray));

}else{

	if(isset($_GET['table_name']) && isset($_GET['roll_no'])){

		$table_name = $_GET['table_name'];
		$table_name = str_replace('"', '', $table_name);
		$roll_no = $_GET['roll_no'];
		$roll_no = str_replace('"', '', $roll_no);


		if($stmt = $conn->prepare("SELECT * FROM $table_name WHERE roll_no = ? ")){

			if($stmt->bind_param("s",$roll_no)){

				if($stmt->execute()){

					$stmt_result = $stmt->get_result();

					if(mysqli_num_rows($stmt_result) == 1){

						$col_names = $stmt_result->fetch_fields();						

						header('Content-Type: text/csv; charset=utf-8');  
	      				header('Content-Disposition: attachment; filename=data.csv'); 
						
						$output = fopen("php://output", "w");
					

						$row = array();
						foreach ($col_names as $col) {
							$row[] = $col->name;
						}

						fputcsv($output, $row);

						$row = $stmt_result->fetch_assoc();
						fputcsv($output, $row);
						fclose($output);

					}else{
						$jsonArray['error'] = "Roll Number not found! Try for different Teacher(Same Subject)";
						die(json_encode($jsonArray));
					}


				}else{
					$jsonArray['error'] = "Execution Failed : ".htmlspecialchars($conn->error);
					die(json_encode($jsonArray));
				}

			}else{
				$jsonArray['error'] = "Bind param failed : ".htmlspecialchars($conn->error);
				die(json_encode($jsonArray));
			}

		}else{
			$jsonArray['error'] = "stmt prepare failed : ".htmlspecialchars($conn->error);
			die(json_encode($jsonArray));
		}

	}else{
		$jsonArray['error'] = "Not all Params Recieved from front-End !";
		die(json_encode($jsonArray));
	}


}




?>