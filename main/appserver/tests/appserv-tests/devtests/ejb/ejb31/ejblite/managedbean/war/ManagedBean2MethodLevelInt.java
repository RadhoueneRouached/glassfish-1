package com.acme;

import javax.annotation.*;
import javax.interceptor.Interceptors;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

@ManagedBean("ManagedBean2MethodLevelInt")
public class ManagedBean2MethodLevelInt extends ManagedBeanSuper {

    static private int numInstances = 0;
    static private int numInterceptorInstances = 0;

    private String aroundInvoke = "";

    public void newInterceptorInstance() {
	numInterceptorInstances++;
    }

    public int getNumInstances() {
	return numInstances;
    }

    public int getNumInterceptorInstances() {
	return numInterceptorInstances;
    }

    @Interceptors({InterceptorA.class, InterceptorB.class})
    public String getAroundInvokeSequence() {
	return "";
    }


    @PostConstruct
    private void init() {
	numInstances++;
    }

    @PreDestroy
    private void destroy() {
    }

}