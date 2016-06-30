/**
 * Copyright (C) 2016 Thomas Mayer (thomas.mayer@unibw.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vrpsim.core.model.util.exceptions;

/**
 * @date 23.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class BehaviourException extends Exception {

	private static final long serialVersionUID = -3404525830636525929L;
	
	public BehaviourException(String msg) {
		super(msg);
	}

}