package com.jgmagalop.aidocanalyzer.shared.exception;

public class MaxUploadSizeExceededException extends  RuntimeException{
    public MaxUploadSizeExceededException(String message){
        super(message);
    }
}
