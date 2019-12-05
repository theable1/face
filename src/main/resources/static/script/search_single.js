var searchAgainClick;
$(document).ready(function () {
    listGroup();//查询人脸库

    //loading
    function loading() {
        $("#imageBox").mLoading({
            text: "加载中...",//加载文字，默认值：加载中...
            icon: "",//加载图标，默认值：一个小型的base64的gif图片
            html: false,//设置加载内容是否是html格式，默认值是false
            content: "",//忽略icon和text的值，直接在加载框中显示此值
            mask: false//是否显示遮罩效果，默认显示
        });
    }

    //搜索按钮
    $('#onSearch').on('click', function () {
        //每次点击搜索按钮，搜索结果需清空
        clearResultBox();
        //获取图片
        var file = document.getElementById("upload");
        var img = file.files[0];
        var groupSelector = document.getElementById("group");
        if (judgeNull(file)) {
            var reader = new FileReader();
            reader.onload = function (e) {
                //获取base64编码
                var dataUrl = e.target.result;
                var fileName = img.name;
                var b64 = dataUrl.split(",")[1];
                // console.log(b64);
                //要传递的数据
                // var groupId = groupSelector.options[groupSelector.selectedIndex].value;
                var groupName = groupSelector.options[groupSelector.selectedIndex].text;
                var number = $('#number').val();
                var imageInfo = {};
                imageInfo.imageId = hex_md5(b64);
                imageInfo.imageB64 = b64;
                // imageInfo.groupId = groupId;
                imageInfo.groupName = groupName;
                imageInfo.imageNum = number;
                //发送
                sendData(imageInfo);
            };
            reader.readAsDataURL(img);
        } else {
            swal.fire("未选择图片！", "", "warning");
        }
    });

//选择文件更新图片
    $('#upload').on('change', function () {
        var element = document.getElementById("upload");
        if (judgeNull(element)) {
            var img = element.files[0];
            var reader = new FileReader();
            reader.onload = function (e) {
                var dataUrl = e.target.result;
                //展示图片
                showUpImage(dataUrl);
                //清除搜索结果框
                clearResultBox();
            };
            reader.readAsDataURL(img);
        } else {
            //去掉上传图片
            clearUpImageBox();
        }
    });

    $('#addIcon').on('click', function () {
        $('#upload').trigger('click');
    });

    $('#upImageBox').on('click', '#closeIcon', function () {
        clearUpImageBox();
        //清除搜索结果框
        clearResultBox();
    });

    function sendData(imageInfo) {
        loading();
        //数据放入数组
        var imageVOList = [];
        imageVOList.push(imageInfo);

        $.ajax({
            type: 'post',
            dataType: 'json',
            contentType: "application/json;charset=utf-8",
            url: '/search/process',
            data: JSON.stringify(imageVOList),
            success: function (data) {
                if (data.code != null || data.message != null) {
                    swal.fire(data.message, "", "warning");
                } else {
                    //隐藏空空如也的div
                    document.getElementById("noResult").style.display = "none";
                    //将查询结果图片放入div
                    var box = $("#showBox");
                    box.css('display', 'block');
                    var upNumber = data.length;
                    for (var i = 0; i < upNumber.length; i++) {
                        box.append(
                            '<li>' +
                            '<img src="' + upNumber[i].imageShowPath + '" id="image' + i + '">' +
                            '<div class="image_info clearfix">' +
                            '<div class="fl similarity">' + '相似度：' + (upNumber[i].distance.toFixed(2)) * 100 + '%' + '</div>' +
                            '<div class="search_again_button"><button class="btn btn-primary" onclick="searchAgainClick(this)">继续搜索</button></div>' +
                            '</div>' +
                            '</li>'
                        );
                        new Viewer(document.getElementById("image" + i));
                    }
                    //结束loading
                    $("#imageBox").mLoading("hide");
                }
            },
            error: function () {
                swal.fire("检索失败！", "", "error");
            }
        });
    }

//判断用户是否上传图片
    function judgeNull(file) {
        if (file.value == "") {
            return false;
        } else {
            return true;
        }
    }

//上传图片icon去掉，展示图片
    function showUpImage(url) {
        //icon去掉
        document.getElementById("addIcon").style.display = "none";
        //显示图片
        var imageOrigin = document.getElementById("imageOrigin");
        imageOrigin.style.display = "block";
        imageOrigin.setAttribute("src", url);
        new Viewer(imageOrigin);
        //显示closeicon
        var close = document.getElementById("closeIcon");
        close.classList.add("fa");
        close.classList.add("fa-close");
        close.classList.add("fa-2x");
        close.style.display = "inline-block";
    }

//去掉上传图片，恢复初始状态
    function clearUpImageBox() {
        //去掉input file选择的图片
        document.getElementById("upload").value = "";
        //去掉图片
        document.getElementById("imageOrigin").style.display = "none";
        //去掉closeicon
        var close = document.getElementById("closeIcon");
        close.classList.remove("fa");
        close.classList.remove("fa-close");
        close.classList.remove("fa-2x");
        close.style.display = "none";
        //显示icon
        document.getElementById("addIcon").style.display = "block";
    }

//清除检索结果，显示空空如也
    function clearResultBox() {
        document.getElementById("noResult").style.display = "block";
        var showBox = document.getElementById("showBox");
        showBox.style.display = "none";
        showBox.innerHTML = "";
    }

//再次搜索按钮
    searchAgainClick = function (e) {
        //检索图片放到上传图片框，搜索结果清空
        var url = e.parentElement.parentElement.previousElementSibling.getAttribute("src");
        showUpImage(url);
        clearResultBox();
        //ajax发送图片数据到后台
        var groupSelector = document.getElementById("group");
        var groupName = groupSelector.options[groupSelector.selectedIndex].text;
        var number = $('#number').val();
        var imageInfo = {};
        imageInfo.imageUrl = url;
        imageInfo.groupName = groupName;
        imageInfo.imageNum = number;
        //发送
        sendData(imageInfo);
    };

    $('#addButton').on('click', function () {
        var number = $('#number').val();
        $('#number').val(Number(number) + 1);
    });

    $('#subButton').on('click', function () {
        var number = $('#number').val();
        if (Number(number) > 10) {
            $('#number').val(Number(number) - 1);
        }
    });

    $('#number').on('change', function () {
        var number = $('#number').val();
        if (Number(number) < 10) {
            swal.fire("返回结果不可低于10张！", "", "warning");
            $('#number').val(10);
        }
    });

//查询可选择的人脸库
    function listGroup() {
        $.ajax({
            type: 'post',
            dataType: 'json',
            contentType: "application/json;charset=utf-8",
            url: '/search/list',
            success: function (data) {
                var group = $('#group');
                for (var i = 0; i < data.length; i++) {
                    group.append(
                        '<option>' + data[i].name + '</option>'
                    );
                }
            },
            error: function () {
                swal.fire("人脸库查询失败！", "", "error");
            }
        });
    }

})
;




