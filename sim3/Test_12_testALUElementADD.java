public class Test_12_testALUElementADD {

    // Need to account for a, b, carryIn, then output
    // This ignores bInvert since Test_10 and Test_11 should check that
    public static void main(String[] args) {

        // Testing the ADD of the element, so aluOp is [0,1,0]
        RussWire[] aluOp = new RussWire[3];
        for (int i = 0; i < aluOp.length; i++) {
            aluOp[i] = new RussWire();
        }
        aluOp[0].set(false);
        aluOp[1].set(true);
        aluOp[2].set(false);

        Sim3_ALUElement testElement;
        // Inputs
        int[] a  =  {0, 1, 0, 1, 0, 1, 0, 1};
        int[] b  =  {0, 0, 1, 1, 0, 0, 1, 1};
        int[] ci =  {0, 0, 0, 0, 1, 1, 1, 1};
        // Outputs
        int[] res = {0, 1, 1, 0, 1, 0, 0, 1};
        int[] co =  {0, 0, 0, 1, 0, 1, 1, 1};

        for (int i = 0; i < a.length; i++) {
            testElement = setElement((a[i] == 1), (b[i] == 1), (ci[i] == 1));
            testElement.aluOp = aluOp;
            testElement.execute_pass1();
            testElement.execute_pass2();
            if (testElement.addResult.get() != (res[i] == 1) || testElement.carryOut.get() != (co[i] == 1)) {
                System.out.print("Error in ADD:\n");
                System.out.printf("%ta: %d, b: %n, carryIn: %d%n", a[i], b[i], ci[i]);
                System.out.printf("%tExpected: result: %d, carryOut: %d%n", res[i], co[i]);
                System.out.printf("%t     Got: result: %d, carryOut: %d%n", testElement.result.get() ? 1 : 0, testElement.carryOut.get() ? 1 : 0);
            }
        }
        System.out.print("All test cases ran!");
    }

    private static Sim3_ALUElement setElement(boolean a, boolean b, boolean ci) {
        Sim3_ALUElement element = new Sim3_ALUElement();
        element.a.set(a);
        element.b.set(b);
        element.carryIn.set(ci);
        element.bInvert.set(false);
        element.less.set(false);
        return element;
    }

}