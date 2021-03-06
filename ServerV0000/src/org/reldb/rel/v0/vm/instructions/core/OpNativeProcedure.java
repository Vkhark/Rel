/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.core;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;
import org.reldb.rel.v0.vm.NativeProcedure;

public final class OpNativeProcedure extends Instruction {

	private NativeProcedure procedure;
	private int parmCount;
	
	public OpNativeProcedure(NativeProcedure procedure, int parmCount) {
		this.procedure = procedure;
		this.parmCount = parmCount;
	}
	
	public final void execute(Context context) {
		context.userProcedure(procedure, parmCount);
	}
}