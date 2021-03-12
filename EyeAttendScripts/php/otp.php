<?php

	header('Content-Type: application/json');
	include("../credentials.php");
	include("mailscript.php");
	$jsonArray = array();
	$conn = mysqli_connect(DB_HOST,DB_USERNAME,DB_PASSWORD,DB_NAME);

	if(!$conn){
		$jsonArray['error'] = "Error Connecting to Database".mysqli_connect_errno();
		die(print json_encode($jsonArray));
	}

	if(isset($_POST['email']) && isset($_POST['otp']) && isset($_POST['flag']) && isset($_POST['username'])){

		$flag = mysqli_real_escape_string($conn,$_POST['flag']);
		$email = mysqli_real_escape_string($conn,$_POST['email']);
		$otp = (int)mysqli_real_escape_string($conn,$_POST['otp']);
		$username = mysqli_real_escape_string($conn,$_POST['username']);

		switch ($flag) {
			case '0':

					$verified = "YES";

					$stmt = $conn->prepare("UPDATE `user_details` SET `verified`= ? WHERE `username` = ?");

					$stmt->bind_param("ss",$verified,$username);

					if($stmt->execute()){
						$jsonArray['success'] = "ACCOUNT VERIFICATION SUCCESSFUL !";

						die(print json_encode($jsonArray));
					}else{
						$jsonArray['error'] = "ACCOUNT VERIFICATION UN-SUCCESSFUL !";
						die(print json_encode($jsonArray));
					}
				break;
			
			case '1':
					$otp = rand(100000, 999999);
					$subject = 'Verify Your Email';
					$message = 'This is a verification email for proper authentification of your account. 
					    Your verification One Time Password(OTP) is : ';

					if(send_mail($email,$subject,$message,$otp)){
						$jsonArrayM['success'] = $otp;
						die(json_encode($jsonArrayM));
					}

				break;
		}

		


	}else{
		$jsonArray['error'] = "Data Not Submitted !";
		die(print json_encode($jsonArray));
	}


?>