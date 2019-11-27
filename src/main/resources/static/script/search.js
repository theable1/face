// $(document).ready(function(){
//
// });

function search() {
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
            var b64 = e.target.result;

            //要传递的数据
            var image64 = b64.split(",")[1];
            var group = selector.options[selector.selectedIndex].value;
            var imageInfo = {
                imageId: hex_md5(img),
                imageB64: image64,
                group: group
            };
            sendData(imageInfo);
        }
        reader.readAsDataURL(img);
    }
}

function judgeNull(file) {
    //判断用户是否上传图片
    if (file.value == "") {
        alert("未选择图片！！");
        return false;
    } else {
        return true;
    }
}

function showImage(e) {
    if (judgeNull(e)){
        var img = e.files[0];
        var reader = new FileReader();
        reader.onload = function (e) {
            //获取base64编码
            var b64 = e.target.result;
            var imageOrigin = document.getElementById("image_origin");
            imageOrigin.setAttribute("src",b64);
        }
        reader.readAsDataURL(img);
    }
}

function sendData(imageInfo) {
    $.ajax({
        type: 'post',
        dataType: 'json',
        contentType: "application/json;charset=utf-8",
        url: '/search/process',
        data: JSON.stringify(imageInfo),
        success: function (data) {
            console.log(data);
            if (data == "success") {
                var imageResult = document.getElementById("image_result");

            } else {
            }
        },
        error: function () {
            alert("检索失败！")
        }
    });
}
