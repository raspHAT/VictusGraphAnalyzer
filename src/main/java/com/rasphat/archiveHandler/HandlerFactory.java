/*
 * HandlerFactory class provides a mechanism to create an instance of a Handler subclass dynamically.
 *
 * <p>This class is intended to be used when the specific subclass of Handler to be used
 * is not known at compile time and needs to be determined dynamically.</p>
 *
 * <p>The class assumes that the string passed to createHandler method represents a fully qualified name
 * of a Handler subclass. It also assumes that this subclass has a public default constructor.</p>
 *
 * <p>The createHandler method throws an IllegalArgumentException in two cases:</p>
 * <ul>
 *     <li>When the provided string is null or empty</li>
 *     <li>When the handler class doesn't have a public default constructor</li>
 * </ul>
 *
 * <p>If any other exception occurs during the instantiation process, it is caught and the stack trace
 * is printed. The method returns null in this case.</p>
 *
 * <p>Note: It is important to ensure the control or validation of the string passed to the createHandler
 * method. As this class uses reflection, it has the potential to allow arbitrary code execution
 * if the provided string is not carefully controlled or validated. This class can load and instantiate
 * any class if its name is passed to this method.</p>
 *
 * @author Markus
 * @version 1.0
 * @since 2023-06-15
 */
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