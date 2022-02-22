package me.yangxiaobin.androidapp.aspect;

import android.os.Bundle;
import kotlin.jvm.functions.Function1;
import me.yangxiaobin.androidapp.util.LoggerKt;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class JavaAspect {

    private static final String TAG = "JavaAspect";

    private final Function1<String, Integer> logD = LoggerKt.logCurried(TAG);

    // Application aspect
    @Before("execution(* android.app.Application.onCreate())")
    public void doBeforeApplicationOnCreate() {
        logD.invoke(" before Application onCreate");
    }


    // Activity aspect
    // logcat output :
    //     ===>  before Activity onCreate, bundle : execution(void me.yangxiaobin.androidapp.MainActivity.onCreate(Bundle))
    //     ===>  before Activity onCreate, bundle : execution(void androidx.fragment.app.FragmentActivity.onCreate(Bundle))
    //     ===>  before Activity onCreate, bundle : execution(void androidx.activity.ComponentActivity.onCreate(Bundle))
    //     ===>  before Activity onCreate, bundle : execution(void androidx.core.app.ComponentActivity.onCreate(Bundle))
    @Before("execution(* android.app.Activity.onCreate(..))")
    public void doBeforeActivityOnCreate(JoinPoint joinPoint) {
        logD.invoke(" before Activity onCreate, bundle : " + joinPoint);
    }

    @Before("execution(* me.yangxiaobin.androidapp.MainActivity.onCreate(android.os.Bundle))")
    public void doBeforeMyActivityOnCreate(JoinPoint joinPoint) {
        logD.invoke(" before Activity onCreate, bundle : " + ((Bundle) joinPoint.getArgs()[0]));
    }

    @Before("execution(* android.app.Activity.onDestroy(..))")
    public void doBeforeActivityOnDestroy() {
        logD.invoke(" before Activity OnDestroy");
    }

    // Fragment aspect
    @Before("execution(* androidx.fragment.app.Fragment.onCreate(..))")
    public void doBeforeFragmentOnDestroy() {
        logD.invoke(" before Fragment onCreate");
    }


}
