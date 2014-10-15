import java.io.FileNotFoundException;

public class VerySimpleCPU {
	// enumeration of instructions
	final static int ADD = 0;
	final static int ADDi = 1;
	final static int NAND = 2;
	final static int NANDi = 3;
	final static int SRL = 4;
	final static int SRLi = 5;
	final static int LT = 6;
	final static int LTi = 7;
	final static int CP = 8;
	final static int CPi = 9;
	final static int CPI = 10;
	final static int CPIi = 11;
	final static int BZJ = 12;
	final static int BZJi = 13;
	final static int MUL = 14;
	final static int MULi = 15;

	public static void main(String[] args) throws FileNotFoundException {
		int A, B, PC = 0, opcode;
		long instr, starA, starB;

		// Use Hardware class
		Hardware vsCPU = new Hardware(0, 0);
		// Set Memory Size for VerySimpleCPU
		vsCPU.setMemorySize(65536);
		// Load program to memory array
		vsCPU.loadProgram();

		vsCPU.resetWriteEnable();
		vsCPU.setAddress((short) 0);
		vsCPU.setDataIn(0);

		// Implement the Instructions of VerySimpleCPU
		while (true) {
			vsCPU.posedge(); // 1st cycle

			vsCPU.setAddress((short) PC);

			vsCPU.posedge(); // 2nd cycle

			instr = vsCPU.getDataOut();
			opcode = (int) (instr >> 28);
			A = (int) ((instr >> 14) & 0x00003FFF);
			B = (int) (instr & 0x00003FFF);
			// A = ... // ToDo 1: You do this

			switch (opcode) {
			case CPIi:
				vsCPU.setAddress((short) B);
				vsCPU.posedge();
				starB = vsCPU.getDataOut();
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();
				vsCPU.setAddress((short) starA);
				vsCPU.setDataIn(starB);
				vsCPU.setWriteEnable();
				vsCPU.posedge();
				vsCPU.resetWriteEnable();
				PC = PC + 1;
				break;

			case LT:
				vsCPU.setAddress((short) B);
				vsCPU.posedge();
				starB = vsCPU.getDataOut();

				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				if (starA < starB) {
					vsCPU.setDataIn(1);
				} else {
					vsCPU.setDataIn(0);
				}
				vsCPU.setWriteEnable();
				vsCPU.posedge();
				vsCPU.resetWriteEnable();
				PC = PC + 1;
				break;

			case LTi:
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				if (starA < B) {
					vsCPU.setDataIn(1);
				} else {
					vsCPU.setDataIn(0);
				}
				vsCPU.setWriteEnable();
				vsCPU.posedge();
				vsCPU.resetWriteEnable();
				PC = PC + 1;
				break;
			case CP:
				vsCPU.setAddress((short) B);
				vsCPU.posedge(); // 3rd cycle
				starB = vsCPU.getDataOut();
				vsCPU.setAddress((short) A);
				vsCPU.setDataIn(starB); // ToDo 2: Fix this
				vsCPU.setWriteEnable();
				vsCPU.posedge(); // 4th cycle
				vsCPU.resetWriteEnable(); // IMPORTANT: You need to reset
				// WriteEnable
				PC = PC + 1;
				break;

			case CPi:
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				vsCPU.setDataIn(B);
				vsCPU.setWriteEnable();
				vsCPU.posedge(); // 4th cycle
				vsCPU.resetWriteEnable(); // IMPORTANT: You need to reset
				// WriteEnable
				PC = PC + 1;
			case BZJ:
				vsCPU.setAddress((short) B);
				vsCPU.posedge();

				starB = vsCPU.getDataOut();

				vsCPU.setAddress((short) A);
				vsCPU.posedge();

				starA = vsCPU.getDataOut();

				if (starB == 0) {
					PC = (int) starA;
				} else {
					PC = PC + 1;
				}

			case BZJi:
				vsCPU.setAddress((short) A);
				vsCPU.posedge(); // 3rd cycle
				starA = vsCPU.getDataOut();
				PC = (int) (starA + B);
				break;
			case ADD:
				vsCPU.setAddress((short) B);
				vsCPU.posedge();
				starB = vsCPU.getDataOut();
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();
				vsCPU.setDataIn(starA + starB);
				vsCPU.setWriteEnable();
				vsCPU.posedge(); // 4th cycle

				vsCPU.resetWriteEnable(); // IMPORTANT: You need to reset
				// WriteEnable
				PC = PC + 1;

				break;

			case ADDi:
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();
				vsCPU.setDataIn(starA + B);
				vsCPU.setWriteEnable();
				vsCPU.posedge();
				vsCPU.resetWriteEnable();
				PC = PC + 1;

			case NAND:
				vsCPU.setAddress((short) B);
				vsCPU.posedge();
				starB = vsCPU.getDataOut();

				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				long andOpeartion = starA & starB;
				andOpeartion = ~andOpeartion;

				vsCPU.setDataIn(andOpeartion);
				vsCPU.setWriteEnable();
				vsCPU.posedge();
				vsCPU.resetWriteEnable();
				PC = PC + 1;

			case NANDi:
				vsCPU.setAddress((short) A);
				vsCPU.posedge();

				starA = vsCPU.getDataOut();

				long andOperation = starA & B;
				andOperation = ~andOperation;

				vsCPU.setDataIn(andOperation);
				vsCPU.setWriteEnable();
				vsCPU.posedge();
				vsCPU.resetWriteEnable();
				PC = PC + 1;

			case MUL:
				vsCPU.setAddress((short) B);
				vsCPU.posedge();
				starB = vsCPU.getDataOut();

				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				vsCPU.setDataIn(starA * starB);
				vsCPU.setWriteEnable();
				vsCPU.posedge();
				vsCPU.resetWriteEnable();
				PC = PC + 1;
			case MULi:
				vsCPU.setAddress((short) A);
				vsCPU.posedge();

				starA = vsCPU.getDataOut();

				starA = starA * B;
				vsCPU.setDataIn(starA);
				vsCPU.setWriteEnable();
				vsCPU.posedge();
				vsCPU.resetWriteEnable();
				PC = PC + 1;

			case CPI:
				vsCPU.setAddress((short) B);
				vsCPU.posedge();
				starB = vsCPU.getDataOut();

				vsCPU.setAddress((short) starB);
				vsCPU.posedge();
				long starStarB = vsCPU.getDataOut();

				vsCPU.setAddress((short) A);
				vsCPU.setDataIn(starStarB);
				vsCPU.setWriteEnable();
				vsCPU.posedge();
				vsCPU.resetWriteEnable();
				PC = PC + 1;

			case SRL:
				vsCPU.setAddress((short) B);
				vsCPU.posedge();
				starB = vsCPU.getDataOut();

				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				if (starB < 32) {
					starA = starA >> starB;
				} else {
					starA = starA << (starB - 32);
				}

				vsCPU.setDataIn(starA);

				vsCPU.setWriteEnable();
				vsCPU.posedge();
				vsCPU.resetWriteEnable();
				PC = PC + 1;

			case SRLi:
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				if (B < 32) {
					starA = starA >> B;
				} else {
					starA = starA << (B - 32);
				}

				vsCPU.setDataIn(starA);
				vsCPU.setWriteEnable();
				vsCPU.posedge();
				vsCPU.resetWriteEnable();
				PC = PC + 1;

			}
			// For checking the results
			vsCPU.dumpMemory();
		}
	}
}
