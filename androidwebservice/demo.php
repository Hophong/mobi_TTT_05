<?php
	class User{
		function User($username,$email,$password,$phone){
		$this->UserName=$username;
		$this->Email=$email;
		$this->Password=$password;
		$this->Phone=$phone;
		}
	}
	$mangUser=array();
	array_push($mangUser, new User("Nguyen Quang Phu","nqphu1998@gmail.com","05081998","0947421123"));
	array_push($mangUser, new User("Truong Ho Phong","nqphu1998@gmail.com","05081998","0947421123"));
	echo json_encode($mangUser);
?>