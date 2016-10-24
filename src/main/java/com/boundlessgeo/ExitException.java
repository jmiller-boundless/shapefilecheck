package com.boundlessgeo;

import org.springframework.boot.ExitCodeGenerator;

public class ExitException extends RuntimeException implements ExitCodeGenerator {

	public ExitException(String string) {
		super(string);
	}

	@Override
	public int getExitCode() {
		return 10;
	}

}
