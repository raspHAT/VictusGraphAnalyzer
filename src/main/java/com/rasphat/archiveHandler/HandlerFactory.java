package com.rasphat.archiveHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class HandlerFactory {

    public static Handler createHandler(String handlerString) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        // Dynamisch Klasse laden
        Class<?> handlerClass = Class.forName(handlerString);

        // Standard-Konstruktor abrufen
        Constructor<?> constructor = handlerClass.getDeclaredConstructor();

        // Instanz der Klasse erstellen

        return (Handler) constructor.newInstance();
    }
}
