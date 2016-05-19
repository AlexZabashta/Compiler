import java.util.ArrayList;
import java.util.List;

import code.var.ConstVariable;
import code.var.Variable;
import exception.TypeInitException;
import misc.EnumType;
import misc.Label;
import misc.Type;
import asm.Command;
import asm.Register;
import asm.com.Add;
import asm.com.Call;
import asm.com.CallFree;
import asm.com.CallGetChar;
import asm.com.CallMalloc;
import asm.com.CallPutChar;
import asm.com.Cmp;
import asm.com.IDiv;
import asm.com.IMul;
import asm.com.Je;
import asm.com.Jl;
import asm.com.Jmp;
import asm.com.Jne;
import asm.com.Mov;
import asm.com.Nop;
import asm.com.Pop;
import asm.com.ShiftEsp;
import asm.com.Push;
import asm.mem.ConstInt;
import asm.mem.CpuRegister;
import asm.mem.RWMemory;
import asm.mem.RamEsp;
import asm.mem.RamRegister;
import ast.Function;
import ast.SystemFunction;
import ast.node.Values;
import static misc.EnumType.*;
import static asm.Register.*;

public class StandardFunctions {

    public static List<Function> getFunctions() {
        List<Function> f = new ArrayList<Function>();

        Type int0 = new Type(INT);
        Type char0 = new Type(CHAR);
        Type bool0 = new Type(BOOL);
        Type void0 = new Type(VOID);

        CpuRegister eax = new CpuRegister(EAX);
        CpuRegister ebx = new CpuRegister(EBX);
        CpuRegister ecx = new CpuRegister(ECX);
        CpuRegister edx = new CpuRegister(EDX);

        {
            List<Command> asmCode = new ArrayList<Command>();
            asmCode.add(new Push(ecx, null, null));
            asmCode.add(new Push(edx, null, null));
            {
                asmCode.add(new CallGetChar(null, null));
            }
            asmCode.add(new Pop(edx, null, null));
            asmCode.add(new Pop(ecx, null, null));

            f.add(new SystemFunction(asmCode, char0, "getchar"));
        }

        {
            List<Command> asmCode = new ArrayList<Command>();
            asmCode.add(new Push(ecx, null, null));
            asmCode.add(new Push(edx, null, null));
            { // [0] = edx, [1] = ecx, [2] = eip, [3] = char
                asmCode.add(new Push(new RamEsp(3), null, null));
                asmCode.add(new CallPutChar(null, null));
                asmCode.add(new ShiftEsp(1, null, null));
            }
            asmCode.add(new Pop(edx, null, null));
            asmCode.add(new Pop(ecx, null, null));

            f.add(new SystemFunction(asmCode, void0, "putchar", char0));
        }

        {
            List<Command> asmCode = new ArrayList<Command>(); // eip, arg1, arg0
            asmCode.add(new Push(ebx, null, null)); // ebx, eip, arg1, arg0
            {
                asmCode.add(new Mov(eax, new ConstInt(-1), null, null));
                asmCode.add(new Mov(ebx, new RamEsp(3), null, null));
                asmCode.add(new Cmp(ebx, new RamEsp(2), null, null));

                String end = Label.getTextLabel();
                asmCode.add(new Jl(end, null, null));

                asmCode.add(new Mov(eax, new ConstInt(0), null, null));
                asmCode.add(new Nop(end, null));

            }
            asmCode.add(new Pop(ebx, null, null));

            f.add(new SystemFunction(asmCode, bool0, "less", int0, int0));
        }
        {
            List<Command> asmCode = new ArrayList<Command>(); // eip, arg1, arg0
            asmCode.add(new Push(edx, null, null));
            { // [0] = edx, [1] = eip, [2] = arg2, [3] = arg1
                asmCode.add(new Mov(eax, new RamEsp(3), null, null));
                asmCode.add(new IMul(new RamEsp(2), null, null));
            }
            asmCode.add(new Pop(edx, null, null));

            f.add(new SystemFunction(asmCode, int0, "mul", int0, int0));
        }
        {
            List<Command> asmCode = new ArrayList<Command>(); // eip, arg1, arg0
            asmCode.add(new Push(edx, null, null));
            { // [0] = edx, [1] = eip, [2] = arg2, [3] = arg1
                asmCode.add(new Mov(edx, new ConstInt(0), null, null));
                asmCode.add(new Mov(eax, new RamEsp(3), null, null));
                asmCode.add(new IDiv(new RamEsp(2), null, null));
            }
            asmCode.add(new Pop(edx, null, null));

            f.add(new SystemFunction(asmCode, int0, "div", int0, int0));
        }
        {
            List<Command> asmCode = new ArrayList<Command>(); // eip, arg1, arg0
            asmCode.add(new Push(edx, null, null));
            { // [0] = edx, [1] = eip, [2] = arg2, [3] = arg1
                asmCode.add(new Mov(edx, new ConstInt(0), null, null));
                asmCode.add(new Mov(eax, new RamEsp(3), null, null));
                asmCode.add(new IDiv(new RamEsp(2), null, null));
                asmCode.add(new Mov(eax, edx, null, null));
            }
            asmCode.add(new Pop(edx, null, null));

            f.add(new SystemFunction(asmCode, int0, "mod", int0, int0));
        }
        {
            List<Command> asmCode = new ArrayList<Command>();
            asmCode.add(new Push(ebx, null, null));
            {
                asmCode.add(new Mov(eax, new ConstInt(-1), null, null));
                asmCode.add(new Mov(ebx, new RamEsp(2), null, null));
                asmCode.add(new Cmp(ebx, new RamEsp(3), null, null));

                String end = Label.getTextLabel();
                asmCode.add(new Je(end, null, null));

                asmCode.add(new Mov(eax, new ConstInt(0), null, null));
                asmCode.add(new Nop(end, null));

            }
            asmCode.add(new Pop(ebx, null, null));

            f.add(new SystemFunction(asmCode, bool0, "equal", int0, int0));
        }
        {
            List<Command> asmCode = new ArrayList<Command>();
            asmCode.add(new Push(ebx, null, null));
            {
                asmCode.add(new Mov(eax, new ConstInt(-1), null, null));
                asmCode.add(new Mov(ebx, new RamEsp(2), null, null));
                asmCode.add(new Cmp(ebx, new RamEsp(3), null, null));

                String end = Label.getTextLabel();
                asmCode.add(new Je(end, null, null));

                asmCode.add(new Mov(eax, new ConstInt(0), null, null));
                asmCode.add(new Nop(end, null));

            }
            asmCode.add(new Pop(ebx, null, null));

            f.add(new SystemFunction(asmCode, bool0, "equal", char0, char0));
        }

        {
            List<Command> asmCode = new ArrayList<Command>();
            asmCode.add(new Mov(eax, new RamEsp(1), null, null));
            f.add(new SystemFunction(asmCode, int0, "charcode", char0));
        }

        {
            List<Command> asmCode = new ArrayList<Command>();
            asmCode.add(new Mov(eax, new RamEsp(1), null, null));
            f.add(new SystemFunction(asmCode, char0, "tochar", int0));
        }

        {
            List<Command> asmCode = new ArrayList<Command>();

            {// [0] = eip, [1] = array
                String end = Label.getTextLabel();
                asmCode.add(new Mov(eax, new RamEsp(1), null, null));
                RWMemory pointer = new RamRegister(Register.EAX);

                asmCode.add(new Cmp(pointer, new ConstInt(-1), null, null));
                asmCode.add(new Je(end, null, null));
                asmCode.add(new Add(pointer, new ConstInt(1), null, null));

                asmCode.add(new Nop(end, null));
            }

            f.add(new SystemFunction(asmCode, void0, "subscribe"));
        }

        EnumType[] types = { BOOL, CHAR, INT };
        for (EnumType enumType : types) {
            for (int dim = 1; dim <= Type.MAX_DIM; dim++) {
                try {
                    Type arrayType = new Type(enumType, dim);
                    Type valType = new Type(enumType, dim - 1);
                    {
                        List<Command> asmCode = new ArrayList<Command>();
                        asmCode.add(new Push(ecx, null, null));
                        asmCode.add(new Push(edx, null, null));

                        { // [0] = edx, [1] = ecx, [2] = eip, [3] = array
                            String end = Label.getTextLabel();
                            String loopStart = Label.getTextLabel();
                            String loopEnd = Label.getTextLabel();
                            asmCode.add(new Mov(ecx, new RamEsp(3), null, null));
                            RWMemory pointer = new RamRegister(Register.ECX);
                            asmCode.add(new Cmp(pointer, new ConstInt(-1), null, null));
                            asmCode.add(new Je(end, null, null));
                            if (valType.dim > 0) {
                                asmCode.add(new Mov(eax, new ConstInt(4), null, null));
                                asmCode.add(new IMul(new RamRegister(Register.ECX, 1), null, null));

                                asmCode.add(new Mov(edx, ecx, null, null));
                                asmCode.add(new Add(edx, new ConstInt(8), null, null));

                                asmCode.add(new Add(eax, edx, null, null));

                                asmCode.add(new Cmp(eax, edx, loopStart, null));
                                asmCode.add(new Je(loopEnd, null, null));
                                {
                                    {
                                        asmCode.add(new Push(eax, null, null));

                                        asmCode.add(new Push(edx, null, null));
                                        asmCode.add(new Call(Values.toString(SystemFunction.PAC + ".unsubscribe", valType), null, null));
                                        asmCode.add(new ShiftEsp(1, null, null));

                                        asmCode.add(new Pop(eax, null, null));
                                    }
                                    asmCode.add(new Add(edx, new ConstInt(4), null, null));
                                    asmCode.add(new Jmp(loopStart, null, null));
                                }
                                asmCode.add(new Nop(loopEnd, null));

                            }
                            asmCode.add(new Add(pointer, new ConstInt(-1), null, null));
                            asmCode.add(new Cmp(pointer, new ConstInt(0), null, null));
                            asmCode.add(new Jne(end, null, null));
                            {
                                asmCode.add(new Push(ecx, null, null));
                                asmCode.add(new CallFree(null, null));
                                asmCode.add(new ShiftEsp(1, null, null));
                            }
                            asmCode.add(new Nop(end, null));
                        }
                        asmCode.add(new Pop(edx, null, null));
                        asmCode.add(new Pop(ecx, null, null));
                        f.add(new SystemFunction(asmCode, void0, "unsubscribe", arrayType));
                    }
                    { // get
                        List<Command> asmCode = new ArrayList<Command>();
                        asmCode.add(new Push(edx, null, null)); // [0] = edx,
                                                                // [1] = eip,
                                                                // [2] = index,
                                                                // [3] = array
                        {
                            asmCode.add(new Mov(eax, new ConstInt(4), null, null));
                            asmCode.add(new IMul(new RamEsp(2), null, null));
                            asmCode.add(new Add(eax, new RamEsp(3), null, null));

                            if (valType.dim == 0) {
                                asmCode.add(new Mov(eax, new RamRegister(Register.EAX, 2), null, null));
                            } else {
                                asmCode.add(new Mov(edx, new RamRegister(Register.EAX, 2), null, null));
                                Variable.subscribe(asmCode, valType, edx);
                                asmCode.add(new Mov(eax, edx, null, null));
                            }
                        }
                        asmCode.add(new Pop(edx, null, null));
                        f.add(new SystemFunction(asmCode, valType, "get", arrayType, int0));
                    }
                    { // set
                        List<Command> asmCode = new ArrayList<Command>();

                        asmCode.add(new Push(edx, null, null)); // [0] = edx,
                                                                // [1] = eip,
                                                                // [2] = val,
                                                                // [3] = index,
                                                                // [4] = array
                        {
                            RWMemory val = new RamEsp(2);
                            RWMemory index = new RamEsp(3);
                            RWMemory array = new RamEsp(4);

                            asmCode.add(new Mov(eax, new ConstInt(4), null, null));
                            asmCode.add(new IMul(index, null, null));
                            asmCode.add(new Add(eax, array, null, null));

                            RWMemory pointer = new RamRegister(Register.EAX, 2);

                            if (valType.dim > 0) {
                                asmCode.add(new Mov(edx, pointer, null, null));
                                asmCode.add(new Push(eax, null, null));
                                { // [0] = eax, [1] = edx, [2] = eip, [3] = val,
                                  // [4] = index, [5] = array
                                    Variable.subscribe(asmCode, valType, new RamEsp(3));
                                    Variable.unsubscribe(asmCode, valType, edx);
                                }
                                asmCode.add(new Pop(eax, null, null));
                            }

                            asmCode.add(new Mov(edx, val, null, null));
                            asmCode.add(new Add(pointer, edx, null, null));

                        }
                        asmCode.add(new Pop(edx, null, null));
                        f.add(new SystemFunction(asmCode, void0, "set", arrayType, int0, valType));
                    }
                    { // new

                        String name = "new" + enumType + dim;

                        List<Command> asmCode = new ArrayList<Command>();

                        asmCode.add(new Push(edx, null, null));
                        asmCode.add(new Push(ecx, null, null));
                        { // [0] = ecx, [1] = edx, [2] = eip, [3] = size
                            RWMemory size = new RamEsp(3);

                            asmCode.add(new Mov(eax, new ConstInt(4), null, null));
                            asmCode.add(new IMul(size, null, null));
                            asmCode.add(new Add(eax, new ConstInt(8), null, null));
                            asmCode.add(new Mov(edx, eax, null, null));

                            asmCode.add(new Push(edx, null, null));
                            asmCode.add(new Push(eax, null, null));
                            asmCode.add(new CallMalloc(null, null));
                            asmCode.add(new ShiftEsp(1, null, null));
                            asmCode.add(new Pop(edx, null, null));

                            asmCode.add(new Mov(new RamRegister(Register.EAX), new ConstInt(1), null, null));
                            asmCode.add(new Mov(ecx, size, null, null));
                            asmCode.add(new Mov(new RamRegister(Register.EAX, 1), ecx, null, null));

                            asmCode.add(new Add(edx, eax, null, null));
                            asmCode.add(new Mov(ecx, eax, null, null));
                            asmCode.add(new Add(ecx, new ConstInt(8), null, null));

                            String loopStart = Label.getTextLabel();
                            String loopEnd = Label.getTextLabel();

                            if (valType.dim > 0) {
                                asmCode.add(new Mov(eax, ConstVariable.NULL, null, null));
                            }

                            asmCode.add(new Cmp(ecx, edx, loopStart, null));
                            asmCode.add(new Je(loopEnd, null, null));
                            {
                                if (valType.dim == 0) {
                                    asmCode.add(new Mov(new RamRegister(Register.ECX), new ConstInt(0), null, null));
                                } else {
                                    asmCode.add(new Mov(new RamRegister(Register.ECX), eax, null, null));
                                }
                                asmCode.add(new Add(ecx, new ConstInt(4), null, null));
                                asmCode.add(new Jmp(loopStart, null, null));
                            }
                            asmCode.add(new Nop(loopEnd, null));

                        }
                        asmCode.add(new Pop(ecx, null, null));
                        asmCode.add(new Pop(edx, null, null));
                        f.add(new SystemFunction(asmCode, arrayType, name.toLowerCase(), int0));
                    }
                } catch (TypeInitException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return f;
    }
}
