<?php
require "db.php"
//4 tham số
// tham số 1: tên server
//tham số 2:tên user
//tham số 3: mật khẩu
//tham số 4: tên database
	$query="SELECT * FROM information";
	$data=mysqli_query($connect,$query);
	//1. tạo class User
	class User{
		function User($id,$username,$email,$password,$phone){
		$this->Id=$id;
		$this->UserName=$username;
		$this->Email=$email;
		$this->Password=$password;
		$this->Phone=$phone;
		}
	}
	//2. tạo mảng
	$mangUser=array();
	//3. thêm phần tử vào mảng
	while ($row=mysqli_fetch_assoc($data))
	{
		array_push($mangUser, new User($row['ID'],$row['UserName'],$row['Email'],$row['Password'],$row['Phone']));
	}
	//4. chuyen dinh dang cua mang -> JSON
	echo json_encode($mangUser);
?>