let categories;
let cards;
let user_id = 1;
let category_id;

$(document).ready(function () {
    showHeader();
    showCategories();

    $('#add_category_button').click(function () {
        let name = $('#add_category_name').val();
        $.ajax({
            type: "POST",
            url: 'category',
            data: {"name": name, "user_id": user_id},
            success: [function (result) {
                $('#add_category_name').val('');
                showCategories();
            }],
            error: [function () {
                alert("error");
            }]
        });
    });

    $('#category_table_content').on("click", "a", function () {
        let arr = $(this).attr('id').split('_');
        let id = parseInt(arr[1]);
        let value = arr[0];

        if (value === "delete") {
            deleteCategory(id);
        } else {
            $('#modalChangeCategory').modal('show');
            changeCategory(id);
        }
    })

    $('#card_table_content').on("click", "a", function() {
        let arr = $(this).attr('id').split('_');
        let id = parseInt(arr[2]);
        let value = arr[1];
        if(value === "delete"){
            deleteCard(id);
        } else{
            $('#modalChangeCard').modal('show');
            changeCard(id);
        }
    })


    document.addEventListener("dblclick", showCardsOnDblclick);

    $('#add_card_button').click(function () {
        let question = $('#add_card_question').val();
        let answer = $('#add_card_answer').val();
        $.ajax({
            type: "POST",
            url: 'card',
            data: { "category_id": category_id, "question": question, "answer": answer},
            success: [function (result) {
                $('#add_card_answer').val('');
                $('#add_card_question').val('');
                showCardsAgain();
            }],
            error: [function () {
                alert("error");
            }]
        });
    });

    $('#btn_log_out').click(function () {
        let cookie = $.cookie('user');
        if(cookie === undefined){
            alert('error111');
        }else {
            $.ajax({
                type: 'PUT',
                url: `user?id=${user}`,
                success: [function () {
                    $(location).attr('href', "http://localhost:8080/CardServer/login.html");
                }],
                error: [function (e) {
                    alert(JSON.stringify(e));
                }]
            });
        }
    });
})
function showHeader(){
    let name;
    let regDate;
    let day;
    let year;
    let month;
    $.ajax({
        type: "GET",
        url: `user?id=${user_id}`,
        success: [function (result) {
            name = result.data.login.toString();
            regDate = result.data.regDate;
            day = regDate[2].toString();
            month = regDate[1].toString();
            year = regDate[0].toString();
            let headerString = 'Hello, ' + name + '! You\'ve been with us since ' +
                day + '/' + month + '/' + year + '.'
            $("h3").html(headerString);
        }],
        error: [function (e){
            alert("error");
            alert(JSON.stringify(e));
        }]
    })
}
function showCategories() {
    $("tbody").html("");
    $.ajax({
        type: "GET",
        url: `category?user_id=${user_id}`,
        success: [function (result) {
            categories = result.data;
            for (let i = 0; i < categories.length; i++) {
                let markup = `<tr id="category_table_row_${categories[i].id}">` +
                    "<td>" + categories[i].id + "</td>" +
                    "<td>" + categories[i].name + "</td>" +
                    `<td style="text-align: center"><a href="#" id="change_${categories[i].id}"><i class="fa fa-edit" style="font-size:20px"></i></a></td>` +
                    `<td style="text-align: center"><a href="#" id="delete_${categories[i].id}"><i class="fa fa-trash" style="font-size:20px"></i></a></td></tr>`;
                $("#category_table_content").append(markup);
            }
            pagination();
        }],
        error: [function (e) {
            alert("error");
            alert(JSON.stringify(e));
        }]
    });
}
function prefillChangeCategoryWindow(id){
    let category = categories.find(category => category.id === id);
    let name = category.name;
    $('#change_category_name').val(name);
}
function prefillChangeCardWindow(id){
    let card = cards.find(card => card.id === id);
    let question = card.question;
    let answer = card.answer;
    $('#change_card_question').val(question);
    $('#change_card_answer').val(answer);
}
function changeCategory(id){
    prefillChangeCategoryWindow(id);
    $('#change_category_button').click(function (){
        let name = $('#change_category_name').val();
        $.ajax({
            type: "PUT",
            url: `category?id=${id}&name=${name}`,
            success: [function(result){
                showCategories();
            }],
            error: [function(e) {
                alert(JSON.stringify(e))
            }]
        })
    })
}
function changeCard(id){
    prefillChangeCardWindow(id);
    $('#change_card_button').click(function (){
        let question = $('#change_card_question').val();
        let answer = $('#change_card_answer').val();
        $.ajax({
            type: "PUT",
            url: `card?id=${id}&question=${question}&answer=${answer}`,
            success: [function(result){
                showCardsAgain();
            }],
            error: [function(e) {
                alert(JSON.stringify(e))
            }]
        })
    })
}
function deleteCategory(category_id) {
    $.ajax({
        type: "DELETE",
        url: `category?id=${category_id}`,
        success: [function (result) {
            showCategories();
        }],
        error: [function (e) {
            alert(JSON.stringify(e));
        }]
    });
}
function deleteCard(card_id){
    $.ajax({
        type: "DELETE",
        url: `card?id=${card_id}`,
        success: [function(result) {
            showCardsAgain();
        }],
        error: [function (e){
            alert(JSON.stringify(e));
        }]
    });
}
function showCardsOnDblclick(evt){
    $("#card_table_content").html("");
    evt = evt || window.event;
    evt = evt.target || evt.srcElement;

    let rowParent = evt.parentElement;
    while(rowParent.tagName !== 'TR'){
        rowParent = rowParent.parentElement;
    }
    let array = rowParent.id.split('_');
    if(array[0] === "category" && array[1] === "table") {
        category_id = parseInt(array[3]);
        $.ajax({
            type: "GET",
            url: `card?category_id=${category_id}`,
            success: [function (result) {
                cards = result.data;
                for (let i = 0; i < cards.length; i++) {
                    let markup = `<tr id="card_table_row_${cards[i].id}">` +
                        "<td>" + cards[i].id + "</td>" +
                        "<td>" + cards[i].question + "</td>" +
                        "<td>" + cards[i].answer + "</td>" +
                        `<td style="text-align: center"><a href="#" id="card_change_${cards[i].id}"><i class="fa fa-edit" style="font-size:20px"></i></a></td>` +
                        `<td style="text-align: center"><a href="#" id="card_delete_${cards[i].id}"><i class="fa fa-trash" style="font-size:20px"></i></a></td></tr>`;
                    $("#card_table_content").append(markup);
                }
                let finalString = `<tr id="final_card_row"> 
                            <td colspan="1">
                            <input type = button class="btn btn-outline-primary"
                             data-bs-toggle="modal" data-bs-target="#modalAddCard"
                             value='Add new card'>
                            </td>
                        </tr> `;
                $("#card_table_content").append(finalString);
            }],
            error: [function (e) {
                alert("error");
                alert(JSON.stringify(e));
            }]
        })
    }
}
function showCardsAgain(){
    $("#card_table_content").html("");
    $.ajax({
        type: "GET",
        url: `card?category_id=${category_id}`,
        success: [function (result) {
            cards = result.data;
            for (let i = 0; i < cards.length; i++) {
                let markup = `<tr id="card_table_row_${cards[i].id}">` +
                    "<td>" + cards[i].id + "</td>" +
                    "<td>" + cards[i].question + "</td>" +
                    "<td>" + cards[i].answer + "</td>" +
                    `<td style="text-align: center"><a href="#" id="card_change_${cards[i].id}"><i class="fa fa-edit" style="font-size:20px"></i></a></td>` +
                    `<td style="text-align: center"><a href="#" id="card_delete_${cards[i].id}"><i class="fa fa-trash" style="font-size:20px"></i></a></td></tr>`;
                $("#card_table_content").append(markup);
            }
            let finalString = `<tr id="final_card_row"> 
                            <td colspan="1">
                            <input type = button class="btn btn-outline-primary"
                             data-bs-toggle="modal" data-bs-target="#modalAddCard"
                             value='Add new card'>
                            </td>
                        </tr> `;
            $("#card_table_content").append(finalString);
        }],
        error: [function (e) {
            alert("error");
            alert(JSON.stringify(e));
        }]
    })
}