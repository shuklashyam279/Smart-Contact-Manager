package com.scm.demo.services.impl;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scm.demo.entity.Contact;
import com.scm.demo.helpers.ResourceNotFoundException;
import com.scm.demo.repositories.ContactRepo;
import com.scm.demo.services.ContactService;

@Service
public class ContactServiceImpl implements ContactService {

	private final ContactRepo contactRepo;

	@Autowired
	public ContactServiceImpl(ContactRepo contactRepo) {
		this.contactRepo = contactRepo;
	}

	@Override
	public Contact save(Contact contact) {

		String contactId = UUID.randomUUID().toString();
		contact.setId(contactId);
		return contactRepo.save(contact);

	}

	@Override
	public Contact update(Contact contact) {
		throw new UnsupportedOperationException("Unimplemented method 'update'");
	}

	@Override
	public List<Contact> getAll() {
		return contactRepo.findAll();
	}

	@Override
	public Contact getById(String id) {
		return contactRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));
	}

	@Override
	public void delete(String id) {
		var contact = contactRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));
		contactRepo.delete(contact);

	}

	@Override
	public List<Contact> search(String name, String email, String phoneNumber) {
		throw new UnsupportedOperationException("Unimplemented method 'search'");
	}

	@Override
	public List<Contact> getByUserId(String userId) {
		return contactRepo.findByUserId(userId);
	}
}
