/* A class of Sim3_ALU
 *
 * Assignment: Sim3
 * 
 * Author: Chris Lin
 * 
 * This class takes a parameter size and create an array of 
 * alu element to repersent each bit operates with alu
 * 
 */
public class Sim3_ALU 
{
	public Sim3_ALU(int size) {
		// initialize input and output
		aluOp = new RussWire[3];
		
		for (int i = 0; i < 3; i++) {
			aluOp[i] = new RussWire();
		}
		
		a = new RussWire[size];
		b = new RussWire[size];
		result = new RussWire[size];
		alu = new Sim3_ALUElement[size];
		
		for (int i = 0; i < size; i++) {
			a[i] = new RussWire();
			b[i] = new RussWire();
			result[i] = new RussWire();
			alu[i] = new Sim3_ALUElement();
		}
		
		bNegate = new RussWire();
		this.size = size;
	}
	
	//input of ALU class
	public RussWire[] aluOp;
	public RussWire bNegate;
	public RussWire[] a, b;
	
	//output of ALU class
	public RussWire[] result;
	
	//gates to go through
	private Sim3_ALUElement[] alu;
	
	private int size;

	
	public void execute() {
		//set the control bits to the first alu element
		alu[0].aluOp[0].set(aluOp[0].get());
		alu[0].aluOp[1].set(aluOp[1].get());
		alu[0].aluOp[2].set(aluOp[2].get());
		
		//set input a, b, bInvert and carryIn to the first alu element
		alu[0].a.set(a[0].get());
		alu[0].b.set(b[0].get());
		alu[0].bInvert.set(bNegate.get());
		alu[0].carryIn.set(bNegate.get());
		
		//exeute first alu element for first pass
		alu[0].execute_pass1();
		
		
		for(int i=1; i < size; i++) {
			//set the control bits to the rest alu elements with given size
			alu[i].aluOp[0].set(aluOp[0].get());
			alu[i].aluOp[1].set(aluOp[1].get());
			alu[i].aluOp[2].set(aluOp[2].get());
			
			//set input a, b, bInvert and carryIn for the rest of alu elements
			alu[i].a.set(a[i].get());
			alu[i].b.set(b[i].get());
			alu[i].bInvert.set(bNegate.get());
			alu[i].carryIn.set(alu[i - 1].carryOut.get());
			
			//execute each of the alu element for the first pass
			alu[i].execute_pass1();
			
			//all less in alu element is false except for the first alu element
			alu[i].less.set(false);
			
		}
		
		//set the less in first alu element to the add result of the LSB.
		alu[0].less.set(alu[size - 1].addResult.get());
		
		for(int i = 0; i < size; i++) {
			//execute pass2 for all of the alu elements and set the result
			alu[i].execute_pass2();
			result[i].set(alu[i].result.get());
		}
	}
	
}
