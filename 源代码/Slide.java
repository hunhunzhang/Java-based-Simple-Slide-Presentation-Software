import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Slide implements Serializable {
    private List<DrawableObject> objects;
    public Slide(){
        objects=new ArrayList<>();
    }
    public void addObject(DrawableObject obj){
        objects.add(obj);
    }
    public List<DrawableObject> getObjects(){
        return objects;
    }
    public void removeObject(DrawableObject obj){
        objects.remove(obj);
    }
}
