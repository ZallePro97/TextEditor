package com.plugins;

import java.util.ServiceLoader;

public class PluginFactory {

    public static ServiceLoader<Plugin> getPlugins() {
        ServiceLoader<Plugin> concretePlugins = ServiceLoader.load(Plugin.class);

        return concretePlugins;
    }

}
