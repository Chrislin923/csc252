/*
Course: csc252
Assignment: Sim5
Author: Chris Lin
Purpose: 
*/

#include "sim5.h"
#include <stdio.h>

void extract_instructionFields(WORD instruction, InstructionFields *fieldsOut){
	//this function set up the instruction fields by shifting or masking
	fieldsOut -> opcode = instruction >> 26 & 0x3f;
	fieldsOut -> rs = instruction >> 21 & 0x1f;
	fieldsOut -> rt = instruction >> 16 & 0x1f;
	fieldsOut -> rd = instruction >> 11 & 0x1f;
	fieldsOut -> shamt = instruction >> 6 & 0x1f;
	fieldsOut -> funct = instruction & 0x3f;
	fieldsOut -> imm16 = instruction & 0xffff;
	fieldsOut -> imm32 = signExtend16to32(fieldsOut -> imm16);
	fieldsOut -> address = instruction & 0x03ffffff;
}

int IDtoIF_get_stall(InstructionFields *fields,
	ID_EX  *old_idex, EX_MEM *old_exmem){
	//this function determines if the insturction has to be stall

	if (old_idex -> memRead){
		//if one  instruction ahead is lw
		if(fields -> opcode == 0x0){
			//R format
			if(old_idex->rt == fields -> rs
				|| old_idex->rt == fields ->rt){ 
				return 1;
			}
		}
		else{
				//I format
			if(old_idex->rt == fields -> rs){
				return 1;
			}
		}
	}
	else if(fields -> opcode == 0x2b){
			//if current instruction is sw, use data forwarding
		if(old_idex->regDst == 1
			&& old_idex -> regWrite == 1
			&& old_idex -> rd == fields->rt){ 
			return 0;
			//one instruction ahead is r format
		}
		else if(old_idex -> regDst == 0
			&& old_idex -> regWrite == 1
			&& old_idex -> rt == fields -> rt){
			//one instruction ahead is i format
			return 0;
		}
		else if(old_exmem -> writeReg == fields -> rt
			&& old_exmem -> regWrite == 1){
			return 1;
		}
		
	}
	return 0;


}




int IDtoIF_get_branchControl(InstructionFields *fields, WORD rsVal, WORD rtVal){
	//this function control branching based on the operation
		if(fields->opcode == 0x4)
			return rsVal == rtVal;
		
		if(fields -> opcode == 0x5)
			return rsVal != rtVal;
		
		if(fields->opcode == 0x2)
			return 2;

		return 0;
	}

WORD calc_branchAddr(WORD pcPlus4, InstructionFields *fields){
	//calculate address for branching
	return pcPlus4 + 4 * fields -> imm32;
}

WORD calc_jumpAddr  (WORD pcPlus4, InstructionFields *fields){
	//calculte address for jumping
	int new = pcPlus4 & 0xf0000000;
	return new + 4 * fields -> address;
}

int execute_ID(int IDstall,
	InstructionFields *fieldsIn,
	WORD pcPlus4,
	WORD rsVal, WORD rtVal,
	ID_EX *new_idex){
	//determine which operation to perform and set up id_ex pipeline
	new_idex->rs = fieldsIn->rs;
	new_idex->rt = fieldsIn->rt;
	new_idex->rd = fieldsIn-> rd;
	new_idex->rsVal = rsVal;
	new_idex->rtVal = rtVal;
	new_idex->imm32 = fieldsIn -> imm32;
	new_idex -> imm16 = fieldsIn -> imm16;

	int op = fieldsIn -> opcode;
	int funct = fieldsIn -> funct;
	int flag = 1; // use to determine if an instruction is called

	if(!IDstall){
		if(op == 0x0){
			// R format
			new_idex -> regWrite = 1;
			new_idex -> ALUsrc = 0;
			new_idex -> memToReg = 0;
			new_idex -> memWrite = 0;
			new_idex -> memRead = 0;
			new_idex -> regDst = 1;
			if(funct == 0x20 ) //add
			{
				new_idex->ALU.op = 0x2;
				new_idex->ALU.bNegate = 0;
				flag = 0;
			}
			if(funct == 0x21) //addu
			{
				new_idex->ALU.op = 0x2;
				new_idex->ALU.bNegate = 0;
				flag = 0;
			}
			if(funct == 0x22) //sub
			{
				new_idex->ALU.op = 0x2;
				new_idex->ALU.bNegate = 1;
				flag = 0;
			}
			if(funct == 0x23) //subu
			{
				new_idex->ALU.op = 0x2;
				new_idex->ALU.bNegate = 1;
				flag = 0;
			}
			if(funct == 0x24) //and
			{
				new_idex->ALU.op = 0x0;
				new_idex->ALU.bNegate = 0;
				flag = 0;
			}
			if(funct == 0x25) //or
			{
				new_idex->ALU.op = 0x1;
				new_idex->ALU.bNegate = 0;
				flag = 0;
			}
			if(funct == 0x26) //xor
			{
				new_idex->ALU.op = 0x4;
				new_idex->ALU.bNegate = 0;
				flag = 0;
			}
			if(funct == 0x2a)// slt
			{
				new_idex->ALU.op = 0x3;
				new_idex->ALU.bNegate = 1;
				flag = 0;
			}

			if(funct == 0x27)//nor 
			{
				new_idex->ALU.op = 0x6;
				new_idex->ALU.bNegate = 0;
				flag = 0;
			}
			// if(funct == 0x0) //sll (extra)
			// {
			// 	new_idex->ALU.op = 0x6;
			// 	new_idex->ALU.bNegate = 0;
			// 	new_idex->extra1 = fields -> shamt;
			// 	flag = 0;
				// }
			if(funct == 0x0){
				new_idex -> regWrite = 0;
				new_idex -> ALUsrc = 0;
				new_idex -> memToReg = 0;
				new_idex -> memWrite = 0;
				new_idex -> memRead = 0;
				new_idex -> regDst = 1;
				new_idex->ALU.op = 0x5;
				new_idex->ALU.bNegate = 0;
				new_idex -> regWrite = 1;
				new_idex -> ALUsrc = 0;
				new_idex->rs = 0;
				new_idex->rt = 0;
				new_idex->rd = 0;
				new_idex->rsVal = 0;
				new_idex->rtVal = 0;
				return 1;
			}
			if(flag){
				// if an instruction is called return 1 esle return 0
				new_idex -> regWrite = 0;
				new_idex -> ALUsrc = 0;
				new_idex -> memToReg = 0;
				new_idex -> memWrite = 0;
				new_idex -> memRead = 0;
				new_idex -> regDst = 0;
				new_idex->ALU.op = 0x0;
				new_idex->ALU.bNegate = 0;
				new_idex -> regWrite = 0;
				new_idex -> ALUsrc = 0;
				new_idex->rs = 0;
				new_idex->rt = 0;
				new_idex->rd = 0;
				new_idex->rsVal = 0;
				new_idex->rtVal = 0;
				return 0;
			}
			else
				return 1;
		}
		//I format
		else
		{

			new_idex -> regWrite = 1;
			new_idex -> ALUsrc = 1;
			new_idex -> memToReg = 0;
			new_idex -> memWrite = 0;
			new_idex -> memRead = 0;
			new_idex -> regDst = 0;

			if(op == 0x8) //addi
			{
				new_idex->ALUsrc = 0x1;
				new_idex->ALU.op = 0x2;
				new_idex->ALU.bNegate = 0;
				flag = 0;
			}
			if(op == 0x9) //addiu
			{
				new_idex->ALUsrc = 0x1;
				new_idex->ALU.op = 0x2;
				new_idex->ALU.bNegate = 0;
				flag = 0;
			}
			if(op == 0xa) //slti
			{
				new_idex->ALUsrc = 0x1;
				new_idex->ALU.op = 0x3;
				new_idex->ALU.bNegate = 1;
				flag = 0;
			}
			if(op == 0x23)//lw
			{
				new_idex -> memRead = 1;
				new_idex -> memToReg = 1;
				new_idex->ALU.op = 0x2;
				new_idex->ALU.bNegate = 0;
				flag = 0;

			}
			if(op == 0x2b)//sw
			{
				new_idex -> regWrite = 0;
				new_idex -> memWrite = 1;
				new_idex->ALU.op = 0x2;
				new_idex->ALU.bNegate = 0;
				flag = 0;
			}
			if(op == 0x4) //beq
			{
				new_idex->ALU.op = 0x0;
				new_idex->ALU.bNegate = 0;
				new_idex -> regWrite = 0;
				new_idex -> ALUsrc = 0;
				flag = 0;
				new_idex->rs = 0;
				new_idex->rt = 0;
				new_idex->rd = 0;
				new_idex->rsVal = 0;
				new_idex->rtVal = 0;


			}
			if(op == 0x2)//j
			{
				new_idex -> regWrite = 0;
				new_idex -> ALUsrc = 0;
				new_idex -> memToReg = 0;
				new_idex -> ALUsrc = 0;
				flag = 0;
				new_idex->ALU.op = 0x0;
				new_idex->ALU.bNegate = 0;
				new_idex -> regWrite = 0;
				new_idex -> ALUsrc = 0;
				flag = 0;
				new_idex->rs = 0;
				new_idex->rt = 0;
				new_idex->rd = 0;
				new_idex->rsVal = 0;
				new_idex->rtVal = 0;
			}
			if(op == 0x5) //bne
			{
				new_idex->ALU.op = 0x0;
				new_idex->ALU.bNegate = 0;
				new_idex -> regWrite = 0;
				new_idex -> ALUsrc = 0;
				flag = 0;
				new_idex->rs = 0;
				new_idex->rt = 0;
				new_idex->rd = 0;
				new_idex->rsVal = 0;
				new_idex->rtVal = 0;
			}
			if(op == 0x0c) //andi
			{
				new_idex -> ALUsrc = 0x2;
				new_idex->ALU.op = 0x0;
				new_idex->ALU.bNegate = 0;
				flag = 0;
			}
			if(op == 0x0d) //ori
			{
				new_idex -> ALUsrc = 0x2;
				new_idex->ALU.op = 0x1;
				new_idex->ALU.bNegate = 0;
				flag = 0;
			}
			if(op == 0x0f) // lui
			{
				new_idex -> ALUsrc = 0x2;
				new_idex -> ALU.op = 0x7;
				new_idex -> ALU.bNegate = 0;
				flag = 0;
			}
			if(flag){
				// if an instruction is called return 1 esle return 0
				new_idex -> regWrite = 0;
				new_idex -> ALUsrc = 0;
				new_idex -> memToReg = 0;
				new_idex -> memWrite = 0;
				new_idex -> memRead = 0;
				new_idex -> regDst = 0;
				new_idex->ALU.op = 0x0;
				new_idex->ALU.bNegate = 0;
				new_idex -> regWrite = 0;
				new_idex -> ALUsrc = 0;
				new_idex->rs = 0;
				new_idex->rt = 0;
				new_idex->rd = 0;
				new_idex->rsVal = 0;
				new_idex->rtVal = 0;
				return 0;
			}
			else
				return 1;

		}
	}
	else{
		new_idex -> ALU.bNegate = 0;
		new_idex -> ALU.op = 0;
		new_idex -> regWrite = 0;
		new_idex -> ALUsrc = 0;
		new_idex -> memToReg = 0;
		new_idex -> memWrite = 0;
		new_idex -> memRead = 0;
		new_idex -> regDst = 0;
		new_idex->ALU.op = 0x0;
		new_idex->ALU.bNegate = 0;
		new_idex -> regWrite = 0;
		new_idex -> ALUsrc = 0;
		new_idex->rs = 0;
		new_idex->rt = 0;
		new_idex->rd = 0;
		new_idex->rsVal = 0;
		new_idex->rtVal = 0;
		return 1;
	}


}

WORD EX_getALUinput1(ID_EX *in, EX_MEM *old_exMem, MEM_WB *old_memWb){
	//set up the first input
	if(old_exMem->regWrite && 
		old_exMem->writeReg == in ->rs)
		return old_exMem->aluResult;

	if(old_memWb->regWrite &&
		old_memWb->writeReg == in -> rs){
		if(old_memWb -> memToReg)
			return old_memWb -> memResult;
		else
			return old_memWb -> aluResult;
	}
	return in->rsVal;

}

WORD EX_getALUinput2(ID_EX *in, EX_MEM *old_exMem, MEM_WB *old_memWb){
	// set up the second input
	if(in -> ALUsrc == 1)
		return in->imm32;
	if(in -> ALUsrc == 2)
		return in -> imm16;

	if(old_exMem->regWrite && 
		old_exMem->writeReg == in ->rt){
		return old_exMem->aluResult;
	}


	if(old_memWb->regWrite &&
		old_memWb->writeReg == in -> rt){
		if(old_memWb -> memToReg)
			return old_memWb -> memResult;
		else
			return old_memWb -> aluResult;
	}
	return in -> rtVal;

}

void execute_EX(ID_EX *in, WORD input1, WORD input2,
                EX_MEM *new_exMem){
	//perform the operation based on alu operation code
	int aluOp = in -> ALU.op;
	new_exMem -> memRead = in -> memRead;
	new_exMem -> memWrite = in -> memWrite;
	new_exMem -> memToReg = in -> memToReg;
	new_exMem -> regWrite = in -> regWrite;
	new_exMem -> rt = in-> rt;
	new_exMem -> rtVal = in -> rtVal;

	if(aluOp == 0){
			new_exMem -> aluResult = input1 & input2;
		}
	if(aluOp == 1){
		new_exMem -> aluResult = input1 | input2;
		}
	if(aluOp == 2){
		if(in->ALU.bNegate == 1){
			new_exMem -> aluResult = input1 - input2;
		}
		else
			new_exMem -> aluResult = input1 + input2;

	}
	if(aluOp == 3){
		new_exMem -> aluResult = input1 < input2;
	}
	if(aluOp == 4){
		new_exMem -> aluResult = input1 ^ input2;
		
	}
	if(aluOp == 5){
		new_exMem -> aluResult = 0;
	}
	if(aluOp == 6){
		new_exMem -> aluResult = ~ (input1 | input2);
		
	}
	if(aluOp == 7){
		new_exMem -> aluResult = input2 << 16;
	}

	if(in -> regDst){
		//R format, store the reuslt in rd to writeReg
		new_exMem -> writeReg = in -> rd;

	}
	else{
		new_exMem-> writeReg = in -> rt;
	}

}

void execute_MEM(EX_MEM *in, MEM_WB *old_memWb,
                 WORD *mem, MEM_WB *new_memwb){
	//decide where to get the result
	new_memwb -> memToReg = in -> memToReg;
	new_memwb -> aluResult = in -> aluResult;
	new_memwb -> writeReg = in -> writeReg;
	new_memwb -> regWrite = in -> regWrite;
	if(in -> memToReg ==0){
		//lw
		new_memwb -> memResult = 0;
	}
	else{
		new_memwb -> memResult = mem[in->aluResult >> 2];
	}

	if(in -> memWrite){
		//sw
		if(old_memWb -> writeReg == in -> rt 
			&& old_memWb -> regWrite){
			//if current rt is depending on old instruction value
			if(old_memWb -> memToReg){
				// if previous memWb is lw
				mem[in->aluResult >> 2] = old_memWb -> memResult;
			}
			else
				mem[in->aluResult >> 2] = old_memWb -> aluResult;
		}
		else
			mem[in->aluResult >> 2] = in -> rtVal;
	}

}

void execute_WB (MEM_WB *in, WORD *regs){
	//decide which result to be stored
	if(in -> regWrite == 0)
		return;
	if(in -> memToReg){
		regs[in -> writeReg] = in -> memResult;
	}
	else{
		regs[in -> writeReg]  = in -> aluResult;
	}
}





