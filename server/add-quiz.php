<!DOCTYPE html>
<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <link type="text/css" rel="stylesheet" href="css/style.css"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
</head>
<body>

<?php

    require_once "db/database.php";

    $serverURL = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? "https" : "http") . "://$_SERVER[HTTP_HOST]";

    $quizName = $_POST['quiz-name'];
    $categories = $_POST['category'];
    $questionsInCategory = $_POST['category-count'];
    $questionTexts = $_POST['question-text'];
    $questionHints = $_POST['question-hint'];
    $questionValues = $_POST['question-value'];
    $questionImages = $_FILES['question-image'];
    $questionHasImages = $_POST['question-has-image'];
    $questionBonuses = $_POST['question-bonus'];
    $actualQuestionIndex = 0;
    $actualImageIndex = 0;
    $quizjson = array(
        'name' => $quizName,
        'categories' => array()
    );

    $sql = "INSERT INTO quizzes (name) VALUES ('{$quizName}')";
    $conn->query($sql);
    $quizID = $conn->insert_id;

    foreach ($categories as $index => $category) {

        $sql = "INSERT INTO categories (name, quiz_id) VALUES ('{$category}', '{$quizID}')";
        $conn->query($sql);
        $categoryID = $conn->insert_id;

        $questionsArray = array();

        $questionsCount = $questionsInCategory[$index];

        for ($i = 0; $i < $questionsCount; $i++) {
            
            $questionText = $questionTexts[$actualQuestionIndex];
            $questionHint = $questionHints[$actualQuestionIndex];
            $questionValue = $questionValues[$actualQuestionIndex];
            $questionHasImage = ($questionHasImages[$actualQuestionIndex] == "true") ? true : false;
            $questionBonus = $questionBonuses[$actualQuestionIndex];
            $questionBonusSqlBoolean = ($questionBonus == "true") ? 1 : 0;
            $questionImage = "";
            if ($questionHasImage) {
                $tmpFilePath = $questionImages['tmp_name'][$actualImageIndex];
                $questionImage = "./imgupload/" . $questionImages['name'][$actualImageIndex];
                move_uploaded_file($tmpFilePath, $questionImage);
                $questionImage = $serverURL . substr($questionImage, 1);
                $actualImageIndex++;
            }

            $sql = "INSERT INTO questions (text, hint, image) VALUES ('{$questionText}', '{$questionHint}', '{$questionImage}')";
            $conn->query($sql);
            $questionID = $conn->insert_id;

            $sql = "INSERT INTO category_question (category_id, question_id, question_value, question_bonus) VALUES ('{$categoryID}', '{$questionID}', '{$questionValue}', '{$questionBonusSqlBoolean}')";
            $conn->query($sql);

            array_push($questionsArray, array(
                'text' => $questionText,
                'hint' => $questionHint,
                'image' => $questionImage,
                'value' => $questionValue,
                'bonus' => $questionBonus
            ));

            $actualQuestionIndex++;

        }

        array_push($quizjson['categories'], array(
            'name' => $category,
            'questions' => $questionsArray
        ));

    }
    
    $digits = 4;
    $code = rand(pow(10, $digits-1), pow(10, $digits)-1);
    $quiz = json_encode($quizjson, JSON_UNESCAPED_UNICODE);

    $sql = "INSERT INTO generated_quizzes (code, quiz) VALUES ('{$code}', '{$quiz}')";
    $conn->query($sql);

    echo "<div id='quiz-creator' class='text-center'>";
    echo "<h1>{$code}</h1>";
    echo "<br>";
    echo "<a href='{$serverURL}' class='btn btn-primary btn-lg center'>Create new quiz</a>";
    echo "</div>";

?>

</body>
</html>