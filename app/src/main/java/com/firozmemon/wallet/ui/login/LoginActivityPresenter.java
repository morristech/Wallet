package com.firozmemon.wallet.ui.login;

import com.firozmemon.wallet.database.DatabaseRepository;
import com.firozmemon.wallet.models.Login;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by firoz on 14/5/17.
 */

public class LoginActivityPresenter {

    private LoginActivityView view;
    private DatabaseRepository databaseRepository;
    private Scheduler mainScheduler;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public LoginActivityPresenter(LoginActivityView view, DatabaseRepository databaseRepository, Scheduler mainScheduler) {
        this.view = view;
        this.databaseRepository = databaseRepository;
        this.mainScheduler = mainScheduler;
    }

    public void createAccountClicked() {
        view.goToCreateAccountActivity();
    }

    public void signInClicked(Login login) {
        compositeDisposable.add(databaseRepository.checkLoginCredentials(login)
                .subscribeOn(Schedulers.io())
                .observeOn(mainScheduler)
                .subscribeWith(new DisposableSingleObserver<Integer>() {
                    @Override
                    public void onSuccess(@NonNull Integer integerVal) {
                        if (integerVal != -1)
                            view.goToNextActivity(integerVal);
                        else
                            view.displayError("Invalid Credentials");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.displayError(e.getMessage());
                    }
                }));
    }

    public void unsubscribe() {
        compositeDisposable.clear();
    }
}
