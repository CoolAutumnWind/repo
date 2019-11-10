// 品牌控制层
app.controller('brandController',function ($scope, $controller, brandService) {

    $controller('baseController',{$scope:$scope});

    //读取列表数据绑定到表单中
    $scope.findAll = function () {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        )
    }

    //分页
    $scope.findPage = function (pageNum, pageSize) {
        brandService.findPage(pageNum, pageSize).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;  //更新总记录数
            }
        )
    }

    // 新增-修改
    $scope.save = function () {
        var object = null;    //方法名称
        if ($scope.entity.id != null) {
            object = brandService.update($scope.entity);  //则执行修改方法
        } else {
            object = brandService.add($scope.entity);
        }
        object.success(
            function (response) {
                if (response.success) {
                    // 判断为true重新查询加载列表
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            }
        )
    }

    // 获取实体
    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    }

    //批量删除
    $scope.dele = function () {
        // 获取复选框
        brandService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                }
            }
        );
    }

    $scope.searchEntity = {}; // 定义搜索对象

    //条件查询
    $scope.search = function (pageNum, pageSize) {
        brandService.findPage(pageNum, pageSize, $scope.searchEntity).success(
            function (response) {
                $scope.paginationConf.totalItems = response.total;//总记录数
                $scope.list = response.rows;//给列表变量赋值
            }
        )
    }
})