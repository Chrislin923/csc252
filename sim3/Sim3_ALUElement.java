/* A class of Sim3_MUX_8by1
 *
 * Assignment: Sim3
 * 
 * Author: Chris Lin
 * 
 * This class perform single alu element, including and, or, add, less
 * and xor
 * 
 * 
 */
public class Sim3_ALUElement 
{
	public Sim3_ALUElement() {
		// initialize input and output
		bInvert = new RussWire();
		a = new RussWire();
		b = new RussWire();
		carryIn = new RussWire();
		less = new RussWire();
		
		aluOp = new RussWire[3];
		
		for (int i = 0; i < 3; i++) {
			aluOp[i] = new RussWire();
		}
		
		result = new RussWire();
		addResult = new RussWire();
		carryOut = new RussWire();
		
		mux = new Sim3_MUX_8by1();
		fAdder = new FullAdder();
		hAdder = new HalfAdder();
		xor = new XOR();
	}
	//input of alu element class
	public RussWire bInvert, a, b, carryIn, less;
	public RussWire[] aluOp;
	
	//output of alu element class
	public RussWire result, addResult, carryOut;
	
	//internal componenets
	private Sim3_MUX_8by1 mux;
	private FullAdder fAdder;
	private HalfAdder hAdder;
	private XOR	xor;
	private boolean bSet;
	
	
	
	public void execute_pass1() {
		//bSet is the result of whehter b should be negate or not determine by the 
		//bInvert
		bSet =  (!bInvert.get() & b.get()) | (bInvert.get() & !b.get());
		boolean and = a.get() & bSet;
		boolean or = a.get() | bSet;
		
		//add a and b 
		fAdder.a.set(a.get());
		fAdder.b.set(bSet);
		fAdder.carryIn.set(carryIn.get());
		fAdder.execute();
		
		//get the result of a xor b
		xor.a.set(a.get());
		xor.b.set(bSet);
		xor.execute();
		
		//set all inputs with the result except for less
		mux.in[0].set(and);
		mux.in[1].set(or);
		mux.in[2].set(fAdder.sum.get());
		
		addResult.set(fAdder.sum.get());
		carryOut.set(fAdder.carryOut.get());
		
		mux.in[4].set(xor.out.get());
		
		//since index 5,6,7 are not being use, set them to false
		mux.in[5].set(false);
		mux.in[6].set(false);
		mux.in[7].set(false);
		
		//pass in the control bits
		mux.control[0].set(aluOp[0].get());
		mux.control[1].set(aluOp[1].get());
		mux.control[2].set(aluOp[2].get());
		
		
		
	}
	
	public void execute_pass2() {
		
		//settig less for the alu element
		
		mux.in[3].set(less.get());
		
		//exexute mux
		mux.execute();
		result.set(mux.out.get());
	}
}
