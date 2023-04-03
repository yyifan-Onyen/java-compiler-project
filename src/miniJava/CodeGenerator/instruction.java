package miniJava.CodeGenerator;

public class instruction {
	/**
	 * Example illustrating use of the mJAM package
	 * @author prins
	 * @version COMP 520 v2.3
	 * 
	 * This example illustrates the interface from the code generator to mJAM
	 * using the Counter.java example studied in class
	 */
	//package codegenExample;
	// for code generator
	//import mJAM.Machine;
	//import mJAM.Machine.*;
	//import mJAM.ObjectFile;

	// to run debugger on object code
	//import mJAM.Disassembler;
	//import mJAM.Interpreter;

	// test class to construct mJAM object code for miniJava 
	// example program  Counter.java
	//public class CodeGen
	//{
		//public static void main(String[] args){
			
			//Machine.initCodeGen();
			//System.out.println("Generating code for Counter.java example");
			
		/*
		 * Preamble:
		 *   generate call to main
		 *   
		 */
			//Machine.emit(Op.LOADL,0);            // array length 0
			//Machine.emit(Prim.newarr);           // empty String array argument
			//int patchAddr_Call_main = Machine.nextInstrAddr();  // record instr addr where main is called                                                
			//Machine.emit(Op.CALL,Reg.CB,-1);     // static call main (address to be patched)
			//Machine.emit(Op.HALT,0,0,0);         // end execution
			
			
		/*
		 * Methods of class Counter
		 * 
		 */
			
			/*
			 *	  public void increase(int k) {
			 * 		  count = count + k;
			 *    }
			 */
			//int codeAddr_increase = Machine.nextInstrAddr();  // record code addr of "increase"
	/* increase: */
			//Machine.emit(Op.LOAD,Reg.OB,0);      // current value of count
			//Machine.emit(Op.LOAD,Reg.LB,-1);     // current value of k
			//Machine.emit(Prim.add);              // count + k
			//Machine.emit(Op.STORE,Reg.OB,0);     // update count
			//Machine.emit(Op.RETURN,0,0,1);       // return popping one caller arg (d = 1) 
			                                     // and returning no value (n = 0)
		
			
			/*
			 *    public static void main(String [] args){
			 *        Counter counter = new Counter();
			 *        counter.increase(3);
			 *        System.out.println(counter.count);
			 *    }
			 */		
			//int codeAddr_main = Machine.nextInstrAddr();
	/* main: */
		//	Machine.emit(Op.LOADL,-1);		     // -1 on stack (= no class descriptor)
			//Machine.emit(Op.LOADL, 1);			 //  1 on stack (# of fields in class "Counter")
			//Machine.emit(Prim.newobj);	         // create new instance of "Counter" on heap
				                                 // heap addr returned becomes initial value 
												 // of "counter"
			
			//Machine.emit(Op.LOADL,3);            // evaluating first arg yields literal 3  
			//Machine.emit(Op.LOAD,Reg.LB,3);      // Counter instance (== value of local var counter)
			//Machine.emit(Op.CALLI,Reg.CB,codeAddr_increase);   // instance method call 
			                                     // address of the "increase" method saved previously
			
			//Machine.emit(Op.LOADL, 0);           // field 0 on stack ("count" field)
			//Machine.emit(Prim.fieldref);         // returns value of "counter.count" on stack
			//Machine.emit(Prim.putintnl);         // print value
			
			//Machine.emit(Op.RETURN,0,0,1);       // return to caller:  pop one caller arg 
	                                             // and return no value
			
		/*
		 *  Postamble
		 *    patch jumps and calls to unknown code addresses
		 *  
		 */
			//Machine.patch(patchAddr_Call_main, codeAddr_main);
			                                     // supply correct address of "main" to
			                                     // generated call in preamble
			
		
		/*
		 * write code to object code file (.mJAM)
		 */
			//String objectCodeFileName = "Counter.mJAM";
			//ObjectFile objF = new ObjectFile(objectCodeFileName);
			//System.out.print("Writing object code file " + objectCodeFileName + " ... ");
			//if (objF.write()) {
				//System.out.println("FAILED!");
				//return;
			//}
			//else
				//System.out.println("SUCCEEDED");	
			
		
	/****************************************************************************************
	 * The pa4 code generator should write an object file as shown above 
	 * at the end of code generation and exit(0) if there are no errors.
	 * 
	 * During development of the code generator you may want to run the generated code 
	 * directly after code generation using the mJAM debugger mode.   You can adapt the 
	 * following code for this (it assumes objectCodeFileName holds the name of the 
	 * objectFile)
	 * 
	 */
			 // create asm file corresponding to object code using disassembler 
	       // String asmCodeFileName = objectCodeFileName.replace(".mJAM",".asm");
	       // System.out.print("Writing assembly file " + asmCodeFileName + " ... ");
	      //  Disassembler d = new Disassembler(objectCodeFileName);
	       // if (d.disassemble()) {
	           //     System.out.println("FAILED!");
	            //    return;
	      //  }
	      //  else
	               // System.out.println("SUCCEEDED");

	/* 
	 * run code using debugger
	 * 
	 */
	       // System.out.println("Running code in debugger ... ");
	      //  Interpreter.debug(objectCodeFileName, asmCodeFileName);

	       // System.out.println("*** mJAM execution completed");
		//}

	//}
}
