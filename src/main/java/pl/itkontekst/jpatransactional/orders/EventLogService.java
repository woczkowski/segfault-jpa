/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.itkontekst.jpatransactional.orders;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author oczkowski
 */
@Service
public class EventLogService {
    
    private EventLogDAO eventLogDAO;

    private OrdersDAO orderDAO;

    @Autowired
    public EventLogService(EventLogDAO eventLogDAO, OrdersDAO orderDAO) {
        this.eventLogDAO = eventLogDAO;
        this.orderDAO = orderDAO;
    }

    

}

