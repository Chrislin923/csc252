
# FILE: asm5.s
# ASSIGNMENT: Assembly Project #5
# AUTHOR: Chris Lin
# DESCRIPTION: 
#	Play the game rock, paper, scissors till first person
#	winning three times.
#
# 


.data
	user_in: .space 20
	rock:  .asciiz "rock\n"
	paper: .asciiz "paper\n"
	scissors: .asciiz "scissors\n"
	welcome: .asciiz "Welcome to rock, paper, scissors!"
	enter: .asciiz "What is your choice? "
	com: .asciiz "The computer choose: "
	TIE: .asciiz "Game is tie!"
	WIN: .asciiz "You win!"
	LOSE: .asciiz "You lose!"
	cur_sc: .asciiz "Current Score: Computer - "
	you: .asciiz " you - "
	F_win: .asciiz "Congrats! You win the game"
	F_lose: .asciiz "Sad! You lose the game"
	
	
.text

.globl strcmp
strcmp:
	#Purpose:
	#	This function compares two strings, if they are
	#	equal, return 0.
	#
	# REGISTERS
	#   t0 -  p1 (parameter a)
	#   t1 -  p2 (parameter b)
	#   t2 - *p1
	#   t3 - *p2
	#   t8 - various temporaries
	
	
	# standard prologue
	addiu   $sp, $sp, -24
	sw      $fp, 0($sp)
	sw      $ra, 4($sp)
	addiu   $fp, $sp, 20

	add     $t0, $a0,$zero          # p1 = a
	add     $t1, $a1,$zero          # p2 = b

strcmp_LOOP:
	lb      $t2, 0($t0)             # read *p1
	lb      $t3, 0($t1)             # read *p2
	beq     $t2,$zero, strcmp_DONE  # if (*p1 == '\0') break
	beq     $t3,$zero, strcmp_DONE  # if (*p2 == '\0') break
	bne     $t2,$t3,   strcmp_DONE  # if (*p1 != *p2 ) break

	addi    $t0, $t0,1              # p1++
	addi    $t1, $t1,1              # p2++
	j       strcmp_LOOP

strcmp_DONE:
	sub     $v0, $t2,$t3            # return *p1 - *p2

	# standard epilogue
	lw      $ra, 4($sp)
	lw      $fp, 0($sp)
	addiu   $sp, $sp, 24
	jr      $ra


.globl main
main:
	#Purpose:
	#	Implement for the game of rock paper scissors
	#
	#	Registers:
	#		#s1 - random number
	#		#s2 - user input
	#		#s3 - computer choice
	#		#s4 - rock
	#		#s5 - paper
	#		#s6 - scissors
	#		#s7 - your score
	#	
	#	Temperary Registers:
	#		#t8 - computer score
	#
	#
	
	
	addi $v0, $zero, 4	#print welcome
	la   $a0, welcome
	syscall
	
	addi $v0, $zero, 11	#print '\n'
	addi $a0, $zero, '\n'
	syscall
	
	addi $s7, $zero, 0	#s7 = user score
	addi $t8, $zero, 0	#t8 = computer score
	
	
LOOP:
	
	li   $a1, 3	#set $a1 to the max bound.
	li   $v0, 42 	#generates the random number.
	syscall
    
    
	add  $s1, $a0, $zero	#s1 = random number
	

    
	add  $t0, $zero, $zero	#t0 = 0
    
	bne  $s1, $t0, CH_P	#if(random number != 0) goto CH_P
	la   $s3, rock		#s3 = com_ch (in string)
	

	
CH_P:
	addi $t0, $zero, 1	#t0 = 1
	
	bne  $s1, $t0, CH_S	#if(random number != 1) goto CH_S
	la   $s3, paper		#s3 = com_ch (in string)
	
	
	
CH_S:
	addi $t0, $zero, 2	#t0 = 2
	
	bne  $s1, $t0, CMP	#if(random number != 2) goto cmp
	la   $s3, scissors	#s3 = com_ch (in string)
	
	
	

CMP:
	addi $t0, $zero, 3	#t0 = 3
	slt  $t1, $s7, $t0	#t1 = user score < 3
	beq  $t1, $zero, DONE	#if(user score > 3) goto DONE
	
	slt  $t1, $t8, $t0	#t1 = computer score < 3
	beq  $t1, $zero, DONE	#if(computer score > 3) goto DONE
	
	addi $v0, $zero, 4	#print enter
	la   $a0, enter
	syscall
	
	
	la   $a0, user_in	#sets $a0 to point to the space allocated for writing a word
	li   $a1, 20
	li   $v0, 8		#load op code for getting a string from the user into register $v0
	syscall			#string in user_in
    
    	
	la   $s2, user_in	#s2 = user_in

	
	la   $s4, rock		#s4 = rock
	la   $s5, paper		#s5 = paper
	la   $s6, scissors	#s6 = scissors

	#compare computer input and user input
	add  $a0, $zero, $s3	#first parameter for strcmp
	add  $a1, $zero, $s2	#sec parameter for strcmp
	
	jal strcmp
	
	add  $t3, $v0, $zero	#t3 = strcmp(user_in, com_ch)
	
	
	bne  $t3, $zero, Cmp_r	#if(com_ch != user_in) goto Cmp_r
	
	addi $v0, $zero, 4	#print com
	la   $a0, com
	syscall
	addi $v0, $zero, 4	#print com_ch
	add  $a0, $zero, $s3
	syscall
	addi $v0, $zero, 4	#print TIE
	la   $a0, TIE
	syscall
	addi $v0, $zero, 11	#print '\n'
	addi $a0, $zero, '\n'
	syscall
	addi $v0, $zero, 4	#print current score
	la   $a0, cur_sc
	syscall
	addi $v0, $zero, 1	#computer score
	add $a0, $zero, $t8
	syscall
	addi $v0, $zero, 4	#print you
	la   $a0, you
	syscall
	addi $v0, $zero, 1	#print your score
	add $a0, $zero, $s7
	syscall
	addi $v0, $zero, 11	#print '\n'
	addi $a0, $zero, '\n'
	syscall
	
	j LOOP
	
Cmp_r:


	add  $a0, $zero, $s4	#first parameter for strcmp
	add  $a1, $zero, $s2	#sec parameter for strcmp
	
	jal strcmp		
	
	add  $t3, $v0, $zero	#t3 = strcmp(rock, user_in)
	
	bne  $t3, $zero, Cmp_p  #if(rock != user_in) goto Cmp_p
	
	add  $t3, $zero, 1	#t3 = 1
	
	bne  $s1, $t3, win	#if(computer not paper) go to win
	addi $v0, $zero, 4	#print com
	la   $a0, com
	syscall
	addi $v0, $zero, 4	#print com_ch
	add  $a0, $zero, $s3
	syscall
	addi $v0, $zero, 4	#print LOSE
	la   $a0, LOSE
	syscall
	addi $v0, $zero, 11	#print '\n'
	addi $a0, $zero, '\n'
	syscall
	
	addi $t8, $t8, 1	#computer score ++
	
	addi $v0, $zero, 4	#print current score
	la   $a0, cur_sc
	syscall
	addi $v0, $zero, 1	#computer score
	add $a0, $zero, $t8
	syscall
	addi $v0, $zero, 4	#print you
	la   $a0, you
	syscall
	addi $v0, $zero, 1	#print your score
	add $a0, $zero, $s7
	syscall
	addi $v0, $zero, 11	#print '\n'
	addi $a0, $zero, '\n'
	syscall
	
	
	j LOOP

	

Cmp_p:
	add  $a0, $zero, $s5	#first parameter for strcmp
	add  $a1, $zero, $s2	#sec parameter for strcmp
	
	jal strcmp		
	
	add  $t3, $v0, $zero	#t3 = strcmp(paper, user_in)
	
	bne  $t3, $zero, Cmp_s  #if(paper != user_in) goto Cmp_s
	
	add  $t3, $zero, 2	#t3 = 2
	
	bne  $s1, $t3, win	#if(computer not scissors) go to p_win
	addi $v0, $zero, 4	#print com
	la   $a0, com
	syscall
	addi $v0, $zero, 4	#print com_ch
	add  $a0, $zero, $s3
	syscall
	addi $v0, $zero, 4	#print LOSE
	la   $a0, LOSE
	syscall
	addi $v0, $zero, 11	#print '\n'
	addi $a0, $zero, '\n'
	syscall
	
	
	addi $t8, $t8, 1	#computer score ++
	
	addi $v0, $zero, 4	#print current score
	la   $a0, cur_sc
	syscall
	addi $v0, $zero, 1	#computer score
	add  $a0, $zero, $t8
	syscall
	addi $v0, $zero, 4	#print you
	la   $a0, you
	syscall
	addi $v0, $zero, 1	#print your score
	add  $a0, $zero, $s7
	syscall
	addi $v0, $zero, 11	#print '\n'
	addi $a0, $zero, '\n'
	syscall
	
	j LOOP


Cmp_s:
	add  $a0, $zero, $s6	#first parameter for strcmp
	add  $a1, $zero, $s2	#sec parameter for strcmp
	
	jal strcmp		
	
	add  $t3, $v0, $zero	#t3 = strcmp(scissors, user_in)
	
	bne  $t3, $zero, DONE #if(scissors != user_in) goto DONE
	
	add  $t3, $zero, 0	#t3 = 0
	
	bne  $s1, $t3, win	#if(computer not rock) go to win
	addi $v0, $zero, 4	#print com
	la   $a0, com
	syscall
	addi $v0, $zero, 4	#print com_ch
	add  $a0, $zero, $s3
	syscall
	addi $v0, $zero, 4	#print LOSE
	la   $a0, LOSE
	syscall
	addi $v0, $zero, 11	#print '\n'
	addi $a0, $zero, '\n'
	syscall
	
	addi $t8, $t8, 1	#computer score ++
	
	addi $v0, $zero, 4	#print current score
	la   $a0, cur_sc
	syscall
	addi $v0, $zero, 1	#computer score
	add  $a0, $zero, $t8
	syscall
	addi $v0, $zero, 4	#print you
	la   $a0, you
	syscall
	addi $v0, $zero, 1	#print your score
	add  $a0, $zero, $s7
	syscall
	addi $v0, $zero, 11	#print '\n'
	addi $a0, $zero, '\n'
	syscall
	
	j LOOP

win:
	addi $v0, $zero, 4	#print com
	la   $a0, com
	syscall
	addi $v0, $zero, 4	#print com_ch
	add  $a0, $zero, $s3
	syscall
	addi $v0, $zero, 4	#print WIN
	la   $a0, WIN
	syscall
	addi $v0, $zero, 11	#print '\n'
	addi $a0, $zero, '\n'
	syscall
	
	
	addi $s7, $s7, 1	#computer score ++
	
	addi $v0, $zero, 4	#print current score
	la   $a0, cur_sc
	syscall
	addi $v0, $zero, 1	#computer score
	add  $a0, $zero, $t8
	syscall
	addi $v0, $zero, 4	#print you
	la   $a0, you
	syscall
	addi $v0, $zero, 1	#print your score
	add  $a0, $zero, $s7
	syscall
	addi $v0, $zero, 11	#print '\n'
	addi $a0, $zero, '\n'
	syscall
	
	j LOOP
	
DONE:
	slt $t0, $t8, $s7	#t0 = computer score < user score
	beq $t0, $zero, FINAL	#if(computer score > user score) goto FINAL
	addi $v0, $zero, 4	#print F_win
	la   $a0, F_win
	syscall
	addi $v0, $zero, 11	#print '\n'
	addi $a0, $zero, '\n'
	syscall
	
	j Exit
	
FINAL:
	addi $v0, $zero, 4	#print you
	la   $a0, F_lose
	syscall
	addi $v0, $zero, 11	#print '\n'
	addi $a0, $zero, '\n'
	syscall
	
Exit:

    
    