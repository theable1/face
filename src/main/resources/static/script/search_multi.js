var searchAgainClick;
var deleteImageClick;
$(document).ready(function () {
    $.fn.datetimepicker.dates['zh-CN'] = {
        days: ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"],
        daysShort: ["周日", "周一", "周二", "周三", "周四", "周五", "周六", "周日"],
        daysMin:  ["日", "一", "二", "三", "四", "五", "六", "日"],
        months: ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"],
        monthsShort: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
        today: "今天",
        suffix: [],
        meridiem: ["上午", "下午"]
    };
    listGroup();//查询人脸库
    date();

    //loading
    function loading() {
        $("#imageBox").mLoading({
            text: "查询中...",//加载文字，默认值：加载中...
            icon: "",//加载图标，默认值：一个小型的base64的gif图片
            html: false,//设置加载内容是否是html格式，默认值是false
            content: "",//忽略icon和text的值，直接在加载框中显示此值
            mask: true //是否显示遮罩效果，默认显示
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

                var imageInfo = {};
                imageInfo.imageId = hex_md5(b64);
                imageInfo.imageB64 = b64;
                imageVOList.push(imageInfo);

            }

            var groupName = $('#group option:selected').text();
            var number = $('#number').val();
            var imageConditionVO ={};
            imageConditionVO.groupName = groupName;
            imageConditionVO.imageNum = number;

            var startDate = $('#startDate').val();
            var endDate = $('#endDate').val();
            if (startDate == "" && endDate == "") {
                imageConditionVO.startTime = new Date(2001, 1 - 1, 1);
                imageConditionVO.endTime = $('#endtime').data("datetimepicker").getDate();
            } else if (startDate == "" && endDate != "") {
                imageConditionVO.startTime = new Date(2001, 1 - 1, 1);
                imageConditionVO.endTime = $('#endtime').data("datetimepicker").getDate();
            } else if (startDate == endDate || (startDate != "" && endDate == "")) {
                imageConditionVO.endTime = $('#endtime').data("datetimepicker").getDate();
                var date = $('#starttime').data("datetimepicker").getDate();
                imageConditionVO.startTime = new Date(date.getFullYear(), date.getMonth(), date.getDate()-1);
            } else {
                imageConditionVO.startTime = $('#starttime').data("datetimepicker").getDate();
                imageConditionVO.endTime = $('#endtime').data("datetimepicker").getDate();
            }
            //发送
            sendData(imageVOList,imageConditionVO,false,null);
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
    function sendData(imageVOList,imageConditionVO,flag,number) {
        loading();

        var parm = {
            'imageVOList': imageVOList,
            'imageConditionVO': imageConditionVO
        };
        //数据放入数组
        $.ajax({
            type: 'post',
            dataType: 'json',
            contentType: "application/json;charset=utf-8",
            url: '/search/process',
            data: JSON.stringify(parm),
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
                //结束loading
                $("#imageBox").mLoading("hide");
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
        var url = $(e).parents("li")[0].children[0].src;

        var imageInfo = {};
        imageInfo.imageUrl = url;
        var imageVOList = [];
        imageVOList.push(imageInfo);

        var groupName = $("#group option:selected").text();
        var number = $('#number').val();
        var imageConditionVO = {};
        imageConditionVO.groupName = groupName;
        imageConditionVO.imageNum = number;

        var startDate = $('#startDate').val();
        var endDate = $('#endDate').val();
        if (startDate == "" && endDate == "") {
            imageConditionVO.startTime = new Date(2001, 1 - 1, 1);
            imageConditionVO.endTime = $('#endtime').data("datetimepicker").getDate();
        } else if (startDate == "" && endDate != "") {
            imageConditionVO.startTime = new Date(2001, 1 - 1, 1);
            imageConditionVO.endTime = $('#endtime').data("datetimepicker").getDate();
        } else if (startDate == endDate || (startDate != "" && endDate == "")) {
            imageConditionVO.endTime = $('#endtime').data("datetimepicker").getDate();
            var date = $('#starttime').data("datetimepicker").getDate();
            imageConditionVO.startTime = new Date(date.getFullYear(), date.getMonth(), date.getDate()-1);
        } else {
            imageConditionVO.startTime = $('#starttime').data("datetimepicker").getDate();
            imageConditionVO.endTime = $('#endtime').data("datetimepicker").getDate();
        }


        //再次搜索的图片所在列
        var col = $(e).parents("ul")[0].id;
        col = col.split("result")[1];
        //删除原本数据
        $(e).parents("ul").find("li").remove();
        //发送获取新数据
        sendData(imageVOList,imageConditionVO,true,col);
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
        if (Number(number) > 1000) {
            swal.fire("返回结果不可大于1000张！", "", "warning");
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

    function date() {
        $('#starttime').datetimepicker({
            autoclose: true,
            pickerPosition: "bottom-left",
            todayBtn:true,
            language:'zh-CN',
            format:'yyyy-MM-dd',
            minView: 2
        });
        $('#endtime').datetimepicker({
            autoclose: true,
            pickerPosition: "bottom-left",
            todayBtn:true,
            language:'zh-CN',
            format:'yyyy-MM-dd',
            minView: 2
        });
    }

    $('#startDate').on('change', function () {
        if (!judgeDate()) {
            $('#starttime').data("datetimepicker").setDate(new Date());
            $('#startDate').val("");
        }
    });

    $('#endDate').on('change', function () {
        if (!judgeDate()) {
            $('#endtime').data("datetimepicker").setDate(new Date());
            $('#endDate').val("");
        }
    });

    function judgeDate() {
        var startTime = $('#starttime').data("datetimepicker").getDate();
        var endTime = $('#endtime').data("datetimepicker").getDate();
        var startDate = $('#startDate').val();
        var endDate = $('#endDate').val();

        if (startTime > new Date() || endTime > new Date()) {
            swal.fire("不能选择大于当前日期的时间！", "", "warning");
            return false;
        } else if (startDate != "" && endDate != "" && startDate > endDate) {
            swal.fire("开始时间必须要小于结束时间！", "", "warning");
            return false;
        } else {
            return true;
        }
    }
});




