package com.anstrat.server.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import com.google.common.collect.Maps;

public class DependencyInjector {

	private Map<Class<?>, Class<?>> binds = Maps.newHashMap();	 // Maps interface to implementing class
	private Map<Class<?>, Object> instances = Maps.newHashMap(); // Maps interfaces to their implementing object instance
	private String packageName;
	
	/**
	 * @param packageName only injects dependencies in this package.
	 */
	public DependencyInjector(String packageName){
		this.packageName = packageName;
		
		// Allow injecting of self
		instances.put(getClass(), this);
	}
	
	/**
	 * Binds an interface to its implementation.
	 */
	public <T> void bind(Class<T> source, Class<? extends T> target){
		binds.put(source, target);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> clazz) {
		try{
			// Have we already created an instance of this class before?
			if(!instances.containsKey(clazz)){
				Object instance = clazz.newInstance();
				instances.put(clazz, instance);
				injectDependencies(clazz, instance);
			}
		}
		catch(Exception e){
			throw new RuntimeException(String.format("Failed to inject dependencies of class %s: %s", clazz, e.getMessage()), e);
		}
		
		// Return instance
		return (T) instances.get(clazz);
	}
	
	/**
	 * Searches for @Inject annotation in the given class and all of it's descendant fields injecting<br>
	 * dependencies.
	 */
	public void injectDependencies(Object obj){
		try{
			injectDependencies(obj.getClass(), obj);
		} 
		catch (Exception e){
			throw new RuntimeException(String.format("Failed to inject dependencies of class %s: %s", obj.getClass(), e.getMessage()), e);
		}
	}
	
	private void injectDependencies(Class<?> clazz, Object instance) throws InstantiationException, IllegalAccessException{

		// Recursively searches the given class's fields for @Inject annotations, travelling down into
		// the classes of each field.
		for(Field field : clazz.getDeclaredFields()){
			
			// Don't check static fields, fields with a pimitive type or fields outside the given package.
			if(isStatic(field) || field.getType().isPrimitive() || !clazz.getPackage().getName().startsWith(packageName)) continue;
			
			field.setAccessible(true);		// Requires to read/write private fields
			
			//System.out.println(String.format("Checking field: %s %s, static = %s", field.getType(), field.getName(), isStatic(field)));
			
			// Found an @Inject annotation, create or get an instance of this field
			if(field.getAnnotation(Inject.class) != null){
				
				Class<?> fieldType = field.getType();
				//System.out.println(String.format("Found @Inject field of type: %s", fieldType));
				
				// If an instance does not already exist, create one and inject it's dependencies
				if(!instances.containsKey(fieldType)){
					
					//System.out.println(String.format("No existing instance of %s found, creating a new one.", fieldType));
					
					// Find which class we're supposed to create
					Class<?> implementingClass = binds.get(fieldType);
					if(implementingClass == null) throw new RuntimeException(String.format("No class bound to interface %s.", fieldType));
					//System.out.println(String.format("Found %s that implements the %s interface.", implementingClass, fieldType));
					
					// Create the instance, recursively checking this field instance's fields for more dependencies
					Object fieldInstance = implementingClass.newInstance();
					instances.put(fieldType, fieldInstance);
					injectDependencies(implementingClass, fieldInstance);
				}

				// Set field
				field.set(instance, instances.get(fieldType));
			}
			else if(field.get(instance) != null){
				// We still need to travel down through all other fields, in case there's an @Inject annotation
				// hidden deep down somewhere.
				injectDependencies(field.getType(), field.get(instance));
			}
		}
	}
	
	private static boolean isStatic(Field field){
		return (field.getModifiers() & Modifier.STATIC) == Modifier.STATIC;
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Inject {
		
	}
}
