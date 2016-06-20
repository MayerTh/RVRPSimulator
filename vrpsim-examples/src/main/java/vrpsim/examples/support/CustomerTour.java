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
/**
 * 
 */
package vrpsim.examples.support;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @date 25.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
@XmlRootElement
public class CustomerTour {

	private List<String> customerIds;

	public CustomerTour() {
	}

	public CustomerTour(List<String> customerIds) {
		this.customerIds = customerIds;
	}

	@XmlElementWrapper(name = "CustomerIds")
	@XmlElement(name = "Id")
	public List<String> getCustomerIds() {
		return this.customerIds;
	}
	
	public void setCustomerIds(List<String> customerIds) {
		this.customerIds = customerIds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.customerIds.toString();
	}

}
