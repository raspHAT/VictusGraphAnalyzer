package com.rasphat.archiveHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class HandlerFactory {

    /**
     * Creates an instance of a Handler subclass.
     *
     * @param handlerString The fully qualified name of a Handler subclass.
     * @return The created Handler instance, or null if the instance could not be created.
     * @throws IllegalArgumentException If the provided string is null or empty or if the Handler subclass
     * doesn't have a public default constructor.
     */

    public static Handler createHandler(String handlerString) {

        if (handlerString == null || handlerString.isEmpty()) {
            throw new IllegalArgumentException("Handler string cannot be null or empty");
        }

        try {
            // Dynamically load the class
            Class<?> handlerClass = Class.forName(handlerString);

            // Check if the class has a default constructor
            Constructor<?> constructor = handlerClass.getDeclaredConstructor();
            if (!constructor.isAccessible()) {
                throw new IllegalArgumentException("Handler class must have a public default constructor");
            }

            // Create an instance of the class
            return (Handler) constructor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}