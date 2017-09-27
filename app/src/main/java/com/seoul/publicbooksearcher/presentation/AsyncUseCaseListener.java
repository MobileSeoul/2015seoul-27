package com.seoul.publicbooksearcher.presentation;

public interface AsyncUseCaseListener<Before, After, Error>{
    void onBefore(Before beforeArgs);
    void onAfter(After afterArg);
    void onError(Error e);
}
