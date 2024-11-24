package com.scm.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scm.demo.entity.Contact;
import com.scm.demo.services.ContactService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final ContactService contactService;

    public ApiController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contacts/{contactId}")
    public Contact getContact(@PathVariable String contactId) {
        return contactService.getById(contactId);
    }

}