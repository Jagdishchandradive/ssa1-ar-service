package com.micro.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidSSNException extends  RuntimeException {
//	long fieldValue;
	
	public InvalidSSNException() {
		super();
	}
	public InvalidSSNException(String message) {
//		super(String.format("%s = Invalid for California State", fieldValue));
//       this.fieldValue = fieldValue;
		super(message);
		
	} 

}
