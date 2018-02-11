/*******************************************************************************
 * Reynolds FMS
 * Copyright (c) 2015 Matthew Reynolds
 * 
 * This product was developed at The Alpha Dogs (www.4946.ca).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package ca._4946.mreynolds.customSwing;

/**
 * An interface to allow the complete replacement of all fields of an object
 * with the values from the same fields of another object. In essence, this
 * function allows you to perform an object assignment (
 * {@code existingObject = objWithNewValues}) without changing the reference,
 * only the object's values.
 * 
 * @author Matthew Reynolds
 * 
 * @param <E>
 *            the type of object that is allowed to replace the current object.
 *            This is almost always the same as the object implementing this
 *            interface (For example:
 *            {@code class Example implements Replaceable<Example>})
 */
public abstract interface Replaceable<E> {

	/**
	 * Replace the current Object's fields with the values from the specified
	 * replacement.
	 * <p>
	 * All implementations of this method are expected to set all of the
	 * object's desires fields to the values of the same field in
	 * 
	 * @param replacement
	 *            the object to load the new values from
	 * @return {@code true} if the replacement succeeded, otherwise {@code false}
	 */
	public abstract boolean replaceWith(E replacement);
}
