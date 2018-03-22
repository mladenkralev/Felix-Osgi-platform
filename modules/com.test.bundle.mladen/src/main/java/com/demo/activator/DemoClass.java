package com.demo.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

class DemoClass implements BundleActivator {


    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Working");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("did not work");
    }
}