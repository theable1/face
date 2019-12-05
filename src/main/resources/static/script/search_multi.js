var searchAgainClick;
var deleteImageClick;
$(document).ready(function () {
    listGroup();//查询人脸库

    //loading
    function loading() {
        $("#imageBox").mLoading({
            text: "查询中...",//加载文字，默认值：加载中...
            icon: "",//加载图标，默认值：一个小型的base64的gif图片
            html: false,//设置加载内容是否是html格式，默认值是false
            content: "",//忽略icon和text的值，直接在加载框中显示此值
            mask: false //是否显示遮罩效果，默认显示
        });
    }

    //搜索按钮
    $('#onSearch').on('click', function () {
        //每次点击搜索按钮，搜索结果需清空
        clearResultBox();
        if (judgeNull) {
            //要传递的数据
            var imageVOList = [];
            var imageBox = $('#upImageBox').children();
            for (var i = 1; i < imageBox.length; i++) {
                var src = imageBox[i].children[1].children[0].src;
                var b64 = src.split(",")[1];
                // var groupId = groupSelector.options[groupSelector.selectedIndex].value;
                var groupSelector = document.getElementById("group");
                var groupName = groupSelector.options[groupSelector.selectedIndex].text;
                var number = $('#number').val();
                var imageInfo = {};
                imageInfo.imageId = hex_md5(b64);
                imageInfo.imageB64 = b64;
                // imageInfo.groupId = groupId;
                imageInfo.groupName = groupName;
                imageInfo.imageNum = number;
                imageVOList.push(imageInfo);
            }
            //发送
            sendData(imageVOList,false,null);
        } else {
            swal.fire("未选择图片！", "", "warning");
        }
    });

    //选择文件更新图片
    $('#upload').on('change', function () {
        if (judgeNull) {
            var imgs = $('#upload')[0].files;
            for (var i = 0; i < imgs.length; i++) {
                var reader = new FileReader();
                reader.onload = function (e) {
                    var dataUrl = e.target.result;
                    //展示图片
                    showUpImage(dataUrl);
                    //清除搜索结果框
                    clearResultBox();
                };
                reader.readAsDataURL(imgs[i]);
                $('#upImage' + i).parent().parent().append('<span style="none">' + imgs[i].name + '</span>');
            }

        } else {
            //去掉上传图片
            clearUpImageBox();
        }
    });

    deleteImageClick = function (e) {
        //移除上传图片
        e.parentElement.remove();
        //清空input选择的图片
        $('#upload').val("");
        //如果是最后一张图片就显示addicon,不是则更新序号
        var count = $('#upImageBox')[0].childElementCount;
        if (count == 1) {
            $('#addIcon').css('display', 'block');
        } else {
            var upImages = $('#upImageBox')[0].children;
            for (var i = 1; i < upImages.length; i++) {
                var number = upImages[i].firstElementChild;
                number.innerHTML = "img" + i;
            }
        }
    };

    //上传图片icon去掉，展示图片
    function showUpImage(url) {
        //icon去掉
        $('#addIcon').css('display', 'none');
        //判断图片是否已存在
        var flag = true;
        var imageBoxes = $('#upImageBox').children();
        for (var i = 1; i < imageBoxes.length; i++) {
            var src = imageBoxes[i].children[1].children[0].src;
            if (src == url) {
                swal.fire("该图片已添加，请重新选择！", "", "warning");
                flag = false;
                break;
            }
        }
        if (flag) {
            //显示图片
            var upImageBox = $('#upImageBox');
            //计算已有几张图片
            var count = upImageBox[0].childElementCount;
            upImageBox.append(
                '<div class="up_image">' +
                '<div class="up_image_title">img' + count + '</div>' +
                '<div class="up_image_img">' +
                '<img id="upImage' + count + '" src="' + url + '">' +
                '</div>' +
                '<i class="fa fa-close" onclick="deleteImageClick(this);"></i>' +
                '<input type="hidden" value="' + count + '">' +
                '</div>');
            new Viewer(document.getElementById('upImage' + count));
        }

    }

    $('#addIcon').on('click', function () {
        $('#upload').trigger('click');
    });

    $('#addImageButton').on('click', function () {
        $('#upload').trigger('click');
    });

    $('#clearAllButton').on('click', function () {
        $('#upImageBox').find('.up_image').remove();
        $('#upload').val("");
        $('#addIcon').css('display', 'block');
    });

    //imageVOList为发送数据 , flag为是否为继续搜索, number为继续搜索的图片所在列数
    function sendData(imageVOList,flag,number) {
        console.log(imageVOList);
        console.log(flag);
        console.log(number);

        loading();
        //数据放入数组
        $.ajax({
            type: 'post',
            dataType: 'json',
            contentType: "application/json;charset=utf-8",
            url: '/search/process',
            data: JSON.stringify(imageVOList),
            success: function (data) {
                for (var i = 0; i < data.length; i++) {
                    var imgResult = data[i];
                    var ul;
                    var num;
                    if (flag){//是继续搜索
                        num = number;
                        //删除原本那一列的li
                        ul = $('#result'+number);
                    }else {//不是继续搜索
                        //隐藏空空如也的div
                        $("#noResult").css('display', 'none');
                        //将一列查询结果图片放入div
                        var box = $("#showBox");
                        box.css('display', 'flex');
                        box.append(
                            '<div class="image_wrap">' +
                            '<div class="result_title">img' + (Number(i) + 1) + '查询结果</div>' +
                            '<ul id="result' + (Number(i) + 1) + '"></ul>' +
                            '</div>'
                        );
                        num = (Number(i) + 1);
                        ul = $('#result' + (Number(i) + 1));
                    }
                    if (imgResult instanceof Array) {
                        if (imgResult.length != 0) {
                            //添加图片
                            for (var j = 0; j < imgResult.length; j++) {
                                ul.append(
                                    '<li>' +
                                    '<img src="' + imgResult[j].imageShowPath + '" id="image' + num + j + '">' +
                                    '<div class="image_info clearfix">' +
                                    '<div class="fl similarity">' + '相似度：' + (Number(imgResult[j].distance).toFixed(2)) * 100 + '%' + '</div>' +
                                    '<div class="search_again_button"><button class="btn btn-primary" onclick="searchAgainClick(this)">继续搜索</button></div>' +
                                    '</div>' +
                                    '</li>'
                                );
                                new Viewer(document.getElementById("image" + num + j));
                            }
                        } else {
                            //找不到相似结果
                            ul.append(
                                '<div class="error">' +
                                '<i class="fa fa-bitbucket fa-4x"></i>' +
                                '<span>找不到相似图片</span>' +
                                '</div>'
                            );
                        }
                    } else {
                        //错误：识别不到人脸
                        ul.append(
                            '<div class="error">' +
                            '<i class="fa fa-eye-slash fa-4x"></i>' +
                            '<span>未识别出人脸</span>' +
                            '</div>'
                        );
                    }
                }
                //结束loading
                $("#imageBox").mLoading("hide");
            },
            error: function () {
                swal.fire("检索失败！", "", "error");
            }
        });
    }

//判断用户是否上传图片
    function judgeNull() {
        var count = $('#upImageBox')[0].childElementCount;
        if (count == 1) {
            return false;
        } else {
            return true;
        }
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
        //ajax发送图片数据到后台
        var groupName = $("#group option:selected").text();
        var number = $('#number').val();
        var url = $(e).parents("li")[0].children[0].src;
        var imageInfo = {};
        imageInfo.imageUrl = url;
        imageInfo.groupName = groupName;
        imageInfo.imageNum = number;
        var imageVOList = [];
        imageVOList.push(imageInfo);
        //再次搜索的图片所在列
        var col = $(e).parents("ul")[0].id;
        col = col.split("result")[1];
        //删除原本数据
        $(e).parents("ul").find("li").remove();
        //发送获取新数据
        sendData(imageVOList,true,col);
    };

    $('#addButton').on('click', function () {
        var number = $('#number').val();
        $('#number').val(Number(number) + 1);
    });

    $('#subButton').on('click', function () {
        var number = $('#number').val();
        if (Number(number) > 0) {
            $('#number').val(Number(number) - 1);
        }
    });

    $('#number').on('change', function () {
        var number = $('#number').val();
        if (Number(number) < 0) {
            swal.fire("返回结果不可小于1张！", "", "warning");
            $('#number').val(10);
        }
        if (Number(number) > 100) {
            swal.fire("返回结果不可大于100张！", "", "warning");
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
                        '<option selected="selected">' + data[i].name + '</option>'
                    );
                }
            },
            error: function () {
                swal.fire("人脸库查询失败！", "", "error");
            }
        });
    };

})
;




