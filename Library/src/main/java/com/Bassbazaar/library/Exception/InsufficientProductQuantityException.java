package com.Bassbazaar.library.Exception;

public class InsufficientProductQuantityException  extends RuntimeException
{

    public InsufficientProductQuantityException(String message)
    {
        super(message);
    }
}
