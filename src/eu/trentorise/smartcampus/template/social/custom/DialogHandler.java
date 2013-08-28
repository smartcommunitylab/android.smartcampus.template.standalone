/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.template.social.custom;

/**
 * Used to pass the control from the dialog back to the context where it was called.
 * @author raman
 *
 * @param <T>
 */
public interface DialogHandler<T> {

	/**
	 * called when the dialog is positively confirmed
	 * @param result
	 */
	public void handleSuccess(T result);
}
