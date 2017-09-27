package com.seoul.publicbooksearcher.domain.usecase;

public interface UseCase <Result, Argument> {

    Result execute(Argument arg);

}
