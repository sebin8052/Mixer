package com.Bassbazaar.library.Exception;

public class CustomerNameAlreadyExistsException extends RuntimeException{

    public CustomerNameAlreadyExistsException(String message)
    {
        super(message);
    }
}
