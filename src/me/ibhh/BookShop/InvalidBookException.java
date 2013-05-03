/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ibhh.BookShop;

/**
 *
 * @author Simon
 */
public class InvalidBookException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidBookException(String msg) {
        super(msg);
    }
}
