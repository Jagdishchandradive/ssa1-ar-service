package com.micro.service;

import java.time.Duration;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ClientResponse;
import com.micro.bindings.CitizenAppRegistrationInputs;
import com.micro.entity.CitizenAppRegistrationEntity;
import com.micro.exception.ExternalServiceException;
import com.micro.exception.InvalidSSNException;
import com.micro.repository.IApplicationRegistrationRepository;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * Service Implementation for handling Citizen Application Registration. This
 * service integrates with an external API to validate SSN and determine the
 * state of the citizen.
 */

@Service
public class CARServiceImpl implements ICitizenApplicationRegistrationService {

	@Autowired
	private IApplicationRegistrationRepository citizenRepo;

//	@Autowired
//	private RestTemplate template;

	@Autowired
	private WebClient client;

	@Value("${ar.ssa-web.url}")
	private String endpointUrl;

	@Value("${ar.state}")
	private String targetState;

	/**
	 * Registers a citizen's application if the provided SSN is valid and belongs to
	 * the target state.
	 *
	 * @param inputs The citizen's application registration details.
	 * @return The generated application ID upon successful registration.
	 * @throws InvalidSSNException If the SSN is invalid, not found, or belongs to a
	 *                             different state.
	 */

	@Override
	public Integer registerCitizenApplication(CitizenAppRegistrationInputs inputs) throws InvalidSSNException {
//		--following we are using restTemplate--
//		ResponseEntity<String>response=template.exchange(endpointUrl,HttpMethod.GET,null,String.class,inputs.getSsn());

//		--following we are using WebClient--
//		// get state name
//		String stateName = client.get()
//								.uri(endpointUrl, inputs.getSsn())
//								.retrieve()
//								.bodyToMono(String.class)
//								.block();
//		// register the citizen if belong to CA state
//		if (stateName.equalsIgnoreCase(targetState)) {
//			// prepare Entity Object
//			CitizenAppRegistrationEntity entity = new CitizenAppRegistrationEntity();
//			BeanUtils.copyProperties(inputs, entity);
//			entity.setStateName(stateName);
//			// save the object
//			int appId = citizenRepo.save(entity).getAppId();
//			return appId;
//		}
//		throw new InvalidSSNException("Invalid SSN");

		/**** Refactored Code *****/
		// Validate SSN input (Ensure it's at least 9 digits)
		if (inputs.getSsn() == null || String.valueOf(inputs.getSsn()).length() < 9) {
			throw new InvalidSSNException("Invalid SSN: Must be at least 9 digits.");
		}
		// Fetch state name from external API
		String stateName = client.get()
				.uri(endpointUrl, inputs.getSsn())
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, response -> {
			       return Mono.error(new InvalidSSNException("Invalid SSN: No record found in external API."));
			    })
				.onStatus(HttpStatusCode::is5xxServerError, response -> {
			        return Mono.error(new ExternalServiceException("External service is unavailable. Please try again later."));
			    })
				.bodyToMono(String.class)
				.retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
					    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
					        new ExternalServiceException("SSA-WEB-API Service unavailable after multiple attempts. Please try again later.")))
				.block();

		// Handle null or unknown state response
		if (stateName == null || "UNKNOWN".equalsIgnoreCase(stateName)) {
			throw new RuntimeException("No response from external API or invalid state returned.");
		}



		// Register the citizen only if they belong to the target state
		if (stateName.equalsIgnoreCase(targetState)) {
			CitizenAppRegistrationEntity entity = new CitizenAppRegistrationEntity();
			BeanUtils.copyProperties(inputs, entity);
			entity.setStateName(stateName);
			return citizenRepo.save(entity).getAppId();
		}

		// If SSN is valid but state doesn't match, throw an exception
		throw new InvalidSSNException("Invalid SSN: Citizen does not belong to the Caifornia state.");

	}

}
