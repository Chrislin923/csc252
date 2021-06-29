
public class Test_01_testMUX {

    public static void main(String[] args) {
        // Index 0: LSB
        Sim3_MUX_8by1 testMUX;
        boolean allCorrect = true;
        String[] numToBin = {"000", "100", "010", "110", "001", "101", "011", "111"};
        
        // Move the current true bit in the input around
        for (int i = 0; i < 8; i++) {
            // Test that in for every control
            for (int control = 0; control < 8; control++) {
                testMUX = new Sim3_MUX_8by1();
                String in = "";
                for (int in_idx = 0; in_idx < testMUX.in.length; in_idx++) {
                    // Only set the current index (i) to 1, all others are 0 so we can verify we get the right bit out
                    testMUX.in[in_idx].set(in_idx == i ? true : false);
                    in += (in_idx == i ? "1" : "0");
                }
                // Set the control
                String currBin = numToBin[control];
                for (int cont_idx = 0; cont_idx < testMUX.control.length; cont_idx++) {
                    testMUX.control[cont_idx].set(currBin.charAt(cont_idx) == '1');
                }

                testMUX.execute();

               
                if (control != i && testMUX.out.get()) {
                    System.out.print("Wrong output! (LSB-MSB)");
                    System.out.printf("\tIn: " + in);
                    System.out.printf("\tControl: " + currBin);
                    System.out.printf("\tOutput: %s ", (testMUX.out.get() ? "1" : "0"));
                    allCorrect = false;
                }
            }
        }

        if (allCorrect) 
            System.out.println("MUX correct for all possible input!");
    }
}