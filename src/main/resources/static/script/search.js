$(document).ready(function () {
    // $('#imageOrigin').zoomify();

    // $('.img').initViewer();

    //搜索按钮
    $('#onSearch').on('click', function () {
        //每次点击搜索按钮，搜索结果需清空
        clearResultBox();
        //获取图片
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
                    imageId: hex_md5(b64),
                    imageB64: b64,
                    groupId: groupId,
                    groupName: groupName
                };
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
    });


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
                    box.style.display = "block";
                    for (var i = 0; i < data.length; i++) {
                        box.append(
                            '<li>' +
                            '<img src="' + data[i] + '" id="image' + i + '">' +
                            '<div class="image_info clearfix">' +
                            '<div class="fl similarity">相似度：' + 100 % +'</div>' +
                            '<div class="search_again_button"><button class="btn btn-primary" onclick="searchAgainClick(this)">继续搜索</button></div>' +
                            '</div>' +
                            '</li>'
                        );
                        new Viewer(document.getElementById("image" + i));
                    }
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
        var close = document.createElement("i");
        close.setAttribute("id", "closeIcon");
        close.className = "fa fa-close fa-2x close_icon";
        document.getElementById("upImageBox").appendChild(close);
    }

    //去掉上传图片，恢复初始状态
    function clearUpImageBox() {
        //去掉图片
        document.getElementById("imageOrigin").style.display = "none";
        //去掉closeicon
        document.getElementById("closeIcon").remove();
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
    function searchAgainClick(e) {
        //检索图片放到上传图片框，搜索结果清空
        var url = e.parentElement.parentElement.previousElementSibling.getAttribute("src");
        showUpImage(url);
        clearResultBox();
        //ajax发送图片数据到后台
        getBase64(url)
            .then(function (base64) {
                var b64 = base64.split(",")[1];
                var selector = document.getElementById("group");
                var groupId = selector.options[selector.selectedIndex].value;
                var groupName = selector.options[selector.selectedIndex].text;
                var imageInfo = {
                    imageId: hex_md5(b64),
                    imageB64: b64,
                    groupId: groupId,
                    groupName: groupName
                };
                //发送
                sendData(imageInfo);
            }, function (err) {
                console.log(err);//打印异常信息
            });
    }

    //传入图片路径，返回base64
    function getBase64(img) {
        function getBase64Image(img, width, height) {//width、height调用时传入具体像素值，控制大小 ,不传则默认图像大小
            var canvas = document.createElement("canvas");
            canvas.width = width ? width : img.width;
            canvas.height = height ? height : img.height;

            var ctx = canvas.getContext("2d");
            ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
            var dataURL = canvas.toDataURL();
            return dataURL;
        }

        var image = new Image();
        image.crossOrigin = '';
        image.src = img;
        var deferred = $.Deferred();
        if (img) {
            image.onload = function () {
                deferred.resolve(getBase64Image(image));//将base64传给done上传处理
            };
            return deferred.promise();//问题要让onload完成后再return sessionStorage['imgTest']
        }
    };

});

