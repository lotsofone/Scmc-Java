import java.util.List;

public class GateBlock extends ItemBlock{
    public GateBlock(int[] pos){
        super("df7f00", pos, "9f0f56e8-2c31-4d83-996c-d00a9b296c3f", 1, 1);
        this.controller = new Controller();
        this.controller.setActive(false);
        this.controller.setId(-1);
        this.controller.setMode(0);
        this.controller.setControllers(null);
        this.controller.setJoints(null);
    }

    private Controller controller;
    public void setController(Controller controller) {
        this.controller = controller;
    }
    public Controller getController() {
        return controller;
    }
    /**
     * Auto-generated: 2018-07-05 9:57:20
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
    public static class Controller {

        private boolean active;
        private List<Link> controllers;
        private int id;
        private List<Joint> joints;
        private int mode;
        public static class Link{
            private int id;
            public Link(int id){
                this.id = id;
            }
            public void setId(int id) {
                this.id = id;
            }
            public int getId() {
                return id;
            }
        }
        public static class Joint{
        }
        public void setActive(boolean active) {
            this.active = active;
        }
        public boolean getActive() {
            return active;
        }

        public void setControllers(List<Link> controllers) {
            this.controllers = controllers;
        }
        public List<Link> getControllers() {
            return controllers;
        }

        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public void setJoints(List<Joint> joints) {
            this.joints = joints;
        }
        public List<Joint> getJoints() {
            return joints;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }
        public int getMode() {
            return mode;
        }

    }
}
