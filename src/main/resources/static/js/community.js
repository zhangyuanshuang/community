/**
 * 提交回复
 */
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    if (!content){
        alert("回复内容不能为空，请输入内容");
        return;
    }
    $.ajax({
        type: "post",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId": questionId,
            "content": content,
            "type": 1
        }),
        success: function (response) {
            if (response.code === 200) {
                window.location.reload();
            } else {
                if (response.code === 2003) {
                    /*确认框*/
                    var isAccepted = confirm(response.message);
                    if (isAccepted){
                        /*打开新的链接*/
                        window.open("https://github.com/login/oauth/authorize?client_id=Iv1.2533d223159ef22a&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
                        window.localStorage.setItem("closeable","true");
                    }
                } else {
                    alert(response.message);
                }
            }
        },
        dataType: "json"
    });
}


/**
 * 二级评论
 */
function collapseComment() {

}