public class Test_11_testALUElementOR {

    public static void main(String[] args) {
        Sim3_ALUElement testElement;

        // Testing the OR of the element, so aluOp is [1,0,0]
        RussWire[] aluOp = new RussWire[3];
        for (int i = 0; i < aluOp.length; i++) {
            aluOp[i] = new RussWire();
        }
        aluOp[0].set(true);
        aluOp[1].set(false);
        aluOp[2].set(false);

        // 0 |  0
        testElement = setupElement(false, false, false);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        System.out.println("0 OR  0 = " + (testElement.result.get() ? "1" : "0"));

        // 0 | !0
        testElement = setupElement(false, false, true);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        System.out.println("0 OR !0 = " + (testElement.result.get() ? "1" : "0"));

        // 0 |  1
        testElement = setupElement(false, true, false);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        System.out.println("0 OR  1 = " + (testElement.result.get() ? "1" : "0"));

        // 0 | !1
        testElement = setupElement(false, true, true);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        System.out.println("0 OR !1 = " + (testElement.result.get() ? "1" : "0"));

        // 1 |  0
        testElement = setupElement(true, false, false);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        System.out.println("1 OR  0 = " + (testElement.result.get() ? "1" : "0"));

        // 1 | !0
        testElement = setupElement(true, false, true);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        System.out.println("1 OR !0 = " + (testElement.result.get() ? "1" : "0"));

        // 1 |  1
        testElement = setupElement(true, true, false);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        System.out.println("1 OR  1 = " + (testElement.result.get() ? "1" : "0"));

        // 1 | !1
        testElement = setupElement(true, true, true);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        System.out.println("1 OR !1 = " + (testElement.result.get() ? "1" : "0"));

    }

    private static Sim3_ALUElement setupElement(boolean a, boolean b, boolean bInvert) {
        Sim3_ALUElement element = new Sim3_ALUElement();
        element.a.set(a);
        element.b.set(b);
        element.bInvert.set(bInvert);
        element.carryIn.set(false);
        element.less.set(false);
        return element;
    }
}