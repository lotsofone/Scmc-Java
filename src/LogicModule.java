import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class LogicModule {
    public List<Gate> gates;
    public List<Gate> inputs;
    public List<Gate> outputs;
    public static class Gate{
        public String name;
        public int mode;
        public boolean active;
        public List<Gate> controllers;
        public int[] pos;
        public int id;
        public Gate(){
            this(0, null, false);
        }
        Gate(int mode, int[] pos, boolean active){
            this.controllers = new ArrayList<>();
            this.mode = mode; this.pos = pos; this.active = active;
            this.id = -1;
        }
        public void addController(Gate d){
            this.controllers.add(d);
        }
    }
    public Gate getInputByName(String name){
        for(int i=0; i<this.inputs.size();i++){
            if(this.inputs.get(i).name==name){
                return this.inputs.get(i);
            }
        }
        return null;
    }
    public Gate getOutputByName(String name){
        for(int i=0; i<this.outputs.size();i++){
            if(this.outputs.get(i).name==name){
                return this.outputs.get(i);
            }
        }
        return null;
    }
    public Gate addNewGate(int mode, int[] pos, boolean active){
        Gate newGate = new Gate(mode ,pos, active);
        this.gates.add(newGate);
        return newGate;
    }
    public LogicModule(){
        this.gates = new ArrayList<>();
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
    }
    //生成D锁存器单元
    public static LogicModule createDLatch(boolean active){
        // D锁存器，输入端DC，C，输出端Q，填满空间[1,1,1],[2,2,5]。通过指定active设置存储内容为1或0
        // 请将C连到DC和C上，D连到D上。
        LogicModule logicModule = new LogicModule();
        Gate q = new Gate(1, new int[]{1, 1, 4}, active);
        Gate delay= new Gate(4, new int[]{1, 1, 3}, !active);
        Gate keepNor= new Gate(4, new int[]{1, 1, 2}, active);
        Gate dAnd= new Gate(0, new int[]{1, 1, 1}, false);
        q.addController(delay); delay.addController(keepNor); keepNor.addController(q);
        logicModule.gates.add(q);logicModule.gates.add(delay);logicModule.gates.add(keepNor);
        logicModule.gates.add(dAnd);
        //输入端口 DC,C
        logicModule.inputs.add(dAnd); dAnd.name = "DC";
        logicModule.inputs.add(keepNor); dAnd.name = "C";
        //输出端口 Q
        logicModule.outputs.add(q); q.name = "Q";
        return logicModule;
    }

    //生成一个RAM字节
    public static LogicModule createRamByte(int content, int byteSize){
        // 内存字节单元，输入端Dn-1~D0，Write，输出端Qn-1~Q0，占用空间[1,1,1],[n+1,2,6]。通过指定content设置存储内容
        // 修改byteSize可获得不同的字长 通过Write来控制所存，Write为1时为直通，为0时则保留
        LogicModule logicModule = new LogicModule();
        Gate write = new Gate(0, new int[]{1,1,1}, false);
        logicModule.gates.add(write);

        //添加锁存器
        int[] offset = new int[]{byteSize-1,0,1};
        for(int i=0; i<byteSize; i++){
            boolean active = ((content & 0x01)!=0);
            content >>= 1;
            LogicModule dLatch = LogicModule.createDLatch(active);
            dLatch.move(offset); offset[0]--;
            //输入端Dn
            Gate Dn = dLatch.getInputByName("DC");
            Dn.name = "D"+(byteSize-i-1);
            logicModule.inputs.add(Dn);
            //输入端Qn
            Gate Qn = dLatch.getOutputByName("Q");
            Qn.name = "Q"+(byteSize-i-1);
            logicModule.outputs.add(Qn);
            //D锁存器输入端C
            Gate Cn = dLatch.getInputByName("C");
            write.addController(Cn);
            write.addController(Dn);
            logicModule.gates.addAll(dLatch.gates);
            /*//debug 用特制输出线输入线
            Gate debugDn = new Gate(0, offset, false);
            debugDn.addController(Dn);*/

        }
        write.name = "Write";
        logicModule.outputs.add(write);
        return logicModule;
    }
    public static LogicModule createRamByte(int content){
        return createRamByte(content, 8);
    }
    /*public static LogicModule createRAM(int[] content, int size, int byteSize){

    }*/
    public static LogicModule createFastAdder(int size){
        LogicModule logicModule = new LogicModule();
        //临时用数组来存门
        Gate[][] GPSs = new Gate[size][];
        for(int i=0; i<size; i++){
            //建立G门，P门，S门
            GPSs[i] = new Gate[5];
            GPSs[i][0] = logicModule.addNewGate(0, new int[]{2,i,1}, false);
            GPSs[i][1] = logicModule.addNewGate(1, new int[]{3,i,1}, false);
            GPSs[i][2] = logicModule.addNewGate(2, new int[]{4,i,1}, false);
            //S输出
            GPSs[i][2].name = "S"+i; logicModule.outputs.add(GPSs[i][2]);
            //A B 输入
            GPSs[i][3] = logicModule.addNewGate(0, new int[]{1,i,1}, false);
            GPSs[i][4] = logicModule.addNewGate(0, new int[]{1,i,2}, false);
            GPSs[i][3].name = "A"+i;
            GPSs[i][4].name = "B"+i;
            logicModule.inputs.add(GPSs[i][3]);
            logicModule.inputs.add(GPSs[i][4]);
            //AB输入连线
            GPSs[i][3].addController(GPSs[i][0]);
            GPSs[i][3].addController(GPSs[i][1]);
            GPSs[i][3].addController(GPSs[i][2]);
            GPSs[i][4].addController(GPSs[i][0]);
            GPSs[i][4].addController(GPSs[i][1]);
            GPSs[i][4].addController(GPSs[i][2]);
        }
        Gate cin = logicModule.addNewGate(0, new int[]{1, 0, 3}, false);
        //输出门
        Gate gout,pout,cout;
        cout = logicModule.addNewGate(1, new int[]{1, size-1, 3}, false);
        gout = logicModule.addNewGate(1, new int[]{1, size-1, 5}, false);
        pout = logicModule.addNewGate(0, new int[]{1, size-1, 4}, false);
        //生成门阵列
        cin.addController(GPSs[0][2]);//c0直接接cin
        for(int i=1; i<size; i++){//生成c1到c7
            Gate ci = logicModule.addNewGate(1, new int[]{1, size-1, 0},false);
            ci.addController(GPSs[i][2]);
            for(int j=0; j<=i; j++)//从P(i-1)...P(0)*Cin到G(i-1)
            {
                Gate jj = logicModule.addNewGate(0, new int[]{1+1+j, size-1, 0}, false);
                jj.addController(ci);
                for(int k=i-1; k>=j; k++){//P(i-1)....P(j)
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
        cin.name = "Cin"; logicModule.inputs.add(cin);
        cout.name =  "Cout"; logicModule.outputs.add(cout);
        gout.name = "Gout"; logicModule.outputs.add(gout);
        pout.name = "Pout"; logicModule.outputs.add(pout);
        return logicModule;
    }
    public void move(int[] offset){
        for(int i=0; i<this.gates.size();i++) {
            Gate gate = this.gates.get(i);
            gate.pos[0]+=offset[0]; gate.pos[1]+=offset[1]; gate.pos[2]+=offset[2];
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
        return childObjects;
    }

}
