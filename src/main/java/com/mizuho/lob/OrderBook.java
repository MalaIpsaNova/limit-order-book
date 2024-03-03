package com.mizuho.lob;

import java.util.*;

public class OrderBook {
    //extra structure to optimize lookups, key is id, value is the Order.
    private Map<Long, Order> ordersById;
    // the key is the price, the value is a map of orders (id + actual order), since multiple orders can have the same price
    private TreeMap<Double, Map<Long, Order>> buyRequests;
    private TreeMap<Double, Map<Long, Order>> sellRequests;

    public OrderBook() {
        ordersById = new HashMap<>(); // we use this structure for efficient lookup of the orders, key is the order ID, value is the Order itself
        buyRequests = new TreeMap<>(Collections.reverseOrder()); // Descending price
        sellRequests = new TreeMap<>(); // Ascending price
    }

    /**
     * Given an Order, add it to the OrderBook (order additions are expected to occur extremely frequently
     * @param order
     */
    public void addOrder(Order order) {
        ordersById.put(order.getId(), order);
        if (order.getSide() == 'B') {
            buyRequests.computeIfAbsent(order.getPrice(), x -> new HashMap<>()).put(order.getId(), order);
        } else {
            sellRequests.computeIfAbsent(order.getPrice(), x -> new HashMap<>()).put(order.getId(), order);
        }
    }

    /**
     * Given an order id, remove an Order from the OrderBook (order deletions are expected to occur at approximately 60% of the rate of the order additions.
     * @param orderId
     */
    public void removeOrder(long orderId) {
        Order order = ordersById.remove(orderId);
        if (order != null) {
            Map<Double, Map<Long, Order>> orders;
            if (order.getSide() == 'B') {
                orders = buyRequests;
            } else {
                orders = sellRequests;
            }
            orders.get(order.getPrice()).remove(orderId);
        }
    }

    /**
     * Given an order id and a new size, modify an existing order in the book to use the new size (size modifications do not affect time priority
     * @param orderId
     * @param newSize
     */
    public void modifyOrderSize(long orderId, long newSize) {
        Order order = ordersById.get(orderId);
        if (order != null) {
            order.setSize(newSize);
        }
    }

    /**
     * Given a side and a level (an integer value >0) return the price for that level (where level 1 represents the best price for
     * a given side). For example, given side=B and level=2, return the second best bid price.
     * @param side
     * @param level
     * @return
     */
    public double getPrice(char side, int level) {
        TreeMap<Double, Map<Long, Order>> orders;
        if (side == 'B') {
            orders = buyRequests;
        } else {
            orders = sellRequests;
        }
        int count = 0;
        for (Map.Entry<Double, Map<Long, Order>> entry : orders.entrySet()) {
            count += entry.getValue().size();
            if (count >= level) {
                return entry.getKey();
            }
        }
        return Double.NaN;
    }

    /**
     * Given a side and a level, return the total size available for that level.
     * @param side
     * @param level
     * @return
     */
    public long getTotalSize(char side, int level) {
        TreeMap<Double, Map<Long, Order>> orders;
        if (side == 'B') {
            orders = buyRequests;
        } else {
            orders = sellRequests;
        }
        int count = 0;
        for (Map.Entry<Double, Map<Long, Order>> entry : orders.entrySet()) {
            count += entry.getValue().size();
            if (count >= level) {
                long total = 0;
                for (Order order : entry.getValue().values()) {
                    total += order.getSize();
                }
                return total;
            }
        }
        return 0;
    }

    /**
     * Get all orders from a given side of the book, in level - and time-order.
     * @param side
     * @return
     */
    public List<Order> getOrders(char side) {
        TreeMap<Double, Map<Long, Order>> orders;
        if (side == 'B') {
            orders = buyRequests;
        } else {
            orders = sellRequests;
        }

        List<Order> result = new ArrayList<>();
        for (Map<Long, Order> levelOrders : orders.descendingMap().values()) {
            result.addAll(levelOrders.values());
        }
        return result;
    }
}
