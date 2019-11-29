$(document).ready(function () {
    $('.upImage').zoomify();



    $('#onSearch').on('click', function () {
        //1、判断用户是否选择图片
        //2、判断用户选择文件是否为图片类型
        //3、已选择再ajax将图片发送到后端
        var file = document.getElementById("upload");
        var img = file.files[0];
        var selector = document.getElementById("group");
        if (judgeNull(file)) {
            var reader = new FileReader();
            reader.onload = function (e) {
                //获取base64编码
                var dataUrl = e.target.result;
                var fileName = img.name;
                var b64 = dataUrl.split(",")[1];
                //要传递的数据
                var groupId = selector.options[selector.selectedIndex].value;
                var groupName = selector.options[selector.selectedIndex].text;
                var imageInfo = {
                    imageId: hex_md5(img),
                    imageB64: b64,
                    groupId: groupId,
                    groupName: groupName,
                    fileName: fileName
                };
                sendData(imageInfo);
            }
            reader.readAsDataURL(img);
        }
    })

    function judgeNull(file) {
        //判断用户是否上传图片
        if (file.value == "") {
            swal.fire("未选择图片！", "", "warning");
            return false;
        } else {
            return true;
        }
    }

    $('#upload').on('change', function () {
        var e = document.getElementById("upload");
        if (judgeNull(e)) {
            var img = e.files[0];
            var reader = new FileReader();
            reader.onload = function (e) {
                var dataUrl = e.target.result;
                var imageOrigin = document.getElementById("imageOrigin");
                //box背景图片去掉
                var box = document.getElementById("origin");
                box.style.background = "none";
                //显示图片
                imageOrigin.setAttribute("src", dataUrl);
                //显示结果为空空如也,检索结果div去除
                document.getElementById("noResult").style.display = "block";
                document.getElementById("showBox").innerHTML = "";
            }
            reader.readAsDataURL(img);
        }
    })

    function sendData(imageInfo) {
        $.ajax({
            type: 'post',
            dataType: 'json',
            contentType: "application/json;charset=utf-8",
            url: '/search/process',
            data: JSON.stringify(imageInfo),
            success: function (data) {
                if (data.code != null || data.message != null) {
                    swal.fire(data.message, "", "warning");
                } else {
                    //隐藏空空如也的div
                    document.getElementById("noResult").style.display = "none";
                    //将查询结果图片放入div
                    var box = $('#showBox');
                    for (var i = 0; i < data.length; i++) {
                        box.append('<li>' +
                                        '<img src="' + data[i] + '" class="zoomify" alt="">' +
                                    '</li>'
                        );
                    }
                }
                // $('.zoomify').renderViewer();
            },
            error: function () {
                swal.fire("检索失败！", "", "error");
            }
        });
    }



});