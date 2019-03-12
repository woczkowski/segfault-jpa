/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.itkontekst.jpatransactional.orders;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author oczkowski
 */
interface EventLogDAO extends CrudRepository<OrderEventLog, Long>{

    public List<OrderEventLog> findByOrder(Order order);
    
}
