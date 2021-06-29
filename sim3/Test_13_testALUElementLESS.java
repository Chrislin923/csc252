public class Test_13_testALUElementLESS {

    public static void main(String[] args) {
        Sim3_ALUElement testElement;

        // Less is 3, so 011
        RussWire[] aluOp = new RussWire[3];
        for (int i = 0; i < aluOp.length; i++) {
            aluOp[i] = new RussWire();
        }
        aluOp[0].set(true);
        aluOp[1].set(true);
        aluOp[2].set(false);

        boolean lessWorks = true;

        testElement = setElement(true);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        
        if (!testElement.result.get()) {
            System.out.println("Less doesn't work for true");
        }

        testElement = setElement(false);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        
        if (testElement.result.get()) {
            System.out.println("Less doesn't work for false");
        }

        System.out.println("Less test cases finished");

    }

    private static Sim3_ALUElement setElement(boolean less) {
        Sim3_ALUElement element = new Sim3_ALUElement();
        element.a.set(false);
        element.b.set(false);
        element.carryIn.set(false);
        element.bInvert.set(false);
        element.less.set(less);
        return element;
    }

}