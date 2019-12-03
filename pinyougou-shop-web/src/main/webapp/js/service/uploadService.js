//文件上传服务层
app.service('uploadService',function ($http) {
    this.uploadFile=function () {

        var formData = new FormData();
        formData.append("file",file.files[0]);

        return $http({
            url: "../upload.do",
            method: 'post',
            data:formData,
            headers:{'Content-Type':undefined}, //默认的Content-Type header 是application/json 通过设置浏览器会帮我们把Content-Type 设置为 multipart/form-data
            transformRequest: angular.identity  //将表格序列化二进制表格
        })
    }
})