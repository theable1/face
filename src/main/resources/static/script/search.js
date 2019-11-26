// $(document).ready(function(){
//
// });

function search(){
    //1、判断用户是否选择图片
    //2、已选择再ajax将图片发送到后端
    if (judge()){
        // $.ajax({
        //     type : 'post',
        //     dataType : 'text',
        //     url : '',
        //     data : ,
        //     success : function(data) {
        //
        //     }
        // });
    }else {
        alert("未选择图片！！")
    }
}

function judge(){
    //判断用户是否上传图片
    var img = document.getElementById("upload");
    if (img.value == ""){
        return false;
    }else {
        return true;
    }
}
