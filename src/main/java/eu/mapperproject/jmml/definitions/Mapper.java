/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.definitions;

/**
 * Definition of a mapper.
 * 
 * @author Joris Borgdorff
 */
public class Mapper {
	public enum Type {
		FAN_IN, FAN_OUT;
	}
	
	private Type type;
	
}
