$('#btn-go-login').click(function () {
        $(location).attr('href', "http://localhost:8080/CardServer/login.html");
    }
)


$('#btn-sign-up').click(function () {
        $.ajax({
            url: 'reg',
            method: 'POST',
            data: {"login" : $('#login').val(), "password" : $('#password').val()},
            success: [function (data) {
                $('.popup-fade').fadeIn();
            }],
            error: [function (e) {
                alert(JSON.stringify(e))
            }]
        })
    }
)

$('#btn-ok').click(function () {
    $('.popup-fade').fadeOut();
    $(location).attr('href', "http://localhost:8080/CardServer/index.html");
})