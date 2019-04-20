<?php

require_once "../db/database.php";

$sql = "SELECT * FROM generated_quizzes";
$result = $conn->query($sql);

echo "ROWS: " . $result->num_rows . "<br><br>";

while( $row = $result->fetch_assoc() ) {
    echo $row['code'] . "<br>";
    echo "<pre>";
    echo $row['quiz'];
    echo "</pre>";
    echo "<br><br>";
}