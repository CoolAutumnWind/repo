package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 6000)
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    /**
     * 购物车列表
     *
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartListString == null || cartListString.equals("")) {
            cartListString = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
        if (username.equals("anonymousUser")) {//如果未登录
            return cartList_cookie;
        } else {//如果登录
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username); //从redis中提取
            if (cartList_cookie.size()>0){//如果本地存在购物车
                //合并购物车
                cartList_redis = cartService.mergeCartList(cartList_cookie, cartList_redis);
                //将合并后的购物车数据存入redis
                cartService.saveCarListToRedis(username, cartList_redis);
                //清除本地购物车
                CookieUtil.deleteCookie(request, response, "cartList");
                //返回合并后的购物车
            }
            return cartList_redis;
        }
    }

    /**
     * 添加商品到购物车
     *
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num) {
        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            //从Cookie中取出数据
            List<Cart> cartList = findCartList(); //获取列表
            //向购物车添加商品
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);   //操作完覆盖原有的列表
            if (username.equals("anonymousUser")) {//如果未登录
                // 将购物车存入Cookie
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");
            } else {
                cartService.saveCarListToRedis(username, cartList);
            }
             return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");

        }
    }
}
