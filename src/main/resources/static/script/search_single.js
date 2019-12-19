var searchAgainClick;
$(document).ready(function () {
    $.fn.datetimepicker.dates['zh-CN'] = {
        days: ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"],
        daysShort: ["周日", "周一", "周二", "周三", "周四", "周五", "周六", "周日"],
        daysMin: ["日", "一", "二", "三", "四", "五", "六", "日"],
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
            mask: true//是否显示遮罩效果，默认显示
        });
    }

    //搜索按钮
    $('#onSearch').on('click', function () {
        //每次点击搜索按钮，搜索结果需清空
        clearResultBox();
        //获取图片
        var file = document.getElementById("upload");
        var img = file.files[0];
        if (judgeNull(file)) {
            var reader = new FileReader();
            reader.onload = function (e) {
                //获取base64编码
                var dataUrl = e.target.result;
                var b64 = dataUrl.split(",")[1];
                //要传递的数据
                var imageInfo = {};
                imageInfo.imageId = hex_md5(b64);
                imageInfo.imageB64 = b64;

                var groupName = $('#group option:selected').text();
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
                //发送
                sendData(imageInfo,imageConditionVO);
            };
            reader.readAsDataURL(img);
        } else {
            swal.fire("未选择图片！", "", "warning");
        }
    });

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

    function sendData(imageInfo,imageConditionVO) {
        loading();
        //数据放入数组
        imageVOList = [];
        imageVOList.push(imageInfo);
        var parm = {
            'imageVOList': imageVOList,
            'imageConditionVO': imageConditionVO
        };

        $.ajax({
            type: 'post',
            dataType: 'json',
            contentType: "application/json;charset=utf-8",
            url: '/search/process',
            data: JSON.stringify(parm),
            success: function (data) {
                var imgs = data[0];
                if (imgs instanceof Array) {
                    if (imgs.length != 0) {
                        //隐藏空空如也的div
                        $("#noResult").css('display', 'none');
                        //将查询结果图片放入div
                        var box = $("#showBox");
                        box.css('display', 'block');
                        for (var i = 0; i < imgs.length; i++) {
                            box.append(
                                '<li>' +
                                '<img src="' + imgs[i].imageShowPath + '" id="image' + i + '">' +
                                '<div class="image_info clearfix">' +
                                '<div class="fl similarity">' + '相似度：' + (Number(imgs[i].distance).toFixed(2)) * 100 + '%' + '</div>' +
                                '<div class="search_again_button"><button class="btn btn-primary" onclick="searchAgainClick(this)">继续搜索</button></div>' +
                                '</div>' +
                                '</li>'
                            );
                            new Viewer(document.getElementById("image" + i));
                        }
                    } else {
                        swal.fire("找不到相似图片！", "", "warning");
                    }
                } else {
                    var dataObj = eval("(" + imgs + ")");//转换为json对象
                    swal.fire(dataObj.message, "", "warning");
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
        var url = $(e).parents("li")[0].children[0].src;
        showUpImage(url);
        clearResultBox();
        //ajax发送图片数据到后台
        var imageInfo = {};
        imageInfo.imageUrl = url;

        var groupName = $('#group option:selected').text();
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
        //发送
        sendData(imageInfo,imageConditionVO);
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
    }

    function date() {
        $('#starttime').datetimepicker({
            autoclose: true,
            pickerPosition: "bottom-left",
            todayBtn: true,
            language: 'zh-CN',
            format: 'yyyy-MM-dd',
            minView: 2
        });
        $('#endtime').datetimepicker({
            autoclose: true,
            pickerPosition: "bottom-left",
            todayBtn: true,
            language: 'zh-CN',
            format: 'yyyy-MM-dd',
            minView: 2
        });
    }

    $('#addImageButton').on('click', function () {
        $('#upload').trigger('click');
    });

});



