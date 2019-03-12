/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.itkontekst.jpatransactional.orders;

import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author oczkowski
 */
interface OrdersDAO extends CrudRepository<Order, Long>{
    
}
