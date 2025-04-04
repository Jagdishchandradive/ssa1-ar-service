package com.micro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.micro.bindings.CitizenAppRegistrationInputs;
import com.micro.service.ICitizenApplicationRegistrationService;

@RestController
@RequestMapping("/citizen_app_reg-api")
public class CAROperationsController {

	@Autowired
	private ICitizenApplicationRegistrationService registrationService;

//
//	@PostMapping("/save")
//	public ResponseEntity<String> saveCitizenApp(@RequestBody CitizenAppRegistrationInputs inputs) {
//		try {
//			// user service
//			int appId = registrationService.registerCitizenApplication(inputs);
//			if (appId > 0)
//				return new ResponseEntity<String>("Citizen Application is registered with the Id::" + appId,
//						HttpStatus.CREATED);
//			else
//				return new ResponseEntity<String>(
//						"Citizen with SSN " + inputs.getSsn() + " not belongs to California State", HttpStatus.OK);
//		} catch (Exception e) {
//			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
//		}
//
//	}
	@PostMapping("/save")
	public ResponseEntity<String> saveCitizenApp(@RequestBody CitizenAppRegistrationInputs inputs)throws Exception {
		// user service
		int appId = registrationService.registerCitizenApplication(inputs);

		return new ResponseEntity<String>("Citizen Application is registered with the Id::" + appId,
				HttpStatus.CREATED);

	}

}
