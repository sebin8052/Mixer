package com.Mixer.library.service.impl;

import com.Mixer.library.model.*;
import com.Mixer.library.repository.OrderDetailRepository;
import com.Mixer.library.repository.OrderRepository;
import com.Mixer.library.repository.ProductRepository;
import com.Mixer.library.service.AddressService;
import com.Mixer.library.service.OrderService;
import com.Mixer.library.service.ShoppingCartService;
import com.Mixer.library.service.WalletService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService
{

    private OrderRepository orderRepository;

    private OrderDetailRepository orderDetailRepository;

    private ShoppingCartService shoppingCartService;

    private ProductRepository productRepository;

    private AddressService addressService;

    private WalletService walletService;


    public OrderServiceImpl(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, ShoppingCartService shoppingCartService, ProductRepository productRepository, AddressService addressService, WalletService walletService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.shoppingCartService = shoppingCartService;
        this.productRepository = productRepository;
        this.addressService = addressService;
        this.walletService = walletService;
    }



    @Override
    public Order save(ShoppingCart cart, long address_id, String paymentMethod, Double oldTotalPrice)  {
        Order order = new Order();
        order.setOrderDate(new Date());
        order.setCustomer(cart.getCustomer());
        order.setTotalPrice(cart.getTotalPrice());
        order.setQuantity(cart.getTotalItems());
        order.setPaymentMethod(paymentMethod);

        Address shippingAddress=addressService.findByIdOrder(address_id);
        order.setShippingAddress(shippingAddress);
        order.setAccept(false);
        order.setOrderStatus("Pending");



       double deliveryFee= calculateDeliveryFee(shippingAddress);
       order.setDeliveryFee(deliveryFee);


      double totalPrice =cart.getTotalPrice() + deliveryFee;
      order.setTotalPrice(totalPrice);


        if(oldTotalPrice != null){
            Double discount= oldTotalPrice - cart.getTotalPrice();
            String formattedDiscount = String.format("%.2f", discount);
            order.setDiscountPrice(Double.parseDouble(formattedDiscount));
        }
        List<OrderDetail> orderDetailList=new ArrayList<>();
        for(CartItem item : cart.getCartItems()){
            Product product=item.getProduct();
            int requestedQuantity = item.getQuantity();
            if (requestedQuantity > product.getCurrentQuantity())
            {
                throw new IllegalArgumentException("Requested quantity exceeds available quantity for product: " + product.getName());
            }
            int newQuantity = product.getCurrentQuantity() - requestedQuantity;
            product.setCurrentQuantity(newQuantity);
            productRepository.save(product);
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(item.getProduct());
            orderDetail.setSize(item.getSize());
            orderDetail.setQuantity(requestedQuantity);
            orderDetailRepository.save(orderDetail);
            orderDetailList.add(orderDetail);
        }
        order.setOrderDetails(orderDetailList);
        if(paymentMethod.equals("COD")) {
            order.setPaymentStatus("Pending");
            shoppingCartService.deleteCartById(cart.getId());
        }
        return orderRepository.save(order);
    }


    @Override
    public void cancelOrder(long order_id ,String cancelReason) {
        Order order=orderRepository.getById(order_id);
        Customer customer=order.getCustomer();

        List<OrderDetail>orderDetailList=order.getOrderDetails();
        for(OrderDetail orderDetail : orderDetailList){
            Product product = orderDetail.getProduct();
            if(product != null) {
                int currentQuantity = product.getCurrentQuantity();
                product.setCurrentQuantity(currentQuantity + orderDetail.getQuantity());
                productRepository.save(product);
            }
        }
        order.setOrderStatus("Cancelled");


        order.setCancelReason(cancelReason);

        orderRepository.save(order);



        if(order.getPaymentMethod().equals("Wallet") || order.getPaymentMethod().equals("RazorPay")){
            walletService.returnCredit(order,customer);
        }
    }


    @Override
    public void acceptOrder(long id) {
        Order order=orderRepository.getById(id);
        order.setAccept(true);
        Date oldDate = new Date();
        LocalDate localDate = oldDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate newLocalDate = localDate.plusDays(5);
        Date newDate = Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        order.setDeliveryDate(newDate);
        order.setOrderStatus("Confirmed");
        orderRepository.save(order);
    }

    @Override
    public void replaceOrderAdmin(long id) {
        Order order=orderRepository.getById(id);
        order.setAccept(true);
        Date oldDate = new Date();
        LocalDate localDate = oldDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate newLocalDate = localDate.plusDays(5);
        Date newDate = Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        order.setDeliveryDate(newDate);
        order.setOrderStatus("Confirmed");
        orderRepository.save(order);
    }

    @Override
    public Order findOrderById(long id) {
        return orderRepository.getById(id);
    }

    @Override
    public List<Order> findAllOrdersByCustomer(long id) {
        return orderRepository.findAllBy(id);
    }




    @Override
    public List<Long> findAllOrderCountForEachMonth() {
        List<Long>orderCounts=new ArrayList<>();
        LocalDate currentDate = LocalDate.now().withMonth(1);
        for(int i=0 ; i < 12 ; i++){
            LocalDate localStartDate=currentDate.withDayOfMonth(1);
            LocalDate localEndDate=currentDate.withDayOfMonth(currentDate.lengthOfMonth());
            long orderCount= orderRepository.countByOrderDateBetweenAndOrderStatus(localStartDate,localEndDate,"Delivered");
            orderCounts.add(orderCount);
            currentDate = currentDate.plusMonths(1);
        }
        return orderCounts;
    }

    @Override
    public Double getTotalOrderAmount() {
        return orderRepository.getTotalConfirmedOrdersAmount();
    }

    @Override
    public Long countTotalConfirmedOrders() {
        return orderRepository.countAllConfirmedOrders();
    }

    @Override
    public Double getTotalAmountForMonth() {
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate=currentDate.withDayOfMonth(1);
        LocalDate endDate=currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        Double totalAmount = orderRepository.getTotalConfirmedOrdersAmountForMonth(startDate,endDate,"Delivered");
        return totalAmount;
    }


    @Override
    public List<Double> getTotalAmountForEachMonth() {
        List<Double> totalRevenuePerMonth = new ArrayList<>();
        LocalDate currentDate = LocalDate.now().withMonth(1);
        for(int i=0 ; i < 12 ; i++){
            LocalDate localStartDate=currentDate.withDayOfMonth(1);
            LocalDate localEndDate=currentDate.withDayOfMonth(currentDate.lengthOfMonth());
            Double totalRevenue = orderRepository.getTotalConfirmedOrdersAmountForMonth(localStartDate,localEndDate,"Delivered");
            totalRevenuePerMonth.add(totalRevenue);
            currentDate = currentDate.plusMonths(1);
        }
        return totalRevenuePerMonth;
    }



    @Override
    public void updatePayment(Order order, boolean status) {
        if(status){
            order.setPaymentStatus("Paid");
            orderRepository.save(order);
        }else {
            order.setPaymentStatus("Failed");
            orderRepository.save(order);
        }
    }




    @Override
    public void returnOrder(long id, Customer customer) {
        Order order=orderRepository.findById(id);
        order.setOrderStatus("Returned");
        orderRepository.save(order);
    }

    @Override
    public void replaceOrderCustomer(long id, Customer customer) {
        Order order=orderRepository.findById(id);
        order.setOrderStatus("Replace");
        orderRepository.save(order);
    }

    @Override
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }


    @Override
    public void updateOrderStatus(String status, long order_id) {
        if(order_id != 0)
        {
            Order order = orderRepository.getReferenceById(order_id); // get the order from the repository
            if(status.equals("Shipped"))
            {
                order.setOrderStatus(status);
                if(order.getPaymentMethod().equals("COD"))
                {
                    order.setPaymentStatus("Pending");
                } else if (order.getPaymentMethod().equals("Wallet")||order.getPaymentMethod().equals("RazorPay"))
                {
                 order.setPaymentStatus("Paid");
                }
                orderRepository.save(order);
            }
            else if(status.equals("Delivered"))
            {
                order.setOrderStatus(status);
                if(order.getPaymentMethod().equals("COD")||order.getPaymentMethod().equals("Wallet")||order.getPaymentMethod().equals("RazorPay"))
                {
                    order.setPaymentStatus("Paid");
                }
                orderRepository.save(order);
            }

            else if(status.equals("Cancelled"))
            {
                order.setOrderStatus(status);
                if(order.getPaymentMethod().equals("Wallet")|| order.getPaymentMethod().equals("RazorPay"))
                {
                    order.setPaymentStatus("Refunded");
                }
                orderRepository.save(order);
            }
            else if (status.equals("Processing"))
            {
                order.setOrderStatus(status);
                if(order.getPaymentMethod().equals("COD"))
                {
                    order.setPaymentStatus("Pending");
                }
                else if (order.getPaymentMethod().equals("Wallet")||order.getPaymentMethod().equals("Razorpay"))
                {
                    order.setPaymentStatus("Paid");

                }
                       orderRepository.save(order);
            }
            else if (status.equals("Returned"))
            {
                order.setOrderStatus(status);
                if(order.getPaymentMethod().equals("COD")||order.getPaymentMethod().equals("Wallet")||order.getPaymentMethod().equals("Razorpay"))
                {
                    order.setPaymentStatus("Processing");
                }
                 orderRepository.save(order);
            }
            else if (status.equals("Out of Delivery"))
            {
                order.setOrderStatus(status);
                if(order.getPaymentMethod().equals("COD"))
                {
                    order.setPaymentStatus("Pending");
                }
                else if (order.getPaymentMethod().equals("Wallet")||order.getPaymentMethod().equals("Razorpay"))
                {
                    order.setPaymentStatus("Paid");

                }
                orderRepository.save(order);
            }
        }
    }


    public double calculateDeliveryFee(Address address)
    {
        String city =address.getCity();
         if("Bangalore".equalsIgnoreCase(city))
         {
             return 50.0;
         } else if ("Ernakulam".equalsIgnoreCase(city))
         {
           return 80.0;
         }
         return 0.0;
    }

}
