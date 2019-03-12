/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.itkontekst.jpatransactional.orders;

import java.math.BigDecimal;

/**
 *
 * @author oczkowski
 */
class OrderData {
    String productName;
    
    int quantity;
    
    BigDecimal price;

    OrderData(String productName, int quantity, BigDecimal price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }
    
}
