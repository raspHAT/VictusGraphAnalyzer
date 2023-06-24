package com.rasphat.data.legacy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class HandlerFactory {

    private static final String packagePath = "com.rasphat.archiveHandler.";
    /**
     * Creates an instance of a Handler subclass.
     *
     * @param handlerString The fully qualified name of a Handler subclass.
     * @return The created Handler instance, or null if the instance could not be created.
     * @throws IllegalArgumentException If the provided string is null or empty or if the Handler subclass
     * doesn't have a public default constructor.
     */

    public static ZipHandler createHandler(String handlerString) {



        if (handlerString == null || handlerString.isEmpty()) {
            throw new IllegalArgumentException("Handler string cannot be null or empty");
        }

        try {
            // Dynamically load the class
            String fullClassPath = packagePath+ handlerString;
            System.out.println(fullClassPath);
            Class<?> handlerClass = Class.forName(fullClassPath);

            // Check if the class has a default constructor
            Constructor<?> constructor = handlerClass.getDeclaredConstructor();
/*            if (!constructor.isAccessible()) {
                throw new IllegalArgumentException("Handler class must have a public default constructor");
            }*/

            // Create an instance of the class
            return (ZipHandler) constructor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}