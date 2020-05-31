/**
 * 提交回复
 */
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2target(questionId, 1, content);
}

function comment2target(targetId, type, content) {
    if (!content) {
        alert("回复内容不能为空，请输入内容");
        return;
    }
    $.ajax({
        type: "post",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code === 200) {
                window.location.reload();
            } else {
                if (response.code === 2003) {
                    /*确认框*/
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        /*打开新的链接*/
                        window.open("https://github.com/login/oauth/authorize?client_id=Iv1.2533d223159ef22a&redirect_uri=http://47.115.13.75/callback&scope=user&state=1");
                        window.localStorage.setItem("closeable", "true");
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
 * @param e
 */
function comment(e) {
    //获取id
    var commentId = e.getAttribute("data-id");
    //获取回复二级评论的内容
    var content = $("#input-" + commentId).val();
    comment2target(commentId, 2, content);
}

/**
 * 展开二级评论
 */
function collapseComment(e) {
    //获取评论的id
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);


    // comments.toggleClass("in"); 自动添加或删除in class

    //获取当前二级评论状态
    var collapse = e.getAttribute("data-collapse");

    if (collapse) {
        //折叠二级评论
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        var subCommentContainer = $("#comment-" + id);
        // console.log(subCommentContainer);
        // console.log(subCommentContainer.children().length);
        if (subCommentContainer.children().length !== 1) {
            //展开二级评论
            comments.addClass("in");
            //标记二级评论展开状态
            e.setAttribute("data-collapse", "in");
            e.classList.add("active");
        } else {
            //渲染回复二级评论的评论
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data.reverse(), function (index, comment) {

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "text": comment.user.name
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class": "pull-right",
                        "html": moment(comment.gmtCreate).format('YYYY-MM-DD')
                    })));

                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaElement = $("<div/>", {
                        "class": "media"
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement);

                    subCommentContainer.prepend(commentElement);
                });
                //展开二级评论
                comments.addClass("in");
                e.setAttribute("data-collapse", "in");
                e.classList.add("active");
            });
        }
    }
}

/**
 * 标签库
 * @param value
 */
function selectTag(e) {
    var value = e.getAttribute("data-tag");
    //获取输入框的值
    var previous = $("#tag").val();
    console.log(previous.split(","));
    //indexof 可判断字符串首次出现的位置 没有出现返回-1
    if (previous.split(",").indexOf(value) === -1) {
        if (previous) {
            $("#tag").val(previous + ',' + value);
        } else {
            $("#tag").val(value);
        }
    }
}

/**
 * 点击展示或隐藏标签
 */
function showSelectTag() {
    $("#select-tag").show();
}

/**
 * 删除功能
 * @param e
 */
function doLayer(e) {
    layer.confirm('你确定要删除问题吗?', {icon: 3, title: '提示', btn: ['确定', '取消']}
        , function (ind) {
            doDelete(e);
            layer.close(ind);
        }, function (inds) {
            layer.close(inds);
        });
}
function doDelete(e) {
    var id = e.getAttribute("data-doDelete");
    console.log(id);
    $.ajax({
        type: "POST",
        url: "/question/delete",
        data: {
            id: id
        },
        success: function () {
            window.location.href = "/profile/question";
        }
    });
}