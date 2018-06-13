package org.csu.mypetstorespring.persistence;

import org.csu.mypetstorespring.domain.Product;

import java.util.List;

public interface ProductMapper {

    // 根据大类categoryId 来查询属于该类的所有Product
    List<Product> getProductListByCategory(String categoryId);

    // 根据小类 productId 来查询该product对象
    Product getProduct(String productId);

    // 根据关键字 keywords 查询所有符合条件的Product
    List<Product> searchProductList(String keywords);
}
