package vrpsim.instance.test;

import java.math.BigInteger;

import vrpsim.instance.generator.config.IIdGenerator;

public class IdGeneratorForTest implements IIdGenerator {

	private BigInteger id = BigInteger.ZERO; 
	
	public BigInteger generateId() {
		id = id.add(BigInteger.ONE);
		return id;
	}

}
