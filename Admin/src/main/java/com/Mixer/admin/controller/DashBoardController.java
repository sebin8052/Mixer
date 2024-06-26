package com.Mixer.admin.controller;

import com.Mixer.library.service.CategoryService;
import com.Mixer.library.service.OrderService;
import com.Mixer.library.service.ProductService;
import com.Mixer.library.service.impl.ReportGenerator;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.apache.commons.io.FileUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Controller
public class DashBoardController
{

    private OrderService orderService;
    private ProductService productService;
    private CategoryService categoryService;

    private ReportGenerator reportGenerator;

    public DashBoardController(OrderService orderService, ProductService productService, CategoryService categoryService, ReportGenerator reportGenerator) {
        this.orderService = orderService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.reportGenerator = reportGenerator;
    }


    @GetMapping("/")
    public String getIndex(HttpSession session, Principal principal, Model model,
                           @RequestParam(name = "filter",required = false,defaultValue = "") String filter,
                           @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                           @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate
    ) {
        if (principal == null) {
            return "redirect:/login";
        } else {

            String period;
            switch (filter) {
                case "week" -> {
                    period="week";
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                    startDate = calendar.getTime();
                    endDate = new Date();
                }
                case "month" -> {
                    period="month";
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    startDate = calendar.getTime();
                    endDate = new Date();
                }
                case "day" -> {
                    period = "day";
                    LocalDate today = LocalDate.now();
                    LocalDateTime startDateTime = today.atStartOfDay();
                    LocalDateTime endDateTime = today.atTime(23, 59, 59);
                    ZoneId zone = ZoneId.systemDefault();
                    startDate = Date.from(startDateTime.atZone(zone).toInstant());
                    endDate = Date.from(endDateTime.atZone(zone).toInstant());
                }

                default -> {
                    period="";
                    filter="";
                }
            }
            if(filter!=""){
                List<Object[]> productStats = productService.getProductsStatsBetweenDates(startDate,endDate);
                model.addAttribute("productStats",productStats);
            }else{
                List<Object[]>productStats = productService.getProductStats();
                model.addAttribute("productStats",productStats);
            }
            Double totalAmount = orderService.getTotalOrderAmount();
            model.addAttribute("period", period);
            Long totalProducts = productService.countAllProducts();
            Long totalCategory = categoryService.countAllCategories();
            Long totalOrders = orderService.countTotalConfirmedOrders();
            Double monthlyRevenue = orderService.getTotalAmountForMonth();
            model.addAttribute("TotalAmount",totalAmount);
            model.addAttribute("TotalProducts",totalProducts);
            model.addAttribute("TotalCategory",totalCategory);
            model.addAttribute("TotalOrders",totalOrders);
            model.addAttribute("MonthlyRevenue",monthlyRevenue);
            session.setAttribute("userLoggedIn", true);
            return "index";
        }
    }



    @PostMapping("/generateReport")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> salesReportGenerator(@RequestBody Map<String, Object> requestData ) throws ParseException, IOException, DocumentException {
        String type = (String) requestData.get("type");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = dateFormat.parse((String) requestData.get("from"));
        Date toDate = dateFormat.parse((String) requestData.get("to"));
        String generatedFile="";
        List<Object[]> productStats = productService.getProductsStatsBetweenDates(fromDate,toDate);
        if(type.equals("csv")){
            generatedFile = reportGenerator.generateProductStatsCsv(productStats);
        }else {
            generatedFile = reportGenerator.generateProductStatsPdf(productStats, (String) requestData.get("from"), (String) requestData.get("to"));
        }
        File requestedFile = new File(generatedFile);
        ByteArrayResource resource = new ByteArrayResource(FileUtils.readFileToByteArray(requestedFile));
        HttpHeaders headers = new HttpHeaders();
        if(type.equals("csv")){
            headers.setContentType(MediaType.parseMediaType("text/csv"));
        }else{
            headers.setContentType(MediaType.APPLICATION_PDF);
        }
        headers.setContentDispositionFormData("attachment", generatedFile);
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @GetMapping("/chart")
    @ResponseBody
    public Map<String,Object> getSalesData() {
        List<Long> salesData = orderService.findAllOrderCountForEachMonth();
        List<Double> revenueData = orderService.getTotalAmountForEachMonth();
        Map<String,Object> responseData = new HashMap<>();
        responseData.put("salesData",salesData);
        responseData.put("revenueData",revenueData);
        return responseData;
    }

}
