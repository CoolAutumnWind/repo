app.controller('searchController', function ($scope,$location, searchService) {

    $scope.search = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        $scope.searchMap.pageSize = parseInt($scope.searchMap.pageSize);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;    //搜索返回的结果
                buildPageLabel();
            }
        )
    }

    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'pageNo': '1',
        'pageSize': '40',
        'sort':'',
        'sortField':''
    };  //搜索对象
    //添加搜索选项
    $scope.addSearchItem = function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();//执行搜索
    }
    //移除搜索选项
    $scope.removeSearchItem = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = "";
        } else {
            delete $scope.searchMap.spec[key];//移除此属性
        }
        $scope.search();//执行搜索
    }

    //构建分页标签
    buildPageLabel = function () {
        $scope.pageLabel = [];    //新增分页栏属性
        var firstPage = 1;  //开始页码
        var lastPage = $scope.resultMap.totalPages;//截止页码
        $scope.firstDot=true;//前面有点
        $scope.lastDot=true;//后边有点
        if ($scope.resultMap.totalPages > 5) {//如果总页码大于5
            if ($scope.searchMap.pageNo <= 3) {  //当前页码小于等于3 显示前5页
                lastPage = 5;
                $scope.firstDot=false;//前面没点
            } else if ($scope.searchMap.pageNo >= lastPage - 2) {
                firstPage = $scope.resultMap.totalPages - 4;  //显示后5页
                $scope.lastDot=false;//后边没点
            } else {
                //显示当前页为中心的5页
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        }else{
            $scope.firstDot=false;//前面无点
            $scope.lastDot=false;//后边无点
        }
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    }

    //根据页码查询
    $scope.queryByPage = function (pageNo) {
        //页码验证
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {    //当前页码小于1或者大于最大页码数，直接无效操作
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.search();
    }

    //判断当前页是否第一页
    $scope.isTopPage=function () {
        if ($scope.searchMap.pageNo == 1){
            return true;
        }else{
            return false;
        }
    }

    //判断当前页是否最后一页
    $scope.isEndPage=function () {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages){
            return true;
        }else{
            return false;
        }
    }

    //设置排序规则
    $scope.sortSearch=function (sort,sortField) {
        $scope.searchMap.sort=sort;
        $scope.searchMap.sortField=sortField;
        $scope.search();
    }

    //判断关键字是不是品牌
    $scope.keywordsIsBrand=function () {
        for (var i = 0; i<$scope.resultMap.brandList.length;i++){
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>= 0){
                return true;
            }
        }
        return false;
    }


    //加载并查询关键字
    $scope.loadKeywords=function () {
       $scope.searchMap.keywords =  $location.search()['keywords'];
       $scope.search();
    }

})