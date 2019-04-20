<!DOCTYPE html>
<html>
<head>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="css/style.css"/>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>QuickTapQuiz Creator</title>
</head>
<body>

    <form action="add-quiz.php" method="POST" enctype="multipart/form-data">
        <div id="quiz-creator">
            <h1>Quiz</h1>
            <input type="text" class=form-control name="quiz-name" placeholder="Quiz name" required>
            <hr>
            <div id="categories"></div>
            <a href="#" id="add-category" class="btn btn-danger">Add Category</a>
            <button id="generate-code" type="submit" class="btn btn-primary btn-lg btn-block">GENERATE CODE</button>
        </div>
    </form>

    <!-- SCRIPTS -->
	<script src="js/jquery-3.4.0.js"></script>
	<script src="js/app.js"></script>

</body>
</html>