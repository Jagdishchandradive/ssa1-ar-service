package com.micro.service;

import com.micro.bindings.CitizenAppRegistrationInputs;
import com.micro.exception.InvalidSSNException;

public interface ICitizenApplicationRegistrationService {
	public Integer registerCitizenApplication(CitizenAppRegistrationInputs inputs) throws InvalidSSNException;
}
