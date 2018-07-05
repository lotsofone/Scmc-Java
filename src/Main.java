import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;

import javax.imageio.ImageIO;

public class Main {
    public static void main(String args[]){
        /*
        GateBlock b = new GateBlock(new int[]{3,5,6});
        b.getController().setControllers(new ArrayList<>());
        b.getController().getControllers().add(new GateBlock.Controller.Link());

        GsonBuilder gb = new GsonBuilder().serializeNulls();
        Gson gson = gb.create();
        String result = gson.toJson(b);
        System.out.println(result);
        */
        //pixel art
        //String result = getImagePixel(new File("inputimage.png"));
        //ram
        String result = generateRAM(new int[]{}, 8);

        System.out.println(result);
        string2Txt(new File("blueprint.json"), result);


    }
    public static String txt2String(File file){
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }
    public static int string2Txt(File file, String string){
        try{
            BufferedWriter br = new BufferedWriter(new FileWriter(file));//构造一个BufferedReader类来读取文件
            br.write(string);
            br.close();
            return 0;
        }catch(Exception e){
            return -1;
        }
    }
    /**
     * 生成内存条
     */
    public static String generateRAM(int[] content, int ByteSize){
        LogicModule d = LogicModule.createDLatch(true);
        List<JsonObject> childObjects = d.toChildObjects();

        JsonObject root = new JsonObject();
        root.add("bodies", new JsonArray());
        JsonArray bodies = root.getAsJsonArray("bodies");
        bodies.add(new JsonObject());
        bodies.get(0).getAsJsonObject().add("childs", new JsonArray());
        JsonArray childs = bodies.get(0).getAsJsonObject().getAsJsonArray("childs");
        for(int i=0; i<childObjects.size(); i++){
            childs.add(childObjects.get(i));
        }
        return new Gson().toJson(root);
        //LogicModule mm = LogicModule.createRamByte(0x00);
    }
    /**
     * 读取一张图片的RGB值并生成blueprint字符串
     */
    public static String getImagePixel(File image) {

        int[] rgba = new int[4];
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(image);
        } catch (IOException e) {

            e.printStackTrace();
        }
        int width = bi.getWidth();
        int height = bi.getHeight();
        int minX = bi.getMinX();
        int minY = bi.getMinY();

        int type = bi.getType();
        System.out.println(type);

        JsonObject root = new JsonObject();
        root.add("bodies", new JsonArray());
        JsonArray bodies = root.getAsJsonArray("bodies");
        bodies.add(new JsonObject());
        bodies.get(0).getAsJsonObject().add("childs", new JsonArray());
        JsonArray childs = bodies.get(0).getAsJsonObject().getAsJsonArray("childs");

        for(int y = minY; y < height; y++) {
            for(int x = minX; x < width; x++) {
                //获取包含这个像素的颜色信息的值, int型
                int pixel = bi.getRGB(x, y);
                //从pixel中获取rgb的值
                rgba = getRGBA(pixel, type);
                String r = Integer.toHexString(rgba[0]);
                String g = Integer.toHexString(rgba[1]);
                String b = Integer.toHexString(rgba[2]);

                String color = (r.length() == 1 ? "0" + r : r)+(g.length() == 1 ? "0" + g : g)+(b.length() == 1 ? "0" + b : b);
                if(rgba[3]>=128){//effect
                    JsonObject block = new Gson().toJsonTree(new BuildingBlock(color, new int[]{x+1,y+1,1}), BuildingBlock.class).getAsJsonObject();
                    childs.add(block);
                }
            }
        }
        System.out.println(root.toString());
        return root.toString();

    }
    public static int[] getRGBA(int color, int type){
        int[] rgba = null;
        switch (type){
            case 5://bgr
                rgba = new int[4];
                rgba[3] = 255; //a
                rgba[0] = (color >> 16) & 0xff; //r
                rgba[1] = (color >> 8) & 0xff; //g
                rgba[2] = (color >> 0) & 0xff; //b
                break;
            case 6://abgr
                rgba = new int[4];
                rgba[0] = (color >> 16) & 0xff; //r
                rgba[1] = (color >> 8) & 0xff; //g
                rgba[2] = (color >> 0) & 0xff; //b
                rgba[3] = (color >> 24) & 0xff; //a
                break;
        }
        //System.out.println(rgba[3]);
        //BufferedImage.
        return rgba;
    }
}
/*
class BlockJsonTemplate{
    public static JsonObject newBlock(int x, int y, int z, String shapeId, String color, int bx, int by, int bz, int xaxis, int zaxis){
        JsonObject block = new JsonObject();
        JsonObject pos = new JsonObject();
        JsonObject bounds;
        return block;
    }
}
*/