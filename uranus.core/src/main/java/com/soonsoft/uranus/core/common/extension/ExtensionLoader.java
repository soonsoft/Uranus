package com.soonsoft.uranus.core.common.extension;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.soonsoft.uranus.core.common.lang.ClassUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.common.utils.Holder;

/**
 * ExtensionLoader loader = ExtensionLoader.load(Interface.class);
 * Class instance = loader.getInstance(key); 
 * Class[] instances = loader.getInstances(); 
 * Class instance = loader.getInstance(index);
 */
public class ExtensionLoader<T> {

    //private static final Pattern NAME_SEPARATOR = Pattern.compile("\\s*[,]+\\s*");

    private static final String SERVICES_DIRECTORY = "META-INF/services/";

    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_MANAGER_BAG = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    private static ILoadingStrategy SERVICES_STRATEGY = () -> SERVICES_DIRECTORY;
    private static ILoadingStrategy[] strategies = new ILoadingStrategy[] { SERVICES_STRATEGY };

    private final Class<?> type;
    private final ConcurrentMap<Class<?>, String> cachedNames = new ConcurrentHashMap<>();
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();
    private final ConcurrentMap<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();
    private String cachedDefaultName;
    private Map<String, IllegalStateException> exceptions = new HashMap<>();

    public static void setLoadingStrategies(ILoadingStrategy... strategies) {
        ExtensionLoader.strategies = strategies;
    }

    private ExtensionLoader(Class<?> type) {
        this.type = type;
        cacheDefaultExtensionName();
    }

    @SuppressWarnings("unchecked")
    public static <T> ExtensionLoader<T> load(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type (" + type + ") is not an interface!");
        }
        if (!type.isAnnotationPresent(SPI.class)) {
            throw new IllegalArgumentException("Extension type (" + type +
                    ") is not an extension, because it is NOT annotated with @" + SPI.class.getSimpleName() + "!");
        }

        ExtensionLoader<T> manager = (ExtensionLoader<T>) EXTENSION_MANAGER_BAG.get(type);
        if (manager == null) {
            EXTENSION_MANAGER_BAG.putIfAbsent(type, new ExtensionLoader<T>(type));
            manager = (ExtensionLoader<T>) EXTENSION_MANAGER_BAG.get(type);
        }
        return manager;
    }

    public T getInstance() {
        return getInstance(cachedDefaultName);
    }

    @SuppressWarnings("unchecked")
    public T getInstance(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Extension name == null");
        }
        final Holder<Object> holder = getOrCreateHolder(name);
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    public T[] getAllInstances() {
        return null;
    }

    @SuppressWarnings("unchecked")
    private T createExtension(String name) {
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw findException(name);
        }
        try {
            T instance = (T) EXTENSION_INSTANCES.get(clazz);
            if (instance == null) {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.getDeclaredConstructor().newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            }
            return instance;
        } catch (Throwable t) {
            throw new IllegalStateException("Extension instance (name: " + name + ", class: " +
                    type + ") couldn't be instantiated: " + t.getMessage(), t);
        }
    }

    //#region 读取配置文件，加载Class

    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.get();
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = loadExtensionClasses(type, strategies);
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private Map<String, Class<?>> loadExtensionClasses(Class<?> type, ILoadingStrategy... strategies) {
        Map<String, Class<?>> extensionClasses = new LinkedHashMap<>();

        if(strategies != null) {
            for(ILoadingStrategy strategy : strategies) {
                loadDirectory(extensionClasses, type, strategy);
            }
        }

        return extensionClasses;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses, Class<?> type, ILoadingStrategy strategy) {
        String className = type.getName();
        String fileName = strategy.directory() + className;
        try {
            Enumeration<java.net.URL> urls = null;
            ClassLoader classLoader = ClassUtils.getClassLoader(ExtensionLoader.class);
            
            // try to load from ExtensionLoader's ClassLoader first
            if (strategy.preferExtensionClassLoader()) {
                ClassLoader extensionLoaderClassLoader = ExtensionLoader.class.getClassLoader();
                if (ClassLoader.getSystemClassLoader() != extensionLoaderClassLoader) {
                    urls = extensionLoaderClassLoader.getResources(fileName);
                }
            }
            
            if(urls == null || !urls.hasMoreElements()) {
                if (classLoader != null) {
                    urls = classLoader.getResources(fileName);
                } else {
                    urls = ClassLoader.getSystemResources(fileName);
                }
            }

            if (urls != null) {
                List<ExtensionInfo> extensionInfoList = new ArrayList<>();
                List<ExtensionInfo> arrayExtension  = new ArrayList<>();

                while (urls.hasMoreElements()) {
                    java.net.URL resourceURL = urls.nextElement();
                    loadResource(type, extensionInfoList, resourceURL, arrayExtension, strategy.excludedPackages());
                }
                
                if(!arrayExtension.isEmpty()) {
                    extensionInfoList.addAll(arrayExtension);
                }

                extensionInfoList.forEach(info -> {
                    String line = info.extensionClassName;
                    String name = info.name;
                    try {
                        Class<?> clazz = Class.forName(line, true, classLoader);
                        if (!type.isAssignableFrom(clazz)) {
                            throw new IllegalStateException("Error occurred when loading extension class (interface: " +
                                    type + ", class line: " + clazz.getName() + "), class "
                                    + clazz.getName() + " is not subtype of interface.");
                        }

                        clazz.getConstructor();

                        cacheName(clazz, name);
                        saveInExtensionClass(extensionClasses, clazz, name);
                    } catch (Throwable t) {
                        IllegalStateException e = new IllegalStateException("Failed to load extension class (class line: " + line + ") in " + info.resourceURL + ", cause: " + t.getMessage(), t);
                        exceptions.put(line, e);
                    }
                });
            }
        } catch (Throwable t) {
            throw new ExtensionClassLoadException(
                "Exception occurred when loading extension class (interface: " + className + ", description file: " + fileName + ").", t);
        }
    }

    private void loadResource(
        Class<?> type,
        List<ExtensionInfo> extensionInfoList,
        java.net.URL resourceURL, 
        List<ExtensionInfo> arrayExtension,
        String... excludedPackages) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceURL.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final int ci = line.indexOf('#');
                if (ci >= 0) {
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.length() > 0) {
                    String name = null;
                    List<ExtensionInfo> extensionInfoPackage = extensionInfoList;
                    if(line.charAt(0) == '-') {
                        name = String.valueOf(arrayExtension.size());
                        line = line.substring(1).trim();
                        extensionInfoPackage = arrayExtension;
                    } else {
                        int i = line.indexOf('=');
                        if (i > 0) {
                            name = line.substring(0, i).trim();
                            line = line.substring(i + 1).trim();
                        }
                    }
                    if (line.length() > 0 && !isExcluded(line, excludedPackages)) {
                        ExtensionInfo extensionInfo = new ExtensionInfo();
                        extensionInfo.name = name;
                        extensionInfo.extensionClassName = line;
                        extensionInfo.resourceURL = resourceURL;

                        extensionInfoPackage.add(extensionInfo);
                    }
                }
            }
        } catch(Throwable t) {
            throw new ExtensionClassLoadException(
                "Exception occurred when loading extension class in " + resourceURL, t);
        }
    }

    private static boolean isExcluded(String className, String... excludedPackages) {
        if (excludedPackages != null) {
            for (String excludePackage : excludedPackages) {
                if (className.startsWith(excludePackage + ".")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void cacheName(Class<?> clazz, String name) {
        if (!cachedNames.containsKey(clazz)) {
            cachedNames.put(clazz, name);
        }
    }

    private void saveInExtensionClass(Map<String, Class<?>> extensionClasses, Class<?> clazz, String name) {
        Class<?> c = extensionClasses.get(name);
        if (c == null) {
            extensionClasses.put(name, clazz);
        } else if (c != clazz) {
            String duplicateMsg = "Duplicate extension " + type.getName() + " name " + name + " on " + c.getName() + " and " + clazz.getName();
            throw new IllegalStateException(duplicateMsg);
        }
    }

    //#endregion

    private Holder<Object> getOrCreateHolder(String name) {
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        return holder;
    }

    private void cacheDefaultExtensionName() {
        final SPI defaultAnnotation = type.getAnnotation(SPI.class);
        if (defaultAnnotation == null) {
            return;
        }

        String value = defaultAnnotation.value();
        if ((value = value.trim()).length() > 0) {
            cachedDefaultName = value;
        }
    }

    private IllegalStateException findException(String name) {
        return new IllegalStateException();
    }

    private class ExtensionInfo {
    
        private String name;
    
        private String extensionClassName;

        private java.net.URL resourceURL;

    }

}