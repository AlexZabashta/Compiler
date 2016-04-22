;text char 1 read
sys.read:
	push dword 128
	call _malloc
	add esp, 4
	mov [esp + 4], eax
	mov edx, eax
	xor ecx, ecx
	readline_loop:
		push ecx		
		push edx
		call _getchar
		pop edx		
		pop ecx
		cmp eax, 32
		je readline_end				
		mov [edx + ecx + 2], eax
		inc ecx
		jmp readline_loop
		
	readline_end:
	mov [edx + 0], dword 1
	mov [edx + 1], ecx
ret


;text int 0 add int 0 int 0
sys.add$int$int:
	mov eax, [esp + 8]
	mov ebx, [esp + 4]
	add eax, ebx
	mov [esp + 12], eax	
ret

;text bool 0 less int 0 int 0
sys.less$int$int:
	mov eax, [esp + 8]
	mov ebx, [esp + 4]
	cmp eax, ebx
	jl cmp cmp_ints_l	
		mov [esp + 12], dword 0
		jmp	cmp_ints_end
	cmp_ints_l:	
		mov [esp + 12], dword 1
	cmp_ints_end:	
ret

;text void 0 print char 1
sys.print$char.1
	mov edx, [esp + 4]
	inc edx
	mov ecx, [edx]
	inc edx       ; [edx, ebx)
	mov ebx, edx
	add ebx, ecx
	
	print_loop:
		cmp edx, ebx
		je print_end			
		push ebx		
		push edx
		push edx		
		call _putchar
		add esp, 4
		pop edx
		pop ebx		
		inc edx
		jmp print_loop
	
	print_end:	
ret   

;text char 1 add char 1 char 1
sys.add$char.1$char.1:
	mov edx, [esp + 8]
	mov ebx, [esp + 4]
	mov ecx, 2
	add ecx, [edx + 2]
	add ecx, [ebx + 2]
	
	push edx
	push ebx
	push ecx
	push ecx
	call _malloc	
	add esp, 4
	pop ecx
	pop ebx
	pop edx	
	
	mov [eax + 0], dword 1
	mov [eax + 1], ecx
	mov [esp + 12], eax

	push edx
	push ebx
	push ecx
	
		mov ecx, [edx + 1]
		add eax, 2
		mov ebx, edx
		add ebx, 2
		
		call memcopy
	
	pop ecx
	pop ebx
	pop edx	
	
	push ebx
	push ecx	
		mov ecx, [ebx + 1]		
		add ebx, 2	
		call memcopy		
	pop ecx
	pop ebx	
ret

memcopy:
	cmp ecx, 0
	je memcopy_end
	mov edx, [ebx]
	mov [eax], edx
	inc eax
	dec ecx
	jmp memcopy
	memcopy_end:
ret

;text int 1 copy int 1
sys.copy$int.1:
	mov ebx, [esp + 4]
	mov ecx, [ebx+1]
	
	push ebx	
	push ecx	
	
	push ecx		
	call _malloc
	add esp, 4
	
	pop ecx
	pop ebx
	
	mov [eax + 0], dword 1
	mov [eax + 1], ecx
			
	add eax, 2
	add ebx, 2
	
	call memcopy	
ret

;data emptyarray
	emptyarray: db -1,-1,-1,-1,0,0,0,0


