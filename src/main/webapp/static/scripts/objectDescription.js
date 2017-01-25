var nounForm = [
        {
            id: 'noun',
            title: "What's inside the box?",
            placeholder: "What is it?",
            fieldType: 'text',
            instruction: "In the image to the left, what is inside the blue box? For example, you could type bank, house, or sign"
        }
];

var descriptionForm = [
    {
        id: 'adjectives',
        title: 'How would you describe it?',
        fieldType: 'text',
        instruction: "Please enter words which describe the {{noun}}. For example, if this was a brick house, you could type large red brick"
    }
];

/***********************************************/
var questionBox;
var turkForm;

var returnData = [];

$(document).ready(function() {
    questionBox = $(".questionPart");
    turkForm = $(".turkForm");

    buildQuestion(nounForm);
});

function buildQuestion(questionJson) {
    var formFields = $("<div></div>");

    questionBox.empty();

    questionJson.forEach(function(field) {
        var qDiv = $("<div></div>");
        var title = $("<span class='title'>" + field.title + "</span>");
        var instruction = $("<span class='instruction'>" + field.instruction + "</span>");
        var input = $("<input type=" + field.fieldType + " name=" + field.id + " placeholder='" + field.placeholder + "' id=" + field.id + " />");
        qDiv.append([title, instruction, input]);
        formFields.append(qDiv);
    });

    var nextBtn = $('<input id="nextBtn" type="button" class="button blue nextBtn" value="Next"/>');
    nextBtn.on('click', nextQuestion);

    questionBox.append(formFields);
    questionBox.append(nextBtn);
}

function nextQuestion() {
    questionBox.find("input").each(function(index, input) {
        if (input.type === 'button') return;
        returnData.push({name: input.id, value: input.value});
    });

    showActivityIndicator();

    $.ajax({
       url: "objectdescription/next",
        method: "post",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(returnData),
        success: function(data) {
            if (data.status === 'complete') {
                return submitAndComplete();
            }

            buildQuestion(data.form);
        }
    });
}

function showActivityIndicator() {
    questionBox.empty();

    var activityIndicator = $('<div class="activityIndicator">Please wait...</div>');

    questionBox.append(activityIndicator);
}

function submitAndComplete() {
    turkForm.find("#description").val(JSON.stringify(returnData));
    turkForm.submit();
}