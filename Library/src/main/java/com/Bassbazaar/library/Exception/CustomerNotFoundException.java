package com.Bassbazaar.library.Exception;

// used in resetpassword

public class CustomerNotFoundException extends Exception
{
    public CustomerNotFoundException(String message){
        super(message);
    }
}
