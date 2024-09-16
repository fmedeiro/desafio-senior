package com.desafiosenior.api_hotel.util;

import java.lang.reflect.Method;

public class AttributeChecker {

    public <T> String getFirstAttributePresent(T obj, String... attributes) {
        for (String attribute : attributes) {
            try {
                String methodName = getMethod(obj, attribute);
                
                // Acessar o metodo usando reflection, independentemente da classe T
                Method method = obj.getClass().getMethod(methodName);
                Object value = method.invoke(obj);

                if (value != null && !(value.toString().isBlank())) {
                    return attribute.toUpperCase();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

	private <T> String getMethod(T obj, String attribute) {
		String methodName = createMethod(obj, attribute);
		
		return methodName;
	}

	private <T> String createMethod(T obj, String attribute) {
		Class<?> clazz = obj.getClass();
		if (clazz.isRecord()) {
			return attribute.intern();
		} else if (isClass(clazz)) {
			return "get" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
		}
		return null;
	}

	private boolean isClass(Class<?> clazz) {
		return !clazz.isAnnotation() && !clazz.isEnum() && !clazz.isInterface() && !clazz.isRecord();
	}
}
