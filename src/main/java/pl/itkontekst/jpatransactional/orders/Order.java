/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.itkontekst.jpatransactional.orders;

import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author oczkowski
 */
@Entity
@Table(name = "orders")
class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String productName;
    
    private int quantity;
    
    private BigDecimal price;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    Order() {
    }

    Order(OrderData orderData) {
        this.productName = orderData.productName;
        this.quantity = orderData.quantity;
        this.price = orderData.price;
        this.status = OrderStatus.PENDING;
    }

    void accept(){
        this.status = OrderStatus.ACCEPTED;
    }

    Long getId() {
        return id;
    }
    
    boolean isAccepted() {
        return OrderStatus.ACCEPTED.equals(this.status);
    }
    
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Order other = (Order) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OrderEntity{" + "id=" + id + ", productName=" + productName + ", quantity=" + quantity + ", price=" + price + '}';
    }

    void cancel() {
        this.status = OrderStatus.CANCELED;
    }

    boolean orderAlreadyDelivered() {
        return OrderStatus.DELIVERED.equals(status);
    }

    boolean isCanceled() {
        return OrderStatus.CANCELED.equals(status);
    }

    void markDelivered() {
        this.status = OrderStatus.DELIVERED;
    }



    
}
