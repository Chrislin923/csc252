public class Test_10_testALUElementAND {

    public static void main(String[] args) {
        Sim3_ALUElement testElement;

        // Testing the AND of the element, so aluOp is [0,0,0]
        RussWire[] aluOp = new RussWire[3];
        for (int i = 0; i < aluOp.length; i++) {
            aluOp[i] = new RussWire();
            aluOp[i].set(false);
        }

        // 0 &  0
        testElement = setupElement(false, false, false);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        System.out.println("0 AND  0 = " + (testElement.result.get() ? "1" : "0"));

        // 0 & !0
        testElement = setupElement(false, false, true);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        System.out.println("0 AND !0 = " + (testElement.result.get() ? "1" : "0"));

        // 0 &  1
        testElement = setupElement(false, true, false);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        System.out.println("0 AND  1 = " + (testElement.result.get() ? "1" : "0"));

        // 0 & !1
        testElement = setupElement(false, true, true);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        System.out.println("0 AND !1 = " + (testElement.result.get() ? "1" : "0"));

        // 1 &  0
        testElement = setupElement(true, false, false);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        System.out.println("1 AND  0 = " + (testElement.result.get() ? "1" : "0"));

        // 1 & !0
        testElement = setupElement(true, false, true);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        System.out.println("1 AND !0 = " + (testElement.result.get() ? "1" : "0"));

        // 1 &  1
        testElement = setupElement(true, true, false);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        System.out.println("1 AND  1 = " + (testElement.result.get() ? "1" : "0"));

        // 1 & !1
        testElement = setupElement(true, true, true);
        testElement.aluOp = aluOp;
        testElement.execute_pass1();
        testElement.execute_pass2();
        System.out.println("1 AND !1 = " + (testElement.result.get() ? "1" : "0"));

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