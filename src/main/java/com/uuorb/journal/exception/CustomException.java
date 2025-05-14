package com.uuorb.journal.exception;

import com.uuorb.journal.constant.ResultStatus;

public class CustomException extends Exception {

    private final ResultStatus customErrEnum;

    public CustomException(ResultStatus customErrEnum) {
        this.customErrEnum = customErrEnum;
    }

    public ResultStatus getCustomErrEnum() {
        return customErrEnum;
    }

}
