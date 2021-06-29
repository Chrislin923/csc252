/* A class of Sim3_MUX_8by1
 *
 * Assignment: Sim3
 * 
 * Author: Chris Lin
 * 
 * This class determine which operation should be done by mux
 * 
 */
public class Sim3_MUX_8by1 
{
	public Sim3_MUX_8by1() {
		// initialize input and output
		in = new RussWire[8];
		control = new RussWire[3];
		
		for (int i = 0; i < 8; i++) {
			in[i] = new RussWire();
		}
		for (int i = 0; i < 3; i++) {
			control[i] = new RussWire();
		}
		
		out = new RussWire();
	}
	
	//input of mux
	public RussWire[] in, control;
	
	//output of mux
	public RussWire out;
	
	
	
	public void execute() {
		//set the control bits to c1, c2, and c3
		boolean c1 = control[0].get();
		boolean c2 = control[1].get();
		boolean c3 = control[2].get();
		
		//get the output based on the sum of product of control bits and input
		
		out.set((!c3 & !c2 & !c1 & in[0].get())|
				(!c3 & !c2 & c1 & in[1].get()) |
				(!c3 & c2 & !c1 & in[2].get()) |
				(!c3 & c2 & c1 & in[3].get())  |
				(c3 & !c2 & !c1 & in[4].get()) |
				(c3 & !c2 & c1 & in[5].get())|
				(c3 & c2 & !c1 & in[6].get())|
				(c3 & c2 & c1 & in[7].get() ));
		
		
	}
	
}
