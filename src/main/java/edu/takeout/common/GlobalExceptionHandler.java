package edu.takeout.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    Result<String> exceptionHandler(SQLIntegrityConstraintViolationException e) {
        if (e.getMessage().contains("Duplicate entry")) {
            String msg = e.getMessage().split(" ")[3] + " exist";
            return Result.error(msg);
        }
        return Result.error("unknow error");
    }

    @ExceptionHandler(CustomException.class)
    Result<String> customExceptionHandler(CustomException e) {
        return Result.error(e.getMessage());
    }
}
