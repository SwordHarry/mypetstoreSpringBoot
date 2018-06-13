package org.csu.mypetstorespring.controller;

import org.csu.mypetstorespring.domain.Cart;
import org.csu.mypetstorespring.domain.CartItem;
import org.csu.mypetstorespring.domain.Item;
import org.csu.mypetstorespring.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

@Controller
public class CartController {

    //日志
    private final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CatalogService catalogService;

    // 往购物车加商品
    @GetMapping("/cart/cart")
    public String addItemToCart(@ModelAttribute("workingItemId") String workingItemId, HttpSession session, Model model){

        if(workingItemId != null){
            Cart cart = (Cart) session.getAttribute("cart");

            // 如果购物车为空
            if(cart == null || cart.getNumberOfItems() == 0){
                cart = new Cart();
            }

            if(cart.containsItemId(workingItemId)){
                cart.incrementQuantityByItemId(workingItemId);
            }else{
                boolean isInStock = catalogService.isItemInStock(workingItemId);
                Item item = catalogService.getItem(workingItemId);
                cart.addItem(item,isInStock);
                logger.info("往购物车加商品,商品号："+item.getItemId());
            }

            session.setAttribute("cart",cart);
            model.addAttribute("cart",cart);
        }

        return "cart/cart";
    }

    // 跳往购物车
    @GetMapping("/cart/viewCart")
    public String viewCart(HttpSession session,Model model){
//        logger.debug("log..."); // 输出DEBUG级别的日志
        Cart cart = (Cart) session.getAttribute("cart");

        if(cart == null){
            cart = new Cart();
        }

        session.setAttribute("cart",cart);
        model.addAttribute("cart",cart);

        return "cart/cart";
    }

    // 从购物车中去掉商品
    // GetMapping 里面不允许相同的值，先随便写一个，可行
    @GetMapping("/cart/removeItemFromCart")
    public String removeItemFromCart(@RequestParam("workingItemId") String workingItemId, HttpSession session, Model model){
//        logger.debug("log..."); // 输出DEBUG级别的日志
        Cart cart = (Cart) session.getAttribute("cart");

        Item item = cart.removeItemById(workingItemId);

        logger.info("从购物车中去掉商品,商品号："+item.getItemId());
        if (item == null){
            model.addAttribute("message","Attempted to remove null CartItem from Cart.");
            return "error";
        }else{
            session.setAttribute("cart",cart);
//            model.addAttribute("cart",cart);
            return "cart/cart";
        }
    }

    // AJAX,去掉商品
    @GetMapping("/cart/removeAJAX")
    public String removeAJAX(@RequestParam("workingItemId") String workingItemId, HttpSession session,HttpServletResponse response , Model model){
//        logger.debug("log..."); // 输出DEBUG级别的日志
        Cart cart = (Cart) session.getAttribute("cart");

        Item item = cart.removeItemById(workingItemId);

        response.setContentType("text/xml;charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.setHeader("Cache-Control", "no-cache");
        out.println("<?xml version='1.0' encoding='"+"utf-8"+"' ?>");

        if (item == null){
            model.addAttribute("message","Attempted to remove null CartItem from Cart.");
            out.close();
            return "error";
        }else{

            out.println("<Msg>"+workingItemId+"?"+cart.getSubTotal()+"</Msg>");
            out.flush();
            out.close();
            session.setAttribute("cart",cart);

            return "cart/cart";
        }
    }

//    // 更新购物车
//    @PostMapping("/cart/updateCart")
//    public String updateCart(HttpServletRequest request, HttpSession session){
//
//        Cart cart = (Cart)session.getAttribute("cart");
//        Iterator<CartItem> cartItemIterator = cart.getAllCartItems();
//        while( cartItemIterator.hasNext()){
//            CartItem cartItem = (CartItem) cartItemIterator.next();
//            String itemId = cartItem.getItem().getItemId();
//            try {
//                int quantity = Integer.parseInt((String) request.getParameter(itemId));
//                cart.setQuantityByItemId(itemId, quantity);
//                if (quantity < 1) {
//                    cartItemIterator.remove();
//                }
//            } catch (Exception e) {
//                //ignore parse exceptions on purpose
//                e.printStackTrace();
//            }
//        }
//        session.setAttribute("cart",cart);
//
//        return "cart/cart";
//
//    }

    // AJAX 购物车刷新
    @GetMapping("/cart/updateCart")
    public void updateCart(@RequestParam("itemId") String itemId,@RequestParam("quantity")String quantity,HttpServletResponse response ,HttpSession session){
//        logger.debug("log..."); // 输出DEBUG级别的日志
        Cart cart = (Cart)session.getAttribute("cart");
        Iterator<CartItem> cartItemIterator = cart.getAllCartItems();

        try {
            response.setContentType("text/xml;charset=utf-8");
            PrintWriter out = response.getWriter();
            response.setHeader("Cache-Control", "no-cache");
            out.println("<?xml version='1.0' encoding='"+"utf-8"+"' ?>");

            while( cartItemIterator.hasNext()){
                CartItem cartItem = (CartItem) cartItemIterator.next();
                if(itemId.equals(cartItem.getItem().getItemId())){
                    try {
                        Integer finalQuantity = Integer.parseInt(quantity);
                        cart.setQuantityByItemId(itemId, finalQuantity);

//                        System.out.println(itemId);
                        if (finalQuantity < 1) {
                            cartItemIterator.remove();
                            out.println("<Msg>"+itemId+"?0?"+cart.getSubTotal()+"</Msg>");
                        }else{
//                            System.out.println(cartItem.getTotal());
                            out.println("<Msg>"+itemId+"?"+cartItem.getTotal()+"?"+cart.getSubTotal()+"</Msg>");
//                            System.out.println(cart.getSubTotal());
                        }
                        out.flush();
                        out.close();
                        break;
                    } catch (Exception e) {
                        //ignore parse exceptions on purpose
                        e.printStackTrace();
                    }

                }

            }

            session.setAttribute("cart",cart);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
