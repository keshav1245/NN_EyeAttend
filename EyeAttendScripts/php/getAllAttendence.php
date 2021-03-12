<?php

include('credentials.php');

$jsonArray = array();
$conn = mysqli_connect(DB_HOST,DB_USERNAME,DB_PASSWORD,DB_NAME) or die(json_encode('Unable to connect'));


if(isset($_GET['table_name'])){


	$table_name = $_GET['table_name'];
	$table_name = str_replace('"', '', $table_name);	


	if($stmt = $conn->prepare("SELECT * FROM `{$table_name}`")){

		// if($stmt->bind_param("s",$table_name)){

			if($stmt->execute()){

				header('Content-Type: text/csv; charset=utf-8');  
	      		header('Content-Disposition: attachment; filename=data.csv');  
	      		$output = fopen("php://output", "w");

				$stmt_result = $stmt->get_result();

				$col_names = $stmt_result->fetch_fields();

				$row = array();
				foreach ($col_names as $col) {
					$row[] = $col->name;
				}

				fputcsv($output, $row);

				while ($row = $stmt_result->fetch_assoc()) {
					fputcsv($output, $row);
				}

				fclose($output);

			}else{
				$jsonArray['error'] = "stmt execute failed !".htmlspecialchars($conn->error);
				die(json_encode($jsonArray));
			}


		// }else{
		// 	$jsonArray['error'] = "stmt bind param failed !".htmlspecialchars($conn->error);
		// 	die(json_encode($jsonArray));
		// }

	}else{
		$jsonArray['error'] = "stmt prepare failed !".htmlspecialchars($conn->error);
		die(json_encode($jsonArray));
	}


}else{
	$jsonArray['error'] = "No params recieved from Front-End!";
	die(json_encode($jsonArray));
}

?>
