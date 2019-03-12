/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.itkontekst.jpatransactional.orders;

import java.time.LocalDateTime;
import java.util.Optional;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author oczkowski
 */
@Entity
class OrderEventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private LocalDateTime eventDate;
    
    private String message;

    @ManyToOne
    private Order order;

    OrderEventLog() {
    }
    
    OrderEventLog(Optional<Order> order, String message) {
        this.eventDate = LocalDateTime.now();
        this.message = message;
        this.order = order.orElse(null);
    }

    void attachOrder(Order order) {
        this.order = order;
    }
    
}
