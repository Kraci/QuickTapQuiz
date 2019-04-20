<?php

    require_once "../db/database.php";

    function json_response($message = null, $status = true) {
        header_remove();
        http_response_code(200);
        header("Cache-Control: no-transform,public,max-age=300,s-maxage=900");
        header('Content-Type: application/json');
        header('Status: 200');
        if (!$status) {
            return json_encode($message);
        }
        return $message;
    }

    if (isset($_GET['code'])) {

        $sql = "SELECT * FROM generated_quizzes WHERE code=" . $_GET['code'];
        $result = $conn->query($sql);

        if ($result->num_rows != 1) {
            $errorJSON = array(
                'name' => 'INVALID',
                'categories' => Array()
            );
            echo json_response($errorJSON, $status = false);
            return;
        }

        $row = $result->fetch_assoc();
        $quizJSON = $row["quiz"];

        echo json_response($quizJSON);

    }    

?>