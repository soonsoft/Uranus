package com.soonsoft.uranus.services.dictionary.exceptions;

import com.soonsoft.uranus.core.error.ApplicationException;

/**
 * Dictionary module specific exception.
 */
public class DictionaryException extends ApplicationException {

    private static final String DEFAULT_ERROR_CODE = "DICTIONARY_ERROR";

    public DictionaryException(String errorCode, String message) {
        super(errorCode, message);
    }

    public DictionaryException(String message) {
        super(DEFAULT_ERROR_CODE, message);
    }

    public DictionaryException(String message, Throwable cause) {
        super(DEFAULT_ERROR_CODE, message, cause);
    }
}
