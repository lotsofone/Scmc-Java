/**
 * Auto-generated: 2018-07-05 9:38:7
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class ItemBlock {

    private String color;
    private Pos pos;
    private String shapeId;
    private int xaxis;
    private int zaxis;
    public ItemBlock(String color, int[] pos, String shapeId, int xaxis, int zaxis){
        this.color = color;
        this.pos = new Pos();
        this.pos.setX(pos[0]);
        this.pos.setY(pos[1]);
        this.pos.setZ(pos[2]);
        this.shapeId = shapeId;
        this.xaxis = xaxis;
        this.zaxis = zaxis;
    }
    public ItemBlock(int[] pos, String shapeId){
        this("f3871c", pos, shapeId, 1, 1);
    }
    public void setColor(String color) {
        this.color = color;
    }
    public String getColor() {
        return color;
    }

    public void setPos(Pos pos) {
        this.pos = pos;
    }
    public Pos getPos() {
        return pos;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }
    public String getShapeId() {
        return shapeId;
    }

    public void setXaxis(int xaxis) {
        this.xaxis = xaxis;
    }
    public int getXaxis() {
        return xaxis;
    }

    public void setZaxis(int zaxis) {
        this.zaxis = zaxis;
    }
    public int getZaxis() {
        return zaxis;
    }
}
