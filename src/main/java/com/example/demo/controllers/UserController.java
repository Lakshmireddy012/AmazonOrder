package com.example.demo.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.controllers.automation.PlaceOrder;

@RestController
public class UserController {

	@CrossOrigin(origins = "http://localhost:4200")
	@GetMapping("/placeorder")
    public boolean placeOrder() {
		PlaceOrder placeOrderObj=new PlaceOrder();
		Thread placeOrderThread=new Thread(placeOrderObj);
		placeOrderThread.start();
        return true;
    }
	
	@PostMapping("/users")
    void addUser(@RequestBody String str) {
        
    }
}
