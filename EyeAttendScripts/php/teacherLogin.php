<?php

header("Content-Type: application/json");
include('credentials.php');
include("mailscript.php");
$conn = mysqli_connect(DB_HOST,DB_USERNAME,DB_PASSWORD,DB_NAME);
$jsonArray = array();

if(!$conn){
	$jsonArray['error'] = "Error Connecting with DB";
	die(json_encode($jsonArray));
}else{

	if(!isset($_POST['govt_id'])){
		$jsonArray['error'] = "No Data Recieved from Frontend!";
		die(json_encode($jsonArray));
	}else{

		$govt_id = mysqli_real_escape_string($conn,$_POST['govt_id']);

		if($stmt = $conn->prepare("SELECT `prof_email` FROM `professor` WHERE `prof_id` = ?")){

			if($stmt->bind_param("s",$govt_id)){

				if($stmt->execute()){

					$stmt_result = $stmt->get_result();

					$row = $stmt_result->fetch_assoc();

					if(empty($row)){
						$jsonArray['error'] = "Account Does not Exist !";
						die(json_encode($jsonArray));
					}

					$email = $row['prof_email'];

					$otp = rand(100000, 999999);
					$subject = 'Verify Your Email';
					$message = 'This is a verification email for proper authentification of your account. 
					    Your verification One Time Password(OTP) is : ';

					if(send_mail($email,$subject,$message,$otp)){
						$jsonArray['success'] = $otp;
						die(json_encode($jsonArray));
					}else{
						$jsonArray['error'] = "Send Mail Failed : ".htmlspecialchars($conn->error);
						die(json_encode($jsonArray));
					}

				}else{
					$jsonArray['error'] = "Execution Failed : ".htmlspecialchars($conn->error);
					die(json_encode($jsonArray));
				}


			}else{
				$jsonArray['error'] = "Bind Param Failed : ".htmlspecialchars($conn->error);
				die(json_encode($jsonArray));
			}


		}else{
			$jsonArray['error'] = "prepare stmt failed : ".htmlspecialchars($conn->error);
			die(json_encode($jsonArray));
		}


	}

}

$conn->close();

?>