1. Encje managed i detached. 
```java
    @Transactional
    public void acceptOrder(Long orderId) {
        Order order = ordersDAO.findById(orderId).get();
        order.accept();
//        ordersDAO.save(order);
    }
```
2. Propagation.REQUIRES_NEW a widoczność zmian z transakcji wyżej 
* step 1 nie widzę ordera 
```java
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logInNewTrans(Long orderId, String message) {
        Optional<Order> order = orderDAO.findById(orderId);
        OrderEventLog orderEventLog = new OrderEventLog(order, message);
        eventLogDAO.save(orderEventLog);
    }

```
3. Propagation.REQUIRES_NEW a przekazwyanie encji załadowanej wcześniej w innej transakcji 
*  mój log jest detached (Encje zwrócone z REQUIRES_NEW są odłączone)

```java

public void acceptOrder(Long orderId) {
    OrderEventLog log = eventLogService.newEventInNewTrans("Podjeto próbę zatwierdzenia");

    Order order = ordersDAO.findById(orderId);
    order.accept();
    ordersDAO.save(order);

    log.attach(order);
}
```

*  a może go nie potrzebuję?

```java
    @Transactional
    public Order newOrder(OrderData orderData) {

        OrderEventLog logAtBegin = eventLogService.
                newEventInNewTrans(Optional.empty(), "Podjęto próbę zapisu zamówienia");

        Order order = new Order(orderData);
        order = ordersDAO.save(order);

//        logAtBegin.attachOrder(order);
        eventLogService.newEvent(order.getId(), "Zapisano zamówienie");
        return order;
    }
```

4. Rollback tylko danych w bazie i tylko konkretnej transakcji (Encje są śledzone tylko w ramach tej transakcji, w której zostały pobrane, Nie przekazuj encji pomiędzy granicami transakcji. Bezpieczniej przekazać ID lub TO)
```java
@Transactional
public void cancelOrder(Long orderId) {
    Order order = ordersDAO.findById(orderId);
    try {
        ordersDAO.tryCancel(order);
        eventLogService.newEvent(orderId, "Anulowano zamówienie");
    } catch (RuntimeException ex) {
        eventLogService.newEvent(orderId, "Nieudana próba anulowania");
    }    
}

@Transactional(propagation = Propagation.REQUIRES_NEW)
public void tryCancel(Order order) {
    order.cancel();
    em.flush();
    if( order.orderAlreadyDelivered()){
        throw new RuntimeException("Nie można anulować zamówienia");
    }
}
```

5. OOM na dużych ilościach encji pobranych do pamięci.
* bez batchowania boom
```java

public void markAllSent(){
    List<Order> orders = ordersDAO.getPacked();
    orders.forEach(o -> o.markSent());
}
```

* batchowanie przesuwa indeks 

```java

private final static int BATCH_SIZE = 100;

public void markAllSent(){
    long index = 0;
    List<Order> orders = ordersDAO.getPacked(index,BATCH_SIZE);
    while(!orders.isEmpty()){
        orders.forEach(o -> o.markSent());
        index += BATCH_SIZE;
        orders = ordersDAO.getPacked(index,BATCH_SIZE);
    }
}
```
* batchowanie ale bez flusha boom

```java

private final static int BATCH_SIZE = 100;

public void markAllSent(){

    List<Order> orders = ordersDAO.getPacked(BATCH_SIZE);
    while(!orders.isEmpty()){
        orders.forEach(o -> o.markSent());

        orders = ordersDAO.getPacked(BATCH_SIZE);
    }
}
```

* batchowanie z flushem ale bez nowej transakcji

```java

private final static int BATCH_SIZE = 100;

public void markAllSent(){

    List<Order> orders = ordersDAO.getPacked(BATCH_SIZE);
    while(!orders.isEmpty()){
        orders.forEach(o -> o.markSent());
   
        em.flush();
        em.clear();
        orders = ordersDAO.getPacked(BATCH_SIZE);
    }
}
```
6. Dodajemy wysyłanie emaila/ inną długotrwałą czynność oraz @Transactional(timeout = 10). 

```java
@Transactional(timeout = 10)
public void markAllSent() {

    List<Order> orders = ordersDAO.getPacked(BATCH_SIZE);
    while (!orders.isEmpty()) {
        orders.forEach(o -> {
            o.markSent();
            emailService.sendNotification(o);
        });

        em.flush();
        em.clear();
        orders = ordersDAO.getPacked(BATCH_SIZE);
    }
}
```
* rozbijamy na nowe transakcje wewnątrz tej samej klasy (omawiamy PROXY i warstwowe AOP)
```java

public void markAllSent() {

    int size = markBatchSent(BATCH_SIZE);
    while (size == BATCH_SIZE) {

        size = markBatchSent(BATCH_SIZE);
    }
}

@Transactional(propagation = Propagation.REQUIRES_NEW)
public int markBatchSent(int batchSize) {
    List<Order> orders = ordersDAO.getPacked(batchSize);
    orders.forEach(o -> {
        o.markSent();
        emailService.sendNotification(o);
    });
    return orders.size();
}
```
* _self lub odrębna klasa (Zmiany się zapisują ale Timeout dalej leci)
```java

private OrdersService _self;
...
public void markAllSent() {

    int size = _self.markBatchSent(BATCH_SIZE);
    while (size == BATCH_SIZE) {

        size = _self.markBatchSent(BATCH_SIZE);
    }
}

@Transactional(propagation = Propagation.REQUIRES_NEW)
public int markBatchSent(int batchSize) {
    List<Order> orders = ordersDAO.getPacked(batchSize);
    orders.forEach(o -> {
        o.markSent();
        emailService.sendNotification(o);
    });
    return orders.size();
}
```

* @Transactional(propagation = Propagation.NEVER) na forze najwyżej 


