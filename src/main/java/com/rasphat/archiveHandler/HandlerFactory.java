package com.rasphat.archiveHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class HandlerFactory {

    public static Handler createHandler(String handlerString) {
        if (handlerString == null || handlerString.isEmpty()) {
            throw new IllegalArgumentException("Handler string cannot be null or empty");
        }

        try {
            // Dynamisch Klasse laden
            Class<?> handlerClass = Class.forName(handlerString);

            // Überprüfen, ob der Klasse einen Standard-Konstruktor hat
            Constructor<?> constructor = handlerClass.getDeclaredConstructor();
            if (!constructor.isAccessible()) {
                throw new IllegalArgumentException("Handler class must have a public default constructor");
            }

            // Instanz der Klasse erstellen
            return (Handler) constructor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}
