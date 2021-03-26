<?php


header("Content-Type: application/json");
include("credentials.php");

define('UPLOAD_PATH', '../images/');
 
 //connecting to database 
 $conn = new mysqli(DB_HOST,DB_USERNAME,DB_PASSWORD,DB_NAME) or die(json_encode('Unable to connect'));
 
 
 //An array to display the response
 $response = array();
 
 //if the call is an api call 
 if(isset($_GET['apicall'])){
 
 //switching the api call 
 switch($_GET['apicall']){
 
 //if it is an upload call we will upload the image
 case 'uploadpic':
 
 //first confirming that we have the image and tags in the request parameter
 if(isset($_FILES['pic']['name']) && isset($_POST['tags']) && isset($_POST['branch']) && isset($_POST['batch'])){
 

 //uploading file and storing it to database as well 
 try{
 move_uploaded_file($_FILES['pic']['tmp_name'], UPLOAD_PATH . $_FILES['pic']['name']);
 $stmt = $conn->prepare("INSERT INTO images (image, tags) VALUES (?,?)");
 $stmt->bind_param("ss", $_FILES['pic']['name'],$_POST['tags']);
 if($stmt->execute()){
 $response['error'] = false;
 $response['message'] = 'File uploaded successfully';

 $filename = $_FILES['pic']['name'];
 $branch = mysqli_real_escape_string($conn,$_POST['branch']);
 $batch = mysqli_real_escape_string($conn,$_POST['batch']);

 $output = exec("/home/zukayu/anaconda3/bin/python3 /opt/lampp/htdocs/EyeAttendScripts/vectorized.py -i /opt/lampp/htdocs/EyeAttendScripts/images/$filename -br $branch -ba $batch  2>&1");


 $response['script_out'] = $output;

 }else{
 throw new Exception("Could not upload file");
 }
 }catch(Exception $e){
 $response['error'] = true;
 $response['message'] = 'Could not upload file';
 }
 
 }else{
 $response['error'] = true;
 $response['message'] = "Required params not available";
 }
 
 break;
 
 default: 
 $response['error'] = true;
 $response['message'] = 'Invalid api call';
 }
 
 }else{
 header("HTTP/1.0 404 Not Found");
 echo "<h1>404 Not Found</h1>";
 echo "The page that you have requested could not be found.";
 exit();
 }
 
 //displaying the response in json 
 header('Content-Type: application/json');
 echo json_encode($response);

?>