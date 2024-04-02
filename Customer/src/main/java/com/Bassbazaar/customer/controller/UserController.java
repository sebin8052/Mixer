package com.Bassbazaar.customer.controller;

import com.Bassbazaar.library.dto.AddressDto;
import com.Bassbazaar.library.dto.CustomerDto;
import com.Bassbazaar.library.model.Address;
import com.Bassbazaar.library.model.Customer;
import com.Bassbazaar.library.service.AddressService;
import com.Bassbazaar.library.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController
{
    private CustomerService customerService;

    private AddressService addressService;

    private PasswordEncoder passwordEncoder;
    public UserController(CustomerService customerService, AddressService addressService, PasswordEncoder passwordEncoder) {
        this.customerService = customerService;
        this.addressService = addressService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    public String getDashboard(@RequestParam(required = false) String tab, Model model, Principal principal, HttpSession session){
        if(principal==null){
            return "redirect:/login";
        }else{
            Customer customer = customerService.findByEmail(principal.getName());
            session.setAttribute("userLoggedIn",true);
            session.setAttribute("username", customer.getFirstName() + " " + customer.getLastName());
            if(tab!=null && !tab.isEmpty()) {
                model.addAttribute("openTab", tab);
                System.out.println(tab);
            }else{
                model.addAttribute("openTab", "");
            }
            model.addAttribute("customer",customer);
            model.addAttribute("title","Dashboard");
            return "dashboard";
        }
    }

    @GetMapping("/address")
    public String getAddress(Principal principal,
                             Model model){
        if(principal==null){
            return "redirect:/login";
        }else{
            Customer customer = customerService.findByEmail(principal.getName());
            List<Address> address = customer.getAddress();
            model.addAttribute("addressList",address);  //list of address
            model.addAttribute("size",address.size());  //no address
            model.addAttribute("addressDto",new AddressDto());
            return "address-content";
        }
    }

    @PostMapping("/add-address")
    public String getAddAddress(@ModelAttribute("addressDto")AddressDto addressDto,
                                Model model,Principal principal){
        addressService.save(addressDto, principal.getName());
        model.addAttribute("success","Address Added");
        return "redirect:/dashboard?tab=address";
    }

    @PostMapping("/add-address-checkout")
    public String AddAddress(@ModelAttribute("addressDto")AddressDto addressDto,
                             Model model, Principal principal, HttpServletRequest request){
        addressService.save(addressDto, principal.getName());
        model.addAttribute("success","Address Added");
        return "redirect:" + request.getHeader("Referer");
    }

    public String getUpdateAddress(@PathVariable("id")Long address_Id, Model model, Principal principal){
        if(principal==null){
            return "redirect:/login";
        }
        AddressDto addressDto=addressService.findById(address_Id);
        model.addAttribute("addressDto",addressDto);
        return"update-address";
    }

    @PostMapping("/update-address/{id}")
    public String updateAddress(@Valid @ModelAttribute("addressDto")AddressDto addressDto, Model model,
                                BindingResult result){
        if (result.hasErrors()) {
            model.addAttribute("addressDto", addressDto);
            return "update-address";
        }
        addressService.update(addressDto);
        model.addAttribute("success","Address updated");
        return "redirect:/dashboard?tab=address";
    }

    @GetMapping("/delete-address/{id}")
    public String deleteAddress(@PathVariable("id")Long address_id,Model model){
        addressService.deleteAddress(address_id);
        model.addAttribute("success","Address Deleted");
        return "redirect:/dashboard?tab=address";
    }

    @GetMapping("/enable-address/{id}")
    public String enableAddress(@PathVariable("id")long address_id,
                                RedirectAttributes redirectAttributes){
        addressService.enable(address_id);
        redirectAttributes.addFlashAttribute("success","Address enabled");
        return "redirect:/dashboard?tab=address";
    }

    @GetMapping("/disable-address/{id}")
    public String disableAddress(@PathVariable("id")long address_id,
                                 RedirectAttributes redirectAttributes){
        addressService.disable(address_id);
        redirectAttributes.addFlashAttribute("success","Address disabled");
        return "redirect:/dashboard?tab=address";
    }


    /* Account details in the Customer */


}
