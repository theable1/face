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
            var group = selector.options[selector.selectedIndex].value;
            $.ajax({
                type: 'post',
                dataType: 'application/json',
                url: '/search/process',
                data: {
                    imageId: hex_md5(img),
                    imageB64: toBase64(img),
                    group: group
                },
                success: function (data) {
                    if (data == "success") {
                        console.log("上传成功");
                    } else {
                        console.log("上传失败");
                    }
                },
                error: function () {
                    console.log("上传失败");
                }
            });
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

function toBase64(file) {
    var base64Data = null;
    var reader = new FileReader()   //新建一个FileReader对象
    reader.readAsDataURL(file)   //将读取的文件转换成base64格式
    reader.onload = function (e) {
        base64Data = e.target.result;
    }
    return base64Data;
}
