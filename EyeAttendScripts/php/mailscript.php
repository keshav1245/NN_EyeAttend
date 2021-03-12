<?php

	use PHPMailer\PHPMailer\PHPMailer;
	use PHPMailer\PHPMailer\Exception;
	require 'vendor/autoload.php';
	
	$jsonArrayM = array();

	function send_mail($to,$subject,$message,$otp){
		$mail = new PHPMailer(true);
		try {
			//Server settings
			$mail->isSMTP();
			$mail->Host = 'smtp.gmail.com';
			$mail->SMTPAuth = true;
			$mail->Username = '';//Change Email Id Here !
			$mail->Password = ''; //Change password Here !
			$mail->SMTPSecure = 'tls';
			$mail->Port = 587;
					 
			$mail->setFrom('zindagibachaodilse@gmail.com', 'EyeAttend Services');
			$mail->addAddress($to);
					 
			$mail->isHTML(true); 
			$mail->Subject = $subject;
			$mail->Body    = $message.$otp;
					 
			$mail->send();
					    //echo 'Message has been sent';
			return true;
		} catch (Exception $e) {
			
			$jsonArrayM['error'] = 'Mailer Error: ' . $mail->ErrorInfo;
			die(json_encode($jsonArrayM));
		}
	}

?>
