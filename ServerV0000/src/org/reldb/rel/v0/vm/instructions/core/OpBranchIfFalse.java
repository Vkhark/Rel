package org.reldb.rel.v0.vm.instructions.core;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpBranchIfFalse extends Instruction {

	private int address;
	
	public OpBranchIfFalse(int address) {
		this.address = address;
	}
	
	public final void execute(Context context) {
		context.branchIfFalse(address);
	}
	
	public String toString() {
		return getName() + " " + address;
	}

}
