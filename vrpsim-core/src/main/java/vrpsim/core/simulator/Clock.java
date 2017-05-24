/**
 * Copyright © 2016 Thomas Mayer (thomas.mayer@unibw.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vrpsim.core.simulator;

/**
 * @date 03.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class Clock implements IClock {

	private ITime simulationTime;

	public Clock() {
		this.simulationTime = new Time(0.0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.simulator.IClock#getCurrentSimulationTime()
	 */
	public ITime getCurrentSimulationTime() {
		return this.simulationTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.simulator.IClock#addTime(vrpsim.core.simulator.ITime)
	 */
	public void setSimulationTime(ITime time) {
		this.simulationTime = time;
	}

	public static class Time implements ITime {

		private double time;

		public Time(Double time) {
			this.time = time;
		}

		public double getTimeDouble() {
			return this.time;
		}
		
		@Override
		public int compareTo(ITime time) {
			this.checkTypeSafty(time);
			return Double.compare(this.time, ((Time) time).getTimeDouble());
		}

		@Override
		public ITime add(ITime time) throws ArithmeticException {
			this.checkTypeSafty(time);
			return new Time(this.time + ((Time) time).getTimeDouble());
		}

		@Override
		public ITime createTimeFrom(Double number) {
			return new Time(number);
		}

		private void checkTypeSafty(ITime time) throws ArithmeticException {
			if (!(time instanceof Time)) {
				throw new ArithmeticException(
						"Can not add " + time.getClass().getSimpleName() + " and " + this.getClass().getSimpleName());
			}
		}

		@Override
		public ITime sub(ITime time) throws ArithmeticException {
			this.checkTypeSafty(time);
			return new Time(this.time - ((Time) time).getTimeDouble());
		}

		@Override
		public ITime max(ITime time1, ITime time2) throws ArithmeticException {
			this.checkTypeSafty(time1);
			this.checkTypeSafty(time2);
			return new Time(Double.max((((Time) time1).getTimeDouble()), ((Time) time2).getTimeDouble()));
		}

		@Override
		public String getValue() {
			return Double.toString(time);
		}
		

		@Override
		public Double getDoubleValue() {
			return time;
		}

	}

}
