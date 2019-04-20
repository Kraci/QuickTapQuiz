(function($) {

    const addCategoryButton = $('#add-category');
    const categories = $('#categories');

    addCategoryButton.click(function (e) {
        e.preventDefault();
        var categoryHtml = `
            <div class="category">
                <h2>Category <a href="#" class="delete-group">x</a></h2>
                <input type="text" class="form-control" name="category[]" placeholder="Category name" required>
                <input type="hidden" value="0" name="category-count[]"/>
                <div class="questions"></div>
                <a href="#" class="add-question btn btn-primary">Add Question</a>
                <a href="#" class="add-bonus-question btn btn-success">Add Bonus Question</a>
                <hr>
            </div>
        `;
        categories.append(categoryHtml);
    });
    
    categories.on('click', '.add-question', function(e) {
        e.preventDefault();
        addQuestion($(this), false);
    });

    categories.on('click', '.add-bonus-question', function(e) {
        e.preventDefault();
        addQuestion($(this), true);
    });

    categories.on('click', '.delete-group', function(e) {
        e.preventDefault();
        $(this).parent().parent().remove();
    });

    categories.on('change', '.image-input', function(e) {
        $(this).next().val(true);
    });

    function addQuestion(button, isBonus) {
        var questions = button.prev();
        if (isBonus) {
           questions = questions.prev(); 
        }
        const categoriesCountObject = questions.prev();
        const categoriesCount = parseInt(categoriesCountObject.val());
        categoriesCountObject.val( categoriesCount + 1 );

        const questionHtml = `
            <div class="question">
                <h4>Question <a href="#" class="delete-group">x</a></h4>
                <label>Text</label>
                <textarea required class="form-control" name="question-text[]" rows="3"></textarea>
                <label>Hint</label>
                <textarea class="form-control" name="question-hint[]" rows="3"></textarea>
                <label>Value</label>
                <input class="form-control" type="number" name="question-value[]" step="100" value="0" min="0">
                <label>Image (optional)</label>
                <input class="form-control image-input" type="file" name="question-image[]" accept="image/png, image/jpeg">
                <input type="hidden" value="false" name="question-has-image[]"/>
                <input type="hidden" value="${isBonus}" name="question-bonus[]"/>
            </div>
        `;

        questions.append(questionHtml);

        if (isBonus) {
            button.remove();
        }
    }

}(jQuery));