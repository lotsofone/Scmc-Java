/**
 * Auto-generated: 2018-07-04 23:7:38
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class BuildingBlock {
    public BuildingBlock(int[] bounds, String shapeId, String color, int[] pos, int xaxis, int zaxis){
        this.bounds = new Bounds();
        this.bounds.setX(bounds[0]);
        this.bounds.setY(bounds[1]);
        this.bounds.setZ(bounds[2]);
        this.shapeId = shapeId;
        this.color = color;
        this.pos = new Pos();
        this.pos.setX(pos[0]);
        this.pos.setY(pos[1]);
        this.pos.setZ(pos[2]);
        this.xaxis = xaxis;
        this.zaxis = zaxis;
    }
    public BuildingBlock(String color, int[] pos){
        this(new int[]{1,1,1}, "a6c6ce30-dd47-4587-b475-085d55c6a3b4", color, pos, 1, 3);
    }
    private Bounds bounds;
    private String color;
    private Pos pos;
    private String shapeId;
    private int xaxis;
    private int zaxis;
    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }
    public Bounds getBounds() {
        return bounds;
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

}/**
 * Auto-generated: 2018-07-04 23:7:38
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
class Bounds {

    private int x;
    private int y;
    private int z;
    public void setX(int x) {
        this.x = x;
    }
    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }
    public int getY() {
        return y;
    }

    public void setZ(int z) {
        this.z = z;
    }
    public int getZ() {
        return z;
    }

}
/**
 * Auto-generated: 2018-07-04 23:7:38
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
class Pos {

    private int x;
    private int y;
    private int z;
    public void setX(int x) {
        this.x = x;
    }
    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }
    public int getY() {
        return y;
    }

    public void setZ(int z) {
        this.z = z;
    }
    public int getZ() {
        return z;
    }

}