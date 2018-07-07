import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.*;

public class LogicModule {
    public String name = "";//模块名
    public List<Gate> gates;//所有逻辑门
    //public List<Gate> inputs;//输入索引
    public List<Gate> outputs;//输出索引
    public Map<String, List<Gate>> inputs;//以名字索引到多个门的输入索引
    public List<int[]> support_blocks;//支撑的建筑方块
    public static class Gate{
        public String name;
        public int mode;
        public boolean active;
        public List<Gate> controllers;
        public int[] pos;
        public int id;
        public Gate(){
            this(0, null);
        }
        Gate(int mode, int[] pos){
            this.controllers = new ArrayList<>();
            this.mode = mode; this.pos = pos; this.active = true;
            this.id = -1;
            this.name = null;
        }
        public void addController(Gate d){
            this.controllers.add(d);
        }
    }
    /*
    public Gate getInputByName(String name){
        for(int i=0; i<this.inputs.size();i++){
            Gate temp = this.inputs.get(i);
            if(temp.name!=null && temp.name.equals(name)){
                return this.inputs.get(i);
            }
        }
        return null;
    }*/
    public Gate addNewGate(int mode, int[] pos){
        Gate newGate = new Gate(mode ,pos);
        this.gates.add(newGate);
        return newGate;
    }
    public List<Gate> registerInput(String name, List<Gate> gates){
        this.inputs.put(name, gates);
        return gates;
    }
    public List<Gate> registerInput(String name, Gate gate){
        List gates = new ArrayList();
        if(gate!=null){
            gates.add(gate);
        }
        this.inputs.put(name, gates);
        return gates;
    }
    public List<Gate> registerInput(String name){
        Gate g = null;
        return registerInput(name, g);
    }
    public void registerOutput(String name, Gate gate){
        this.outputs.add(gate);
        gate.name = name;
    }
    public List<Gate> getInputsByName(String name){
        return this.inputs.get(name);
    }
    public Gate getOutputByName(String name){
        for(int i=0; i<this.outputs.size();i++){
            Gate temp = this.outputs.get(i);
            if(temp.name!=null && temp.name.equals(name)){
                return temp;
            }
        }
        throw new RuntimeException("In logicmodule "+this.name+", the output named \""+name+"\" does not exist");
    }
    public void outputLinkToInput(LogicModule targetModule, String outputName, String inputName){//指定输出连接到另一个模块的输入
        if(!targetModule.inputs.containsKey(inputName)){
            throw new RuntimeException("In logicmodule "+targetModule.name+", the input named \""+inputName+"\" does not exist");
        }
        Gate thisOutput = this.getOutputByName(outputName);
        List<Gate> targetInputs = targetModule.inputs.get(inputName);
        for(int i=0; i<targetInputs.size(); i++){
            thisOutput.addController(targetInputs.get(i));
        }
    }
    public void gateLinkToInput(LogicModule subModule, Gate gate, String inputName){//指定一个门连接到一个子模块的输出
        if(!subModule.inputs.containsKey(inputName)){
            throw new RuntimeException("In logicmodule "+subModule.name+", the input named \""+inputName+"\" does not exist");
        }
        List<Gate> targetInputs = subModule.inputs.get(inputName);
        for(int i=0; i<targetInputs.size(); i++){
            gate.addController(targetInputs.get(i));
        }
    }
    public LogicModule(){
        this.gates = new ArrayList<>();
        //this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.support_blocks = new ArrayList<>();
        this.inputs = new TreeMap<>();
    }

    /**
    * 作品，科学计算器
     * 用户操作进行各种科学计算
    */
    public static LogicModule siencetificCalculator(){
        int bitWidth = 16;
        LogicModule scientificCalculator = new LogicModule();
        scientificCalculator.name = "siencetificCalculator";
        //需要使用的模块--------------------------------------------------------------------------------------
        LogicModule r1,r2,r3,r4;//主寄存器
        LogicModule userInput1, userInput2, userOutput;//用户交互变量
        LogicModule mainAdder;//可控加减计算器
        LogicModule mainBus;//主总线
        LogicModule shiftBus;//移位总线
        LogicModule aluLeftBus,aluRightBus;//ALU总线
        LogicModule controllers;//微指令控制输入口

        LogicModule r1AluL, r2AluL, r3AluL, r4AluL;//控制主寄存器能否输出到ALULeft总线
        LogicModule r1AluR, r2AluR, r3AluR, r4AluR;//...................ALURight总线
        LogicModule r1Shift, r2Shift, r3Shift, r4Shift;//...............移位总线

        LogicModule aluOutputL, aluOutputM, aluOutputR;//ALU计算结果的左中右输出控制
        LogicModule shiftOutputL, shiftOutputM, shiftOutputR;//移位总线左中右输出控制
        LogicModule userInput1Out, userInput2Out;//用户输入变量输出控制
        // 创建模块对象-------------------------------------------------
        {
            //主寄存器
            r1 = LogicModule.createRegister(bitWidth);
            scientificCalculator.gates.addAll(r1.gates);
            r2 = LogicModule.createRegister( bitWidth);
            scientificCalculator.gates.addAll(r2.gates);
            r3 = LogicModule.createRegister( bitWidth);
            scientificCalculator.gates.addAll(r3.gates);
            r4 = LogicModule.createRegister( bitWidth);
            scientificCalculator.gates.addAll(r4.gates);
            //用户输入变量
            userInput1 = LogicModule.createRegister( bitWidth);
            scientificCalculator.gates.addAll(userInput1.gates);
            userInput2 = LogicModule.createRegister( bitWidth);
            scientificCalculator.gates.addAll(userInput2.gates);
            userOutput = LogicModule.createRegister( bitWidth);
            scientificCalculator.gates.addAll(userOutput.gates);
            //可控加减计算器
            mainAdder = LogicModule.createControllableFastAdder(bitWidth);
            scientificCalculator.gates.addAll(mainAdder.gates);

            mainBus = LogicModule.createBus(bitWidth);//主总线
            scientificCalculator.gates.addAll(mainBus.gates);
            shiftBus = LogicModule.createBus(bitWidth);//移位总线
            scientificCalculator.gates.addAll(shiftBus.gates);
            aluLeftBus = LogicModule.createBus(bitWidth);//ALU左总线
            scientificCalculator.gates.addAll(aluLeftBus.gates);
            aluRightBus = LogicModule.createBus(bitWidth);//ALU右总线
            scientificCalculator.gates.addAll(aluRightBus.gates);
            controllers = LogicModule.createBus(32);//微指令控制输入口
            scientificCalculator.gates.addAll(controllers.gates);

            //控制主寄存器能否输出到ALULeft总线
            r1AluL = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(r1AluL.gates);
            r2AluL = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(r2AluL.gates);
            r3AluL = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(r3AluL.gates);
            r4AluL = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(r4AluL.gates);
            //...................ALURight总线
            r1AluR = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(r1AluR.gates);
            r2AluR = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(r2AluR.gates);
            r3AluR = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(r3AluR.gates);
            r4AluR = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(r4AluR.gates);
            //...............移位总线
            r1Shift = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(r1Shift.gates);
            r2Shift = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(r2Shift.gates);
            r3Shift = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(r3Shift.gates);
            r4Shift = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(r4Shift.gates);

            //ALU计算结果的左中右输出控制
            aluOutputL = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(aluOutputL.gates);
            aluOutputM = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(aluOutputM.gates);
            aluOutputR = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(aluOutputR.gates);
            //移位总线左中右输出控制
            shiftOutputL = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(shiftOutputL.gates);
            shiftOutputM = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(shiftOutputM.gates);
            shiftOutputR = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(shiftOutputR.gates);
            //用户输入变量输出控制
            userInput1Out = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(userInput1Out.gates);
            userInput2Out = LogicModule.createOutputController(bitWidth);
            scientificCalculator.gates.addAll(userInput2Out.gates);
        }
        //添加底承载板
        scientificCalculator.support_blocks.add(new int[]{-90, -40, -1, 40, 40, 0});
        scientificCalculator.support_blocks.add(new int[]{-90, -2-bitWidth/2, -1, 40, -1-bitWidth/2, 16});
        //调整模块位置---------------------------------------------------------------------------------------------
        {
            mainAdder.move(-32, -bitWidth/2, 0);//可控加减计算器
            //ALU总线
            aluLeftBus.rotate(3, 1);
            aluRightBus.rotate(3, 1);
            aluLeftBus.move(-36, -bitWidth/2, 1);
            aluRightBus.move(-36, -bitWidth/2, 5);
            //寄存器
            r1.rotate(3, 1);
            r2.rotate(3, 1);
            r3.rotate(3, 1);
            r4.rotate(3, 1);
            r1.move(-48, -bitWidth/2,0);
            r2.move(-50, -bitWidth/2,0);
            r3.move(-52, -bitWidth/2,0);
            r4.move(-54, -bitWidth/2,0);
            //寄存器到ALU总线的输出控制
            r1AluL.rotate(3,1);
            r2AluL.rotate(3,1);
            r3AluL.rotate(3,1);
            r4AluL.rotate(3,1);
            r1AluL.move(-36, -bitWidth/2, 3);
            r2AluL.move(-38, -bitWidth/2, 3);
            r3AluL.move(-40, -bitWidth/2, 3);
            r4AluL.move(-42, -bitWidth/2, 3);
            r1AluR.rotate(3,1);
            r2AluR.rotate(3,1);
            r3AluR.rotate(3,1);
            r4AluR.rotate(3,1);
            r1AluR.move(-36, -bitWidth/2, 7);
            r2AluR.move(-38, -bitWidth/2, 7);
            r3AluR.move(-40, -bitWidth/2, 7);
            r4AluR.move(-42, -bitWidth/2, 7);
            //主总线
            mainBus.rotate(3,1);
            mainBus.move(-64, -bitWidth/2, 1);
            //ALU计算结果的左中右输出控制
            aluOutputL.rotate(3,1);
            aluOutputM.rotate(3,1);
            aluOutputR.rotate(3,1);
            aluOutputL.move(36, -bitWidth/2, 1);
            aluOutputM.move(36, -bitWidth/2, 3);
            aluOutputR.move(36, -bitWidth/2, 5);
            //移位总线
            shiftBus.rotate(3,1);
            shiftBus.move(-64, -bitWidth/2, 15);
            //寄存器向移位总线的输出控制
            r1Shift.rotate(3,1);
            r2Shift.rotate(3,1);
            r3Shift.rotate(3,1);
            r4Shift.rotate(3,1);
            r1Shift.move(-48, -bitWidth/2, 7);
            r2Shift.move(-50, -bitWidth/2, 7);
            r3Shift.move(-52, -bitWidth/2, 7);
            r4Shift.move(-54, -bitWidth/2, 7);
            //移位总线左中右输出控制
            shiftOutputL.rotate(3,1);
            shiftOutputM.rotate(3,1);
            shiftOutputR.rotate(3,1);
            shiftOutputL.move(-62, -bitWidth/2, 11);
            shiftOutputM.move(-64, -bitWidth/2, 11);
            shiftOutputR.move(-66, -bitWidth/2, 11);
            //微指令控制输入口
            controllers.move(-16, -34, 0);
            //用户交互变量
            userInput1.rotate(3,1);
            userInput2.rotate(3,1);
            userOutput.rotate(3,1);
            userInput1.move(-80, -bitWidth/2, 1);
            userInput2.move(-82, -bitWidth/2, 1);
            userOutput.move(-84, -bitWidth/2, 1);
            //用户输入变量输出控制
            userInput1Out.rotate(3, 1);
            userInput2Out.rotate(3, 1);
            userInput1Out.move(-80, -bitWidth/2, 6);
            userInput2Out.move(-82, -bitWidth/2, 6);

        }
        //循环连线，bitWidth次循环里面，需要连线的数据模块做连接------------------------------------------------------------
        for(int i=0; i<bitWidth; i++){
            //主寄存器的输出
            r1.outputLinkToInput(r1AluL, "Q_"+i, "D_"+i);
            r1.outputLinkToInput(r1AluR, "Q_"+i, "D_"+i);
            r1.outputLinkToInput(r1Shift, "Q_"+i, "D_"+i);
            r2.outputLinkToInput(r2AluL, "Q_"+i, "D_"+i);
            r2.outputLinkToInput(r2AluR, "Q_"+i, "D_"+i);
            r2.outputLinkToInput(r2Shift, "Q_"+i, "D_"+i);
            r3.outputLinkToInput(r3AluL, "Q_"+i, "D_"+i);
            r3.outputLinkToInput(r3AluR, "Q_"+i, "D_"+i);
            r3.outputLinkToInput(r3Shift, "Q_"+i, "D_"+i);
            r4.outputLinkToInput(r4AluL, "Q_"+i, "D_"+i);
            r4.outputLinkToInput(r4AluR, "Q_"+i, "D_"+i);
            r4.outputLinkToInput(r4Shift, "Q_"+i, "D_"+i);
            //主寄存器从mainBus输入
            mainBus.outputLinkToInput(r1, "D_"+i, "D_"+i);
            mainBus.outputLinkToInput(r2, "D_"+i, "D_"+i);
            mainBus.outputLinkToInput(r3, "D_"+i, "D_"+i);
            mainBus.outputLinkToInput(r4, "D_"+i, "D_"+i);
            //加减法器的输出控制
            if(i-1>=0)
                mainAdder.outputLinkToInput(aluOutputL, "S_"+i, "D_"+(i-1));
            mainAdder.outputLinkToInput(aluOutputM, "S_"+i, "D_"+i);
            if(i+1<bitWidth)
                mainAdder.outputLinkToInput(aluOutputR, "S_"+i, "D_"+(i+1));
            //加减法器从自己的总线输入
            aluLeftBus.outputLinkToInput(mainAdder, "D_"+i, ("A_"+i));
            aluRightBus.outputLinkToInput(mainAdder, "D_"+i, ("B_"+i));
            //移位总线
            r1Shift.outputLinkToInput(shiftBus, "D_"+i, "D_"+i);
            r2Shift.outputLinkToInput(shiftBus, "D_"+i, "D_"+i);
            r3Shift.outputLinkToInput(shiftBus, "D_"+i, "D_"+i);
            r4Shift.outputLinkToInput(shiftBus, "D_"+i, "D_"+i);

            if(i-1>=0)
                shiftBus.outputLinkToInput(shiftOutputL, "D_"+i, ("D_"+(i-1)));
            shiftBus.outputLinkToInput(shiftOutputM, "D_"+i, ("D_"+i));
            if(i+1<bitWidth)
                shiftBus.outputLinkToInput(shiftOutputR, "D_"+i, ("D_"+(i+1)));
            //ALU总线从寄存器的输出控制器接收输出
            r1AluL.outputLinkToInput(aluLeftBus, "D_"+i, "D_"+i);
            r2AluL.outputLinkToInput(aluLeftBus, "D_"+i, "D_"+i);
            r3AluL.outputLinkToInput(aluLeftBus, "D_"+i, "D_"+i);
            r4AluL.outputLinkToInput(aluLeftBus, "D_"+i, "D_"+i);
            r1AluR.outputLinkToInput(aluRightBus, "D_"+i, "D_"+i);
            r2AluR.outputLinkToInput(aluRightBus, "D_"+i, "D_"+i);
            r3AluR.outputLinkToInput(aluRightBus, "D_"+i, "D_"+i);
            r4AluR.outputLinkToInput(aluRightBus, "D_"+i, "D_"+i);
            //主总线接收的输入
            aluOutputL.outputLinkToInput(mainBus, "D_"+i, "D_"+i);
            aluOutputM.outputLinkToInput(mainBus, "D_"+i, "D_"+i);
            aluOutputR.outputLinkToInput(mainBus, "D_"+i, "D_"+i);
            shiftOutputL.outputLinkToInput(mainBus, "D_"+i, "D_"+i);
            shiftOutputM.outputLinkToInput(mainBus, "D_"+i, "D_"+i);
            shiftOutputR.outputLinkToInput(mainBus, "D_"+i, "D_"+i);
            userInput1Out.outputLinkToInput(mainBus, "D_"+i, "D_"+i);
            userInput2Out.outputLinkToInput(mainBus, "D_"+i, "D_"+i);
            //总线输出到用户
            mainBus.outputLinkToInput(userOutput, "D_"+i, ("D_"+i));
            //用户输入
            userInput1.outputLinkToInput(userInput1Out, "Q_"+i, ("D_"+i));
            userInput2.outputLinkToInput(userInput2Out, "Q_"+i, ("D_"+i));
        }
        //对需要由微指令控制的输出做连线------------------------------------------------------------
        //主寄存器的输出控制
        controllers.outputLinkToInput(r1AluL, "D_"+0, "C");
        controllers.outputLinkToInput(r2AluL, "D_"+1, "C");
        controllers.outputLinkToInput(r3AluL, "D_"+2, "C");
        controllers.outputLinkToInput(r4AluL, "D_"+3, "C");
        controllers.outputLinkToInput(r1AluR, "D_"+4, "C");
        controllers.outputLinkToInput(r2AluR, "D_"+5, "C");
        controllers.outputLinkToInput(r3AluR, "D_"+6, "C");
        controllers.outputLinkToInput(r4AluR, "D_"+7, "C");
        controllers.outputLinkToInput(r1Shift, "D_"+8, "C");
        controllers.outputLinkToInput(r2Shift, "D_"+9, "C");
        controllers.outputLinkToInput(r3Shift, "D_"+10, "C");
        controllers.outputLinkToInput(r4Shift, "D_"+11, "C");
        //主寄存器输入控制
        controllers.outputLinkToInput(r1, "D_"+12, "C");
        controllers.outputLinkToInput(r2, "D_"+13, "C");
        controllers.outputLinkToInput(r3, "D_"+14, "C");
        controllers.outputLinkToInput(r4, "D_"+15, "C");
        //ALU计算结果的左中右输出控制
        controllers.outputLinkToInput(aluOutputL, "D_"+16, "C");
        controllers.outputLinkToInput(aluOutputM, "D_"+17, "C");
        controllers.outputLinkToInput(aluOutputR, "D_"+18, "C");
        //移位总线输出控制
        controllers.outputLinkToInput(shiftOutputL, "D_"+20, "C");
        controllers.outputLinkToInput(shiftOutputM, "D_"+21, "C");
        controllers.outputLinkToInput(shiftOutputR, "D_"+22, "C");
        //用户控制输入
        controllers.outputLinkToInput(userInput1Out, "D_"+28, "C");
        controllers.outputLinkToInput(userInput2Out, "D_"+29, "C");
        //用户输出
        controllers.outputLinkToInput(userOutput, "D_"+30, "C");
        //同步时钟
        controllers.outputLinkToInput(r1, "D_"+31, "CLK");
        controllers.outputLinkToInput(r2, "D_"+31, "CLK");
        controllers.outputLinkToInput(r3, "D_"+31, "CLK");
        controllers.outputLinkToInput(r4, "D_"+31, "CLK");
        controllers.outputLinkToInput(userOutput, "D_"+31, "CLK");
        /*
        for(int i=0; i<bitWidth; i++){
            //主寄存器的输出控制
            controllers.getOutputByName("D_"+0).addController(r1AluL.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+1).addController(r2AluL.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+2).addController(r3AluL.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+3).addController(r4AluL.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+4).addController(r1AluR.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+5).addController(r2AluR.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+6).addController(r3AluR.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+7).addController(r4AluR.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+8).addController(r1Shift.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+9).addController(r2Shift.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+10).addController(r3Shift.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+11).addController(r4Shift.getInputByName("D_"+i));
            //主寄存器输入控制
            controllers.getOutputByName("D_"+12).addController(r1.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+13).addController(r2.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+14).addController(r3.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+15).addController(r4.getInputByName("D_"+i));
            //ALU计算结果的左中右输出控制
            controllers.getOutputByName("D_"+16).addController(aluOutputL.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+17).addController(aluOutputM.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+18).addController(aluOutputR.getInputByName("D_"+i));
            //移位总线输出控制
            controllers.getOutputByName("D_"+20).addController(shiftOutputL.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+21).addController(shiftOutputM.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+22).addController(shiftOutputR.getInputByName("D_"+i));
            //用户控制输入
            controllers.getOutputByName("D_"+28).addController(userInput1Out.getInputByName("D_"+i));
            controllers.getOutputByName("D_"+29).addController(userInput2Out.getInputByName("D_"+i));
            //用户输出
            controllers.getOutputByName("D_"+30).addController(userOutput.getInputByName("D_"+i));
            //同步时钟
            controllers.getOutputByName("D_"+31).addController(r1.getInputByName("D"));
        }
        */
            /*
            剩余
            LogicModule controllers;//微指令控制输入口
            */

        return scientificCalculator;
    }

    /**
     * 生成一条总线
     */
    public static LogicModule createBus(int size){
        //总线，由一排OR门构成。输入为Dn-1~D0，输出为Dn-1~D0，占用空间[0,0,0],[n,1,1]
        LogicModule bus = new LogicModule();
        for(int i=0; i<size; i++){
            Gate Bn = bus.addNewGate(1, new int[]{i, 0, 0});
            Bn.name = "D_"+i;
            bus.registerInput(Bn.name, Bn);
            bus.outputs.add(Bn);
        }
        bus.name = "Bus "+size+" bits";
        return bus;
    }
    public static LogicModule createOutputController(int size){
        //输出控制器，由一排与门构成。输入为Cn-1~C0，输出为Cn-1~C0，占用空间[0,0,0],[n,1,1]
        //功能是控制某个数据能否输出到总线上
        LogicModule bus = new LogicModule();
        List<Gate> con = bus.registerInput("C");
        for(int i=0; i<size; i++){
            Gate Cn = bus.addNewGate(0, new int[]{i, 0, 0});
            bus.registerInput("D_"+i, Cn);
            bus.registerOutput("D_"+i, Cn);

            con.add(Cn);
        }
        bus.name = "OutputController "+size+" bits";
        return bus;
    }
    //生成D锁存器单元
    public static LogicModule createDLatch(){
        /**
         * D锁存器，输入端D，C，S，R，输出端Q，填满空间[0,0,0],[1,1,4]。
         * D是输入数据，C是锁存控制，为0时锁死，为1时放通。D，C不可悬空
         * S和R可以用于载入后设置初始值，S置1，R置0，可悬空。悬空或不悬空都不浪费逻辑门
         */
        //
        // 请将C连到DC和RC上，D连到D上，不可悬空
        // Q和RC可用于从蓝图加载后设置初值，Q输入用于置1，可悬空；RC输入接R可用于置0，可悬空。
        LogicModule logicModule = new LogicModule();
        Gate q = new Gate(1, new int[]{0, 0, 3});
        Gate delay= new Gate(3, new int[]{0, 0, 2});
        Gate keepNor= new Gate(4, new int[]{0, 0, 1});
        Gate dAnd= new Gate(0, new int[]{0, 0, 0});
        q.addController(delay); delay.addController(keepNor); keepNor.addController(q);
        dAnd.addController(q);

        logicModule.gates.add(q);logicModule.gates.add(delay);logicModule.gates.add(keepNor);
        logicModule.gates.add(dAnd);
        //输入端口 D,C,R,S,Q
        logicModule.registerInput("D", dAnd);
        List<Gate> inputC = logicModule.registerInput("C");
        inputC.add(dAnd); inputC.add(keepNor);
        logicModule.registerInput("S", q);
        logicModule.registerInput("R", keepNor);
        //输出端口 Q
        logicModule.outputs.add(q); q.name = "Q";
        logicModule.name = "DLatch";
        return logicModule;
    }
    /**
     * 生成一个寄存器
     * 输入端Dn-1~D0，C，CLK，输出端Qn-1~Q0，占用空间[0,0,0],[n,1,4]
     * Dn是输入数据。CLK上升沿时写入，其他情况锁定。Qn为数据输出
     * C是写入控制，为1时才能写入。这个接口可以接多条线，几个输入同时为1才会写入，也可以悬空。
     */
    public static LogicModule createRegister(int size){
        LogicModule logicModule = new LogicModule();
        Gate[][] QCDs = new Gate[size][3];
        Gate notC = logicModule.addNewGate(4, new int[]{0, 0, 0});
        //输入
        List<Gate> CLK = logicModule.registerInput("CLK", notC);
        List<Gate> C = logicModule.registerInput("C");
        for(int i=0; i<size; i++){
            QCDs[i][0] = logicModule.addNewGate(2, new int[]{i, 0, 3});//Q
            QCDs[i][1] = logicModule.addNewGate(0, new int[]{i, 0, 2});//C
            QCDs[i][2] = logicModule.addNewGate(2, new int[]{i, 0, 1});//D
            //内连线
            QCDs[i][0].addController(QCDs[i][0]);
            QCDs[i][0].addController(QCDs[i][2]);
            QCDs[i][2].addController(QCDs[i][1]);
            QCDs[i][1].addController(QCDs[i][0]);
            notC.addController(QCDs[i][1]);
            //输出
            logicModule.registerOutput("Q_"+i, QCDs[i][0]);
            //输入
            logicModule.registerInput("D_"+i, QCDs[i][2]);
            CLK.add(QCDs[i][1]);
            C.add(QCDs[i][1]);
        }
        logicModule.name = "Register "+size+" bits";
        return logicModule;
    }

    /**
     * 生成一个RAM字节
     * 输入端Dn-1~D0，Write，Reset，输出端Qn-1~Q0，占用空间[0,0,0],[n,1,5]
     * Dn是输入数据。Write为1时写入，为0时锁定。Qn为数据输出
     * C是写入控制，为1时才能写入，即C为1且Write为1时写入。这个接口可以接多条线，几个输入同时为1才会写入，也可以悬空。
     * Reset用于从蓝图载入后设置初值为content，可悬空。
     */
    public static LogicModule createRamByte(int content, int byteSize){
        LogicModule logicModule = new LogicModule();
        Gate write = new Gate(0, new int[]{0,0,0});
        logicModule.gates.add(write);
        Gate reset = logicModule.addNewGate(0, new int[]{1, 0, 0});
        logicModule.registerInput("Reset", reset);

        //添加锁存器
        int[] offset = new int[]{0,0,1};
        for(int i=0; i<byteSize; i++){
            boolean active = true;
            LogicModule dLatch = LogicModule.createDLatch();
            dLatch.move(offset); offset[0]++;
            //输入端Dn
            logicModule.registerInput("D_"+i, dLatch.getInputsByName("D"));
            //输出端Qn
            logicModule.registerOutput("Q_"+i, dLatch.getOutputByName("Q"));
            //write输入
            logicModule.gateLinkToInput(dLatch, write, "D");
            logicModule.gateLinkToInput(dLatch, write, "C");
            //R和S
            logicModule.gates.addAll(dLatch.gates);
            if((content & 0x01)!=0){
                logicModule.gateLinkToInput(dLatch, reset, "S");
            }
            else{
                logicModule.gateLinkToInput(dLatch, reset, "R");
            }
            content>>=1;

        }
        logicModule.registerInput("Write", write);
        logicModule.registerInput("C", write);
        logicModule.name = "RamByte "+byteSize+" bits";
        return logicModule;
    }
    public static LogicModule createRamByte(int content){
        return createRamByte(content, 8);
    }
    /*public static LogicModule createRAM(int[] content, int size, int byteSize){

    }*/
    /**
     * 生成可控加减法器
     * 输入An-1~A0, Bn-1~B0, Cin, AC, BC, 输出Sn-1~S0, Cout Gout Pout
     * AC控制A是否输入，为1时输入A，为0时输入0，可用作条件控制加或者不加
     * BC控制是否对B和Cin取反，可用作条件控制加或者减。1为加，0为减
     * Cin Cout为进位输入和输出
     * Gout Pout为总产生式输出，可用作分组超前进位加法器的级联模块
     * fitbox为[1, 0, 0], [size+3, size, 6]
     */
    public static LogicModule createControllableFastAdder(int size){
        LogicModule logicModule = new LogicModule();
        //临时用数组来存门
        Gate[][] GPSs = new Gate[size][];
        for(int i=0; i<size; i++){
            //建立G门，P门，S门
            GPSs[i] = new Gate[5];
            GPSs[i][0] = logicModule.addNewGate(0, new int[]{1,i,1});
            GPSs[i][1] = logicModule.addNewGate(1, new int[]{2,i,1});
            GPSs[i][2] = logicModule.addNewGate(2, new int[]{3,i,1});
            //S输出
            GPSs[i][2].name = "S_"+i; logicModule.outputs.add(GPSs[i][2]);
            //A B 输入
            GPSs[i][3] = logicModule.addNewGate(0, new int[]{0,i,1});//AND
            GPSs[i][4] = logicModule.addNewGate(5, new int[]{0,i,2});//NXOR
            logicModule.registerInput("A_"+i, GPSs[i][3]);
            logicModule.registerInput("B_"+i, GPSs[i][4]);
            //AB输入连线
            GPSs[i][3].addController(GPSs[i][0]);
            GPSs[i][3].addController(GPSs[i][1]);
            GPSs[i][3].addController(GPSs[i][2]);
            GPSs[i][4].addController(GPSs[i][0]);
            GPSs[i][4].addController(GPSs[i][1]);
            GPSs[i][4].addController(GPSs[i][2]);
        }
        Gate cin = logicModule.addNewGate(2, new int[]{0, 0, 3});
        //输出门
        Gate gout,pout,cout;
        cout = logicModule.addNewGate(1, new int[]{0, size-1, 3});
        gout = logicModule.addNewGate(1, new int[]{0, size-1, 5});
        pout = logicModule.addNewGate(0, new int[]{0, size-1, 4});
        //生成门阵列
        cin.addController(GPSs[0][2]);//c0直接接cin
        for(int i=1; i<size; i++){//生成c1到c7
            Gate ci = logicModule.addNewGate(1, new int[]{0, size-i, 0});
            ci.addController(GPSs[i][2]);
            for(int j=0; j<=i; j++)//从P(i-1)...P(0)*Cin到G(i-1)
            {
                Gate jj = logicModule.addNewGate(0, new int[]{1+j, size-i, 0});
                jj.addController(ci);
                for(int k=i-1; k>=j; k--){//P(i-1)....P(j)
                    GPSs[k][1].addController(jj);
                }
                if(j>0){//G(j-1)
                    GPSs[j-1][0].addController(jj);
                }
                else{//cin
                    cin.addController(jj);
                }
            }
        }
        //标记输入输出
        logicModule.registerInput("Cin, cin");
        cout.name =  "Cout"; logicModule.outputs.add(cout);
        gout.name = "Gout"; logicModule.outputs.add(gout);
        pout.name = "Pout"; logicModule.outputs.add(pout);
        //创建输出逻辑
        {//给cout和gout创建
            int i=size;
            for(int j=0; j<=i; j++)//从P(i-1)...P(0)*Cin到G(i-1)
            {
                Gate jj = logicModule.addNewGate(0, new int[]{1+j, size-i, 0});
                for(int k=i-1; k>=j; k--){//P(i-1)....P(j)
                    GPSs[k][1].addController(jj);
                }
                if(j>0){//G(j-1)
                    GPSs[j-1][0].addController(jj);
                    jj.addController(gout);
                }
                else{//cin
                    cin.addController(jj);
                }
                jj.addController(cout);
            }
        }
        {//给pout创建
            int i=size;
            {//从P(i-1)...P(0)
                int j=0;
                Gate jj = logicModule.addNewGate(0, new int[]{0, size-i, 0});
                for(int k=i-1; k>=j; k--){//P(i-1)....P(j)
                    GPSs[k][1].addController(jj);
                }
                jj.addController(pout);
            }
        }
        logicModule.name = "ControllableFastAdder "+size+" bits";
        return logicModule;
    }
    public void move(int x, int y, int z){
        for(int i=0; i<this.gates.size();i++) {
            Gate gate = this.gates.get(i);
            gate.pos[0]+=x; gate.pos[1]+=y; gate.pos[2]+=z;
        }
    }
    public void move(int[] offset){
        for(int i=0; i<this.gates.size();i++) {
            Gate gate = this.gates.get(i);
            gate.pos[0]+=offset[0]; gate.pos[1]+=offset[1]; gate.pos[2]+=offset[2];
        }
    }
    private static int[] rotate_transform(int[] pos, int axis, int step){
        switch (axis){
            case 1:
                System.out.println("this rotation not supported");
                return pos;
            case 2:
                System.out.println("this rotation not supported");
                return pos;
            case 3:
                int x = pos[0]; int y = pos[1];
                switch (step){
                    case 1:
                        pos[0] = -y; pos[1] = x;
                        return pos;
                    case 2:
                        pos[0] = -x; pos[1] = -y;
                        return pos;
                    case 3:
                        pos[0] = y; pos[1] = -x;
                        return pos;
                }
        }
        new RuntimeException("逻辑错误，axis="+axis+" step="+step).printStackTrace();
        return pos;
    }
    public void rotate(int axis, int step){
        if(step<=0||step>3)return;
        if(axis<-3||axis>3||axis==0)return;
        if(axis<0){
            axis = -axis; step = 4-step;
        }
        //gates
        for(int i=0; i<gates.size(); i++){
            Gate g = gates.get(i);
            rotate_transform(g.pos, axis, step);
        }
        //supporters
        for(int i=0; i<support_blocks.size(); i++){
            int[] sbi = support_blocks.get(i);
            int[] p1 = rotate_transform(Arrays.copyOfRange(sbi,0,3), axis, step);
            int[] p2 = rotate_transform(Arrays.copyOfRange(sbi,3,6), axis, step);
            for(int j=0; j<2; j++){
                if(p1[j]>p2[j]){
                    int temp = p1[j]; p1[j]=p2[j]; p2[j]=temp;
                }
                sbi[j] = p1[j];
                sbi[j+3] = p2[j];
            }
        }
    }
    public List<JsonObject> toChildObjects(){
        List<JsonObject> childObjects = new ArrayList<>();
        //设置gson
        GsonBuilder gb = new GsonBuilder().serializeNulls();
        Gson gson = gb.create();
        //先分配id
        for(int i=0; i<gates.size(); i++){
            gates.get(i).id=i+1;
        }
        //生成
        for(int i=0; i<gates.size(); i++){
            Gate gate = gates.get(i);
            GateBlock gateBlock = new GateBlock(gate.pos);
            gateBlock.setController(new GateBlock.Controller());
            GateBlock.Controller controller = gateBlock.getController();
            controller.setActive(gate.active);
            controller.setId(gate.id);
            controller.setMode(gate.mode);
            controller.setJoints(null);
            if(gate.controllers.size()==0){
                controller.setControllers(null);
            }
            else{
                controller.setControllers(new ArrayList<>());
                List<GateBlock.Controller.Link> controllers = controller.getControllers();
                for(int j=0; j<gate.controllers.size(); j++){
                    controllers.add(new GateBlock.Controller.Link(gate.controllers.get(j).id));
                }
            }
            childObjects.add(gson.toJsonTree(gateBlock).getAsJsonObject());
        }
        //承载板
        for(int i=0; i<support_blocks.size(); i++){
            int[] suppoter = support_blocks.get(i);
            BuildingBlock block = new BuildingBlock(
                    new int[]{suppoter[3]-suppoter[0], suppoter[4]-suppoter[1], suppoter[5]-suppoter[2]},
                    new int[]{suppoter[0], suppoter[1], suppoter[2]});
            childObjects.add(gson.toJsonTree(block).getAsJsonObject());
        }

        return childObjects;
    }

}
