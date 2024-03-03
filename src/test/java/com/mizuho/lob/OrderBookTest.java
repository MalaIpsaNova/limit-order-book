package com.mizuho.lob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderBookTest {
    private OrderBook orderBook;

    @BeforeEach
    void setUp() {
        orderBook = new OrderBook();
    }

    @Test
    void testAddOrder() {
        Order order = new Order(1, 100.0, 'B', 10);
        orderBook.addOrder(order);
        assertEquals(1, orderBook.getOrders('B').size());
    }

    @Test
    void testRemoveOrder() {
        Order order = new Order(1, 100.0, 'B', 10);
        orderBook.addOrder(order);
        orderBook.removeOrder(1);
        assertEquals(0, orderBook.getOrders('B').size());
    }

    @Test
    void testModifyOrderSize() {
        Order order = new Order(1, 100.0, 'B', 10);
        orderBook.addOrder(order);
        orderBook.modifyOrderSize(1, 20);
        assertEquals(20, orderBook.getOrders('B').get(0).getSize());
    }

    @Test
    void testGetPrice() {
        Order order1 = new Order(1, 500.0, 'B', 10);
        Order order2 = new Order(2, 300.0, 'B', 10);
        orderBook.addOrder(order1);
        orderBook.addOrder(order2);
        assertEquals(300.0, orderBook.getPrice('B', 2));
    }

    @Test
    void testGetTotalSize() {
        Order order1 = new Order(1, 500.0, 'B', 10);
        Order order2 = new Order(2, 500.0, 'B', 20);
        orderBook.addOrder(order1);
        orderBook.addOrder(order2);
        assertEquals(30, orderBook.getTotalSize('B', 1));
    }

    @Test
    void testGetOrders() {
        Order order1 = new Order(1, 500.0, 'B', 10);
        Order order2 = new Order(2, 300.0, 'B', 20);
        orderBook.addOrder(order1);
        orderBook.addOrder(order2);
        assertEquals(2, orderBook.getOrders('B').size());
    }
}