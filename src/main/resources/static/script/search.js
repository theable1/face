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
        if (judgeType(img)) {
            var reader=new FileReader();
            reader.onload=function(e){
                var b64 = e.target.result;//获取base64编码
                // console.log(b64);
                var image64 = b64.split(",")[1];
                // console.log(image64);
                // console.log(hex_md5(img));
                var group = selector.options[selector.selectedIndex].value;
                var data = {
                    imageId: hex_md5(img),
                    imageB64: image64,
                    group: group
                };

                $.ajax({
                    type: 'post',
                    dataType: 'json',
                    contentType: "application/json;charset=utf-8",
                    url: '/search/process',
                    data: JSON.stringify(data),
                    success: function (data) {
                        if (data == "success") {
                            alert("上传成功");
                            console.log("上传成功");
                        } else {
                            alert("上传失败");
                            console.log("上传失败");
                        }
                    },
                    error: function () {
                        alert("上传失败");
                        console.log("上传失败");
                    }
                });
            }
            reader.readAsDataURL(img);
        }
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

function judgeType(file) {
    //判断是否是图片类型
    if (!/image\/\w+/.test(file.type)) {
        alert("只能选择图片!!");
        return false;
    }
    return true;
}


