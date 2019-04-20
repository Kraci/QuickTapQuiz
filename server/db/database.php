<?php

$user = '';
$password = '';
$db = '';
$host = '';
$port = 0;

$conn = mysqli_init();
$success = mysqli_real_connect(
    $conn, 
    $host, 
    $user, 
    $password, 
    $db,
    $port
);

?>