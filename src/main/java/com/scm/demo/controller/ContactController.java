package com.scm.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scm.demo.entity.Contact;
import com.scm.demo.entity.User;
import com.scm.demo.forms.ContactForm;
import com.scm.demo.helpers.Helper;
import com.scm.demo.helpers.Message;
import com.scm.demo.helpers.MessageType;
import com.scm.demo.services.ContactService;
import com.scm.demo.services.ImageService;
import com.scm.demo.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.util.UUID;
import org.slf4j.Logger;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(ContactController.class);

    private final ContactService contactService;
    private final ImageService imageService;
    private final UserService userService;

    @Autowired
    public ContactController(ContactService contactService, ImageService imageService, UserService userService) {
        this.contactService = contactService;
        this.imageService = imageService;
        this.userService = userService;
    }

    @RequestMapping("/add")
    // add contact page: handler
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();
        contactForm.setFavorite(true);
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult result,
            Authentication authentication, HttpSession session) {

        // process the form data
        // 1 validate form
        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> logger.info(error.toString()));
            session.setAttribute("message", Message.builder()
                    .content("Please correct the following errors")
                    .type(MessageType.red)
                    .build());
            return "user/add_contact";
        }

        String username = Helper.getEmailOfLoggedInUser(authentication);
        // form ---> contact

        User user = userService.getUserByEmail(username);
        // 2 process the contact picture
        String filename = UUID.randomUUID().toString();
        String fileURL = imageService.uploadImage(contactForm.getContactImage(), filename);
        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setFavorite(contactForm.isFavorite());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setUser(user);
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setPicture(fileURL);
        contact.setCloudinaryImagePublicId(filename);
        contactService.save(contact);
        System.out.println(contactForm);

        // 3 set the contact picture url

        // 4 set message to be displayed on the view
        session.setAttribute("message",
                Message.builder()
                        .content("You have successfully added a new contact")
                        .type(MessageType.green)
                        .build());
        return "redirect:/user/contacts/add";
    }
}